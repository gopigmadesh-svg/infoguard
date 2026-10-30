package com.example.myapplication.data.network;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.myapplication.data.network.client.NetworkConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * TruthLens Java Helper for uploading profile pictures to the PHP backend
 * using multipart/form-data with OkHttp.
 */
public class ProfilePictureUploader {

    private static final String TAG = "ProfilePictureUploader";

    // SharedPreferences keys for persistent profile picture caching
    private static final String PREF_NAME = "truthlens_user_session";
    private static final String KEY_PROFILE_PICTURE_URL = "saved_profile_picture_url";

    // Default base URL for physical device / emulator.
    public static final String DEFAULT_UPLOAD_URL = "http://127.0.0.1/Truthlense_backened/upload_profile_picture.php";

    public static String getDefaultUploadUrl() {
        try {
            return NetworkConfig.INSTANCE.getCurrentBaseUrl() + "upload_profile_picture.php";
        } catch (Throwable t) {
            return DEFAULT_UPLOAD_URL;
        }
    }

    public interface UploadCallback {
        void onProgress(int progressPercent);
        void onSuccess(String profilePictureUrl);
        void onError(String errorMessage);
    }

    /**
     * Upload an image URI to the PHP backend using multipart/form-data.
     *
     * @param context Application/Activity context
     * @param imageUri Selected image Uri from Photo Picker
     * @param userId Authenticated user ID (or <= 0 to use email)
     * @param email Authenticated user email
     * @param callback Result callback executed on Android Main Thread
     */
    public static void uploadProfilePicture(
            Context context,
            Uri imageUri,
            int userId,
            String email,
            UploadCallback callback
    ) {
        uploadProfilePicture(context, getDefaultUploadUrl(), imageUri, userId, email, callback);
    }

    /**
     * Upload an image URI with custom endpoint URL.
     */
    public static void uploadProfilePicture(
            Context context,
            String uploadUrl,
            Uri imageUri,
            int userId,
            String email,
            UploadCallback callback
    ) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        if (imageUri == null) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("No image selected."));
            }
            return;
        }

        ContentResolver contentResolver = context.getContentResolver();

        // 1. Determine MIME type and file name
        String mimeType = contentResolver.getType(imageUri);
        if (mimeType == null || !mimeType.startsWith("image/")) {
            mimeType = "image/jpeg";
        }

        String fileName = queryFileName(contentResolver, imageUri);
        if (fileName == null || fileName.isEmpty()) {
            fileName = "profile_image_" + System.currentTimeMillis() + ".jpg";
        }

        // 2. Read bytes into memory with size validation (5 MB limit)
        byte[] fileBytes;
        try {
            fileBytes = readBytesFromUri(contentResolver, imageUri);
        } catch (IOException e) {
            Log.e(TAG, "Error reading image bytes", e);
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Failed to read image file: " + e.getMessage()));
            }
            return;
        }

        if (fileBytes == null || fileBytes.length == 0) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Image file is empty."));
            }
            return;
        }

        long maxSizeBytes = 5 * 1024 * 1024; // 5 MB
        if (fileBytes.length > maxSizeBytes) {
            if (callback != null) {
                mainHandler.post(() -> callback.onError("Image file exceeds maximum limit of 5 MB."));
            }
            return;
        }

        // 3. Build Multipart request body
        MediaType mediaType = MediaType.parse(mimeType);
        RequestBody fileRequestBody = RequestBody.create(mediaType, fileBytes);

        // Wrap with progress reporting
        RequestBody progressRequestBody = new ProgressRequestBody(fileRequestBody, (bytesWritten, contentLength) -> {
            int progress = (int) (100 * bytesWritten / contentLength);
            if (callback != null) {
                mainHandler.post(() -> callback.onProgress(progress));
            }
        });

        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("profile_picture", fileName, progressRequestBody);

        if (userId > 0) {
            bodyBuilder.addFormDataPart("user_id", String.valueOf(userId));
        }
        if (email != null && !email.trim().isEmpty()) {
            bodyBuilder.addFormDataPart("email", email.trim());
        }

        MultipartBody requestBody = bodyBuilder.build();

        // 4. Configure OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .addHeader("Accept", "application/json")
                .build();

        // 5. Execute Asynchronous Call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Network call failed", e);
                if (callback != null) {
                    mainHandler.post(() -> callback.onError("Network connection error: " + e.getLocalizedMessage()));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "";
                Log.d(TAG, "Server response: " + responseBody);

                try {
                    JSONObject json = new JSONObject(responseBody);
                    boolean success = json.optBoolean("success", false);
                    String message = json.optString("message", "Unknown server response");
                    String profilePictureUrl = json.optString("profile_picture_url", "");

                    // Fallback to data object if needed
                    if (profilePictureUrl.isEmpty() && json.has("data")) {
                        JSONObject dataObj = json.optJSONObject("data");
                        if (dataObj != null) {
                            profilePictureUrl = dataObj.optString("profile_picture_url", "");
                        }
                    }

                    if (success && !profilePictureUrl.isEmpty()) {
                        // Persist in local storage
                        saveSavedProfilePicture(context, profilePictureUrl);

                        final String finalUrl = profilePictureUrl;
                        if (callback != null) {
                            mainHandler.post(() -> callback.onSuccess(finalUrl));
                        }
                    } else {
                        if (callback != null) {
                            mainHandler.post(() -> callback.onError(message));
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    if (callback != null) {
                        mainHandler.post(() -> callback.onError("Server returned invalid response (HTTP " + response.code() + ")"));
                    }
                }
            }
        });
    }

    /**
     * Retrieve the locally saved profile picture URL from SharedPreferences.
     */
    public static String getSavedProfilePicture(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_PROFILE_PICTURE_URL, "");
    }

    /**
     * Save the profile picture URL into SharedPreferences.
     */
    public static void saveSavedProfilePicture(Context context, String url) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_PROFILE_PICTURE_URL, url).apply();
    }

    // Helper: Query display name from ContentResolver
    private static String queryFileName(ContentResolver resolver, Uri uri) {
        Cursor cursor = resolver.query(uri, null, null, null, null);
        if (cursor == null) return null;
        try {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    // Helper: Read InputStream into byte array
    private static byte[] readBytesFromUri(ContentResolver resolver, Uri uri) throws IOException {
        InputStream inputStream = resolver.openInputStream(uri);
        if (inputStream == null) return null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } finally {
            inputStream.close();
        }
    }

    // Helper: RequestBody with progress reporting
    private static class ProgressRequestBody extends RequestBody {
        private final RequestBody delegate;
        private final ProgressListener listener;

        interface ProgressListener {
            void onProgress(long bytesWritten, long contentLength);
        }

        ProgressRequestBody(RequestBody delegate, ProgressListener listener) {
            this.delegate = delegate;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return delegate.contentType();
        }

        @Override
        public long contentLength() throws IOException {
            return delegate.contentLength();
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            CountingSink countingSink = new CountingSink(sink, contentLength(), listener);
            BufferedSink bufferedSink = Okio.buffer(countingSink);
            delegate.writeTo(bufferedSink);
            bufferedSink.flush();
        }

        private static class CountingSink extends ForwardingSink {
            private long bytesWritten = 0;
            private final long contentLength;
            private final ProgressListener listener;

            CountingSink(Sink delegate, long contentLength, ProgressListener listener) {
                super(delegate);
                this.contentLength = contentLength;
                this.listener = listener;
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                if (listener != null) {
                    listener.onProgress(bytesWritten, contentLength);
                }
            }
        }
    }
}

