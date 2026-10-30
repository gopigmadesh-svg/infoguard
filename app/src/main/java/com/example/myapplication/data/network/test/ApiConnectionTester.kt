package com.example.myapplication.data.network.test

import android.util.Log
import com.example.myapplication.data.network.client.DirectApiClient
import com.example.myapplication.data.network.client.NetworkConfig
import com.example.myapplication.data.network.client.RetrofitClient
import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.model.AnalyzeClaimRequest
import com.example.myapplication.data.network.model.LoginRequest
import com.example.myapplication.data.network.model.SignUpRequest
import com.example.myapplication.data.network.model.UserDto
import com.example.myapplication.data.network.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class StepResult(
    val stepName: String,
    val success: Boolean,
    val message: String,
    val details: String? = null
)

data class ApiTestReport(
    val baseUrl: String,
    val overallSuccess: Boolean,
    val steps: List<StepResult>
)

/**
 * Diagnostic utility to test and verify the complete backend API connection
 * without requiring any UI integration.
 */
object ApiConnectionTester {

    private const val TAG = "TruthLensApiTester"

    suspend fun runFullApiDiagnostic(): ApiTestReport = withContext(Dispatchers.IO) {
        val steps = mutableListOf<StepResult>()
        val baseUrl = NetworkConfig.currentBaseUrl
        Log.d(TAG, "Starting TruthLens API connection diagnostic against $baseUrl")

        // 1. Health / Setup Check
        try {
            val setupResp = DirectApiClient.getJson<Any>("setup_database.php")
            steps.add(
                StepResult(
                    stepName = "1. Backend Health & Database Check",
                    success = setupResp.success,
                    message = setupResp.message,
                    details = "Resolved URL: ${DirectApiClient.resolveUrl("setup_database.php")}"
                )
            )
        } catch (e: Exception) {
            steps.add(
                StepResult(
                    stepName = "1. Backend Health & Database Check",
                    success = false,
                    message = "Connection failed: ${e.message}",
                    details = "Ensure XAMPP Apache and MySQL are running."
                )
            )
        }

        // 2. Test Sign Up
        val testEmail = "test_user_${System.currentTimeMillis() % 10000}@truthlens.test"
        val testPassword = "password123"
        var createdUserId = 1

        try {
            val signUpResp = try {
                RetrofitClient.apiService.signUp(SignUpRequest(name = "Test Auditor", email = testEmail, password = testPassword))
            } catch (e: Exception) {
                DirectApiClient.postJson<UserDto?>("auth/signup.php", SignUpRequest("Test Auditor", testEmail, testPassword))
            }

            steps.add(
                StepResult(
                    stepName = "2. User Registration (auth/signup.php)",
                    success = signUpResp.success,
                    message = signUpResp.message,
                    details = "Created User ID: ${signUpResp.data?.id ?: "N/A"}"
                )
            )
            if (signUpResp.data?.id != null) {
                createdUserId = signUpResp.data.id
            }
        } catch (e: Exception) {
            steps.add(
                StepResult(
                    stepName = "2. User Registration (auth/signup.php)",
                    success = false,
                    message = "Sign up test failed: ${e.message}"
                )
            )
        }

        // 3. Test Login
        try {
            val loginResp = try {
                RetrofitClient.apiService.login(LoginRequest(email = testEmail, password = testPassword))
            } catch (e: Exception) {
                DirectApiClient.postJson<UserDto>("auth/login.php", LoginRequest(testEmail, testPassword))
            }

            steps.add(
                StepResult(
                    stepName = "3. User Login (auth/login.php)",
                    success = loginResp.success,
                    message = loginResp.message,
                    details = "Authenticated Email: ${loginResp.data?.email ?: "N/A"}"
                )
            )
        } catch (e: Exception) {
            steps.add(
                StepResult(
                    stepName = "3. User Login (auth/login.php)",
                    success = false,
                    message = "Login test failed: ${e.message}"
                )
            )
        }

        // 4. Test Claim Analysis
        try {
            val sampleClaim = "Breakthrough study published by global health researchers."
            val analysisResp = try {
                RetrofitClient.apiService.analyzeClaim(
                    AnalyzeClaimRequest(userId = createdUserId, content = sampleClaim, scanType = "news")
                )
            } catch (e: Exception) {
                DirectApiClient.postJson<AnalysisResultDto>(
                    "analysis/analyze_claim.php",
                    AnalyzeClaimRequest(createdUserId, sampleClaim, "news")
                )
            }

            steps.add(
                StepResult(
                    stepName = "4. Claim Verification (analysis/analyze_claim.php)",
                    success = analysisResp.success,
                    message = analysisResp.message,
                    details = "Verdict: ${analysisResp.data?.verdict}, Score: ${analysisResp.data?.trustScore}%"
                )
            )
        } catch (e: Exception) {
            steps.add(
                StepResult(
                    stepName = "4. Claim Verification (analysis/analyze_claim.php)",
                    success = false,
                    message = "Claim analysis test failed: ${e.message}"
                )
            )
        }

        // 5. Test Profile Fetch & Update
        try {
            val profileResp = ProfileRepository().getProfile(createdUserId)
            if (profileResp.isSuccess) {
                val p = profileResp.getOrThrow()
                steps.add(
                    StepResult(
                        stepName = "5. User Profile API (profile/get_profile.php)",
                        success = true,
                        message = "Retrieved profile for ${p.name}",
                        details = "Email: ${p.email}, Topics: ${p.topicsCount}, Checks: ${p.checksCount}"
                    )
                )
            } else {
                steps.add(
                    StepResult(
                        stepName = "5. User Profile API (profile/get_profile.php)",
                        success = false,
                        message = profileResp.exceptionOrNull()?.message ?: "Failed to get profile"
                    )
                )
            }
        } catch (e: Exception) {
            steps.add(
                StepResult(
                    stepName = "5. User Profile API (profile/get_profile.php)",
                    success = false,
                    message = "Profile test failed: ${e.message}"
                )
            )
        }

        val allPassed = steps.all { it.success }
        val report = ApiTestReport(baseUrl = baseUrl, overallSuccess = allPassed, steps = steps)

        for (step in steps) {
            val symbol = if (step.success) "PASSED" else "FAILED"
            Log.d(TAG, "[$symbol] ${step.stepName} -> ${step.message} (${step.details.orEmpty()})")
        }

        report
    }
}

