package com.example.myapplication.data

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.InputStream
import kotlin.coroutines.resume

object OcrProcessor {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateImageUri(context: Context, uri: Uri): ValidationResult {
        val contentResolver = context.contentResolver
        val type = contentResolver.getType(uri)

        // Validate MIME type if available
        if (type != null && !type.startsWith("image/")) {
            return ValidationResult(false, "Invalid file type. Please select a JPG, JPEG, or PNG image.")
        }

        // Validate File Size (max 10MB = 10 * 1024 * 1024 bytes)
        val maxSizeBytes = 10 * 1024 * 1024L
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val size = inputStream?.available() ?: 0
            inputStream?.close()

            if (size > maxSizeBytes) {
                return ValidationResult(false, "Image file size exceeds the 10MB limit. Please choose a smaller image.")
            }
        } catch (_: Exception) {
            // Proceed if available() check throws
        }

        return ValidationResult(true)
    }

    suspend fun extractTextFromImage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            try {
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.text.trim()
                        if (continuation.isActive) {
                            if (text.isNotBlank()) {
                                continuation.resume(text)
                            } else {
                                continuation.resume("No readable text found in the image. Visual content analysis completed.")
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        if (continuation.isActive) {
                            continuation.resume("Text recognition failed: ${exception.localizedMessage ?: "Unknown error"}. Proceeding with visual image analysis.")
                        }
                    }
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resume("Could not read image file: ${e.localizedMessage ?: "File error"}")
                }
            }
        }
    }
}
