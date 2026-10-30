package com.example.myapplication.data.network.api

import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.model.AnalyzeClaimRequest
import com.example.myapplication.data.network.model.ApiResponse
import com.example.myapplication.data.network.model.ForgotPasswordRequest
import com.example.myapplication.data.network.model.LoginRequest
import com.example.myapplication.data.network.model.RecentScanDto
import com.example.myapplication.data.network.model.RemoveArticleRequest
import com.example.myapplication.data.network.model.ResetPasswordRequest
import com.example.myapplication.data.network.model.SaveArticleRequest
import com.example.myapplication.data.network.model.SavedArticleDto
import com.example.myapplication.data.network.model.SignUpRequest
import com.example.myapplication.data.network.model.UpdateProfileRequest
import com.example.myapplication.data.network.model.UpdateSettingsRequest
import com.example.myapplication.data.network.model.UserDto
import com.example.myapplication.data.network.model.UserProfileDto
import com.example.myapplication.data.network.model.UserSettingsDto
import com.example.myapplication.data.network.model.VerifyOtpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Complete Retrofit API Service interface for TruthLens Android Application.
 * Communicates with TruthLens PHP backend deployed on XAMPP / remote server.
 */
interface TruthLensApiService {

    // --- Authentication ---

    @POST("auth/signup.php")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): ApiResponse<UserDto?>

    @POST("auth/login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<UserDto>

    @POST("auth/forgot_password.php")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): ApiResponse<Unit>

    @POST("auth/verify_otp.php")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): ApiResponse<Unit>

    @POST("auth/reset_password.php")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): ApiResponse<Unit>


    // --- User Profile ---

    @GET("profile/get_profile.php")
    suspend fun getProfile(
        @Query("user_id") userId: Int
    ): ApiResponse<UserProfileDto>

    @POST("profile/update_profile.php")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): ApiResponse<UserProfileDto>


    // --- News / Claim Analysis ---

    @POST("analysis/analyze_claim.php")
    suspend fun analyzeClaim(
        @Body request: AnalyzeClaimRequest
    ): ApiResponse<AnalysisResultDto>

    @GET("analysis/get_recent_scans.php")
    suspend fun getRecentScans(
        @Query("user_id") userId: Int,
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<RecentScanDto>>


    // --- Saved Articles / Bookmarks ---

    @GET("articles/get_saved.php")
    suspend fun getSavedArticles(
        @Query("user_id") userId: Int
    ): ApiResponse<List<SavedArticleDto>>

    @POST("articles/save_article.php")
    suspend fun saveArticle(
        @Body request: SaveArticleRequest
    ): ApiResponse<Unit>

    @POST("articles/remove_saved.php")
    suspend fun removeSavedArticle(
        @Body request: RemoveArticleRequest
    ): ApiResponse<Unit>


    // --- App & User Settings ---

    @GET("settings/get_settings.php")
    suspend fun getSettings(
        @Query("user_id") userId: Int
    ): ApiResponse<UserSettingsDto>

    @POST("settings/update_settings.php")
    suspend fun updateSettings(
        @Body request: UpdateSettingsRequest
    ): ApiResponse<UserSettingsDto>
}

