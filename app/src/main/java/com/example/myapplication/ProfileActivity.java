package com.example.myapplication;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.data.network.ProfilePictureUploader;

import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

/**
 * TruthLens Java Profile Activity
 * Demonstrates Photo Picker integration, Progress Reporting, and SharedPreferences persistence.
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private FrameLayout layoutUploadProgress;
    private TextView tvUploadPercent;
    private TextView tvStatusMessage;
    private CardView btnEditProfilePicture;

    // Current user identifier (from login session)
    private int currentUserId = 1;
    private String currentUserEmail = "alex@example.com";

    // Modern Android Photo Picker Launcher
    private final ActivityResultLauncher<String> photoPickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImageSelected);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        loadSavedProfilePicture();
    }

    private void initViews() {
        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        layoutUploadProgress = findViewById(R.id.layoutUploadProgress);
        tvUploadPercent = findViewById(R.id.tvUploadPercent);
        tvStatusMessage = findViewById(R.id.tvStatusMessage);
        btnEditProfilePicture = findViewById(R.id.btnEditProfilePicture);

        // Open Photo Picker on edit button click or avatar click
        btnEditProfilePicture.setOnClickListener(v -> openPhotoPicker());
        findViewById(R.id.cardProfileImage).setOnClickListener(v -> openPhotoPicker());
    }

    private void openPhotoPicker() {
        if (layoutUploadProgress.getVisibility() == View.VISIBLE) {
            Toast.makeText(this, "Upload in progress, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }
        // Launch standard Android photo picker for image mime types
        photoPickerLauncher.launch("image/*");
    }

    private void handleImageSelected(Uri selectedUri) {
        if (selectedUri == null) {
            return;
        }

        // Show immediate local preview
        displayUriImage(selectedUri);

        // Display upload progress overlay
        layoutUploadProgress.setVisibility(View.VISIBLE);
        tvUploadPercent.setText("0%");
        tvStatusMessage.setVisibility(View.VISIBLE);
        tvStatusMessage.setTextColor(0xFF00E5FF);
        tvStatusMessage.setText("Uploading profile picture to server...");

        // Perform multipart/form-data upload using ProfilePictureUploader
        ProfilePictureUploader.uploadProfilePicture(
                this,
                selectedUri,
                currentUserId,
                currentUserEmail,
                new ProfilePictureUploader.UploadCallback() {
                    @Override
                    public void onProgress(int progressPercent) {
                        tvUploadPercent.setText(progressPercent + "%");
                    }

                    @Override
                    public void onSuccess(String profilePictureUrl) {
                        layoutUploadProgress.setVisibility(View.GONE);
                        tvStatusMessage.setText("Profile picture updated successfully!");
                        Toast.makeText(ProfileActivity.this, "Profile picture uploaded successfully!", Toast.LENGTH_SHORT).show();

                        // Load and display updated picture from server URL
                        loadRemoteImage(profilePictureUrl);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        layoutUploadProgress.setVisibility(View.GONE);
                        tvStatusMessage.setTextColor(0xFFEF4444);
                        tvStatusMessage.setText("Upload failed: " + errorMessage);
                        Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    /**
     * Retrieve and display the saved profile picture URL when the app is reopened.
     */
    private void loadSavedProfilePicture() {
        String savedUrl = ProfilePictureUploader.getSavedProfilePicture(this);
        if (savedUrl != null && !savedUrl.trim().isEmpty()) {
            loadRemoteImage(savedUrl);
        }
    }

    private void displayUriImage(Uri uri) {
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivProfilePicture.setImageBitmap(bitmap);
                is.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRemoteImage(String imageUrl) {
        new Thread(() -> {
            try {
                InputStream in = new java.net.URL(imageUrl).openStream();
                Bitmap bmp = BitmapFactory.decodeStream(in);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (bmp != null) {
                        ivProfilePicture.setImageBitmap(bmp);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

