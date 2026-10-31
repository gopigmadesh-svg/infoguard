package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.data.network.model.AnalysisResultDto
import com.example.myapplication.data.network.repository.AuthRepository
import com.example.myapplication.data.network.repository.SessionManager
import com.example.myapplication.ui.AnalysisScreen
import com.example.myapplication.ui.CameraScanScreen
import com.example.myapplication.ui.ChatScreen
import com.example.myapplication.ui.CompareSourcesScreen
import com.example.myapplication.ui.CompleteScreen
import com.example.myapplication.ui.EmptyStateScreen
import com.example.myapplication.ui.ErrorScreen
import com.example.myapplication.ui.ForgotPasswordScreen
import com.example.myapplication.ui.HelpSupportScreen
import com.example.myapplication.ui.HistoryScreen
import com.example.myapplication.ui.HomeScreen
import com.example.myapplication.ui.ImageAnalysisScreen
import com.example.myapplication.ui.LoginScreen
import com.example.myapplication.ui.LogoutScreen
import com.example.myapplication.ui.NotificationsScreen
import com.example.myapplication.ui.OnboardingScreen
import com.example.myapplication.ui.OtpVerificationScreen
import com.example.myapplication.ui.PresentationBoardScreen
import com.example.myapplication.ui.PrivacySecurityScreen
import com.example.myapplication.ui.ProfileScreen
import com.example.myapplication.ui.ProfileSetupScreen
import com.example.myapplication.ui.RemindersScreen
import com.example.myapplication.ui.ReportScreen
import com.example.myapplication.ui.ResetPasswordScreen
import com.example.myapplication.ui.ResultScreen
import com.example.myapplication.ui.SavedArticlesScreen
import com.example.myapplication.ui.ScanScreen
import com.example.myapplication.ui.SettingsScreen
import com.example.myapplication.ui.SignUpScreen
import com.example.myapplication.ui.SourceCredibilityScreen
import com.example.myapplication.ui.SplashScreen
import com.example.myapplication.ui.TipsScreen
import com.example.myapplication.ui.TopicInsightsScreen
import com.example.myapplication.ui.TrustScoreExplanationScreen
import com.example.myapplication.ui.UrlAnalysisScreen
import com.example.myapplication.ui.WhyResultScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.AuthViewModel

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD,
    OTP_VERIFICATION,
    RESET_PASSWORD,
    PROFILE_SETUP,
    HOME,
    SCAN,
    TIPS,
    HISTORY,
    PROFILE,
    SETTINGS,
    SAVED_ARTICLES,
    CHAT,
    REMINDERS,
    NOTIFICATIONS,
    CAMERA_SCAN,
    IMAGE_ANALYSIS,
    URL_ANALYSIS,
    COMPARE_SOURCES,
    TOPIC_INSIGHTS,
    HELP_SUPPORT,
    PRIVACY_SECURITY,
    PRESENTATION_BOARD,
    TRUST_SCORE_EXPLANATION,
    ANALYSIS,
    COMPLETE,
    RESULT,
    WHY_RESULT,
    REPORT,
    SOURCE_CREDIBILITY,
    EMPTY_STATE,
    ERROR,
    LOGOUT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainAppNav()
                }
            }
        }
    }
}

@Composable
fun MainAppNav() {
    val navigationStack = remember { mutableStateListOf(AppScreen.SPLASH) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authRepository = remember { AuthRepository(sessionManager) }
    val authViewModel = remember { AuthViewModel(authRepository) }

    var activeClaimText by remember { mutableStateOf("") }
    var activeScanResult by remember { mutableStateOf<AnalysisResultDto?>(null) }

    val currentScreen = navigationStack.lastOrNull() ?: AppScreen.HOME

    fun navigateTo(screen: AppScreen) {
        if (navigationStack.lastOrNull() != screen) {
            navigationStack.add(screen)
        }
    }

    fun switchTab(tabScreen: AppScreen) {
        if (navigationStack.lastOrNull() != tabScreen) {
            navigationStack.removeAll { it == tabScreen }
            navigationStack.add(tabScreen)
        }
    }

    fun goBack() {
        if (navigationStack.size > 1) {
            navigationStack.removeAt(navigationStack.lastIndex)
        }
    }

    fun selectBottomTab(index: Int) {
        when (index) {
            0 -> switchTab(AppScreen.HOME)
            1 -> switchTab(AppScreen.SCAN)
            2 -> switchTab(AppScreen.TIPS)
            3 -> switchTab(AppScreen.HISTORY)
            4 -> switchTab(AppScreen.PROFILE)
        }
    }

    // Android System Back Button Handler
    BackHandler(
        enabled = navigationStack.size > 1 &&
                currentScreen != AppScreen.LOGIN &&
                currentScreen != AppScreen.HOME
    ) {
        goBack()
    }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 500),
        label = "AppScreenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        navigationStack.clear()
                        if (authRepository.isLoggedIn()) {
                            navigationStack.add(AppScreen.HOME)
                        } else {
                            navigationStack.add(AppScreen.ONBOARDING)
                        }
                    }
                )
            }
            AppScreen.ONBOARDING -> {
                OnboardingScreen(
                    onGetStartedClick = {
                        navigateTo(AppScreen.LOGIN)
                    }
                )
            }
            AppScreen.LOGIN -> {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navigationStack.clear()
                        navigationStack.add(AppScreen.HOME)
                    },
                    onNavigateToSignUp = {
                        navigateTo(AppScreen.SIGN_UP)
                    },
                    onForgotPasswordClick = {
                        navigateTo(AppScreen.FORGOT_PASSWORD)
                    }
                )
            }
            AppScreen.SIGN_UP -> {
                SignUpScreen(
                    authViewModel = authViewModel,
                    onSignUpSuccess = {
                        navigationStack.clear()
                        navigationStack.add(AppScreen.HOME)
                    },
                    onNavigateToLogin = {
                        goBack()
                    }
                )
            }
            AppScreen.FORGOT_PASSWORD -> {
                ForgotPasswordScreen(
                    authViewModel = authViewModel,
                    onSendCodeClick = { email ->
                        authViewModel.setResetPasswordEmail(email)
                        navigateTo(AppScreen.OTP_VERIFICATION)
                    },
                    onBackToLoginClick = {
                        goBack()
                    }
                )
            }
            AppScreen.OTP_VERIFICATION -> {
                OtpVerificationScreen(
                    authViewModel = authViewModel,
                    email = authViewModel.resetEmail.collectAsState().value,
                    onVerifyClick = {
                        navigateTo(AppScreen.RESET_PASSWORD)
                    },
                    onResendClick = { }
                )
            }
            AppScreen.RESET_PASSWORD -> {
                ResetPasswordScreen(
                    authViewModel = authViewModel,
                    email = authViewModel.resetEmail.collectAsState().value,
                    onUpdatePasswordSuccess = {
                        navigationStack.clear()
                        navigationStack.add(AppScreen.LOGIN)
                    }
                )
            }
            AppScreen.PROFILE_SETUP -> {
                ProfileSetupScreen(
                    onSavePreferences = {
                        if (navigationStack.contains(AppScreen.HOME)) {
                            goBack()
                        } else {
                            navigationStack.clear()
                            navigationStack.add(AppScreen.HOME)
                        }
                    },
                    onBackClick = {
                        goBack()
                    }
                )
            }
            AppScreen.HOME -> {
                HomeScreen(
                    onBackToSplash = {
                        navigateTo(AppScreen.PRESENTATION_BOARD)
                    },
                    onAskAiClick = {
                        navigateTo(AppScreen.CHAT)
                    },
                    onScanNewsClick = {
                        navigateTo(AppScreen.SCAN)
                    },
                    onScanImageClick = {
                        navigateTo(AppScreen.CAMERA_SCAN)
                    },
                    onCheckUrlClick = {
                        navigateTo(AppScreen.URL_ANALYSIS)
                    },
                    onScanHistoryClick = {
                        navigateTo(AppScreen.HISTORY)
                    },
                    onTabSelected = { index ->
                        selectBottomTab(index)
                    }
                )
            }
            AppScreen.SCAN -> {
                ScanScreen(
                    onBackClick = {
                        goBack()
                    },
                    onScanFinished = {
                        navigateTo(AppScreen.ANALYSIS)
                    },
                    onScanNewsClick = { text ->
                        activeClaimText = text
                        navigateTo(AppScreen.ANALYSIS)
                    },
                    onScanImageClick = { _, _ ->
                        navigateTo(AppScreen.IMAGE_ANALYSIS)
                    },
                    onScanUrlClick = { url ->
                        activeClaimText = url
                        navigateTo(AppScreen.ANALYSIS)
                    },
                    onTabSelected = { index ->
                        selectBottomTab(index)
                    }
                )
            }
            AppScreen.TIPS -> {
                TipsScreen(
                    onBackClick = {
                        goBack()
                    },
                    onTabSelected = { index ->
                        selectBottomTab(index)
                    }
                )
            }
            AppScreen.CAMERA_SCAN -> {
                CameraScanScreen(
                    onBackClick = {
                        goBack()
                    },
                    onCaptureClick = {
                        navigateTo(AppScreen.IMAGE_ANALYSIS)
                    },
                    onGalleryClick = {
                        navigateTo(AppScreen.IMAGE_ANALYSIS)
                    }
                )
            }
            AppScreen.IMAGE_ANALYSIS -> {
                ImageAnalysisScreen(
                    onBackClick = {
                        goBack()
                    },
                    onStartFullAnalysisClick = {
                        navigateTo(AppScreen.ANALYSIS)
                    },
                    onShareClick = { }
                )
            }
            AppScreen.URL_ANALYSIS -> {
                UrlAnalysisScreen(
                    onBackClick = {
                        goBack()
                    },
                    onAnalyzePageClick = {
                        navigateTo(AppScreen.ANALYSIS)
                    },
                    onCopyLinkClick = { }
                )
            }
            AppScreen.COMPARE_SOURCES -> {
                CompareSourcesScreen(
                    analysisResult = activeScanResult,
                    onBackClick = {
                        goBack()
                    },
                    onOpenReportClick = {
                        navigateTo(AppScreen.REPORT)
                    },
                    onFilterClick = { }
                )
            }
            AppScreen.TOPIC_INSIGHTS -> {
                TopicInsightsScreen(
                    onBackClick = {
                        goBack()
                    },
                    onClaimClick = {
                        navigateTo(AppScreen.TRUST_SCORE_EXPLANATION)
                    },
                    onTabSelected = { index ->
                        selectBottomTab(index)
                    }
                )
            }
            AppScreen.HELP_SUPPORT -> {
                HelpSupportScreen(
                    onBackClick = {
                        goBack()
                    },
                    onStartConversationClick = {
                        navigateTo(AppScreen.CHAT)
                    },
                    onReportIssueClick = {
                        navigateTo(AppScreen.ERROR)
                    }
                )
            }
            AppScreen.PRIVACY_SECURITY -> {
                PrivacySecurityScreen(
                    onBackClick = {
                        goBack()
                    },
                    onDeleteAccountClick = {
                        navigateTo(AppScreen.LOGOUT)
                    }
                )
            }
            AppScreen.PRESENTATION_BOARD -> {
                PresentationBoardScreen(
                    onBackClick = {
                        goBack()
                    },
                    onAskAiClick = {
                        navigateTo(AppScreen.CHAT)
                    }
                )
            }
            AppScreen.TRUST_SCORE_EXPLANATION -> {
                TrustScoreExplanationScreen(
                    analysisResult = activeScanResult,
                    onBackClick = {
                        goBack()
                    },
                    onViewReportClick = {
                        navigateTo(AppScreen.REPORT)
                    },
                    onCompareSourcesClick = {
                        navigateTo(AppScreen.COMPARE_SOURCES)
                    },
                    onShareClick = {
                        navigateTo(AppScreen.REPORT)
                    }
                )
            }
            AppScreen.ANALYSIS -> {
                AnalysisScreen(
                    claimText = activeClaimText,
                    userId = if (authRepository.getCurrentUserId() > 0) authRepository.getCurrentUserId() else null,
                    onBackClick = {
                        goBack()
                    },
                    onAnalysisSuccess = { result ->
                        activeScanResult = result
                        navigateTo(AppScreen.COMPLETE)
                    },
                    onAnalysisComplete = {
                        navigateTo(AppScreen.COMPLETE)
                    }
                )
            }
            AppScreen.COMPLETE -> {
                CompleteScreen(
                    analysisResult = activeScanResult,
                    onViewResultClick = {
                        navigateTo(AppScreen.RESULT)
                    },
                    onBackToHomeClick = {
                        switchTab(AppScreen.HOME)
                    }
                )
            }
            AppScreen.RESULT -> {
                ResultScreen(
                    analysisResult = activeScanResult,
                    onBackClick = {
                        goBack()
                    },
                    onViewDetailsClick = {
                        navigateTo(AppScreen.TRUST_SCORE_EXPLANATION)
                    },
                    onShareClick = {
                        navigateTo(AppScreen.REPORT)
                    }
                )
            }
            AppScreen.WHY_RESULT -> {
                WhyResultScreen(
                    analysisResult = activeScanResult,
                    onBackClick = {
                        goBack()
                    }
                )
            }
            AppScreen.REPORT -> {
                ReportScreen(
                    analysisResult = activeScanResult,
                    onBackClick = {
                        goBack()
                    },
                    onDownloadClick = {
                        navigateTo(AppScreen.SAVED_ARTICLES)
                    },
                    onShareClick = { }
                )
            }
            AppScreen.HISTORY -> {
                HistoryScreen(
                    onBackClick = {
                        goBack()
                    },
                    onItemClick = {
                        navigateTo(AppScreen.TRUST_SCORE_EXPLANATION)
                    },
                    onTabSelected = { index ->
                        selectBottomTab(index)
                    },
                    onStartCheckingClick = {
                        navigateTo(AppScreen.SCAN)
                    }
                )
            }
            AppScreen.SAVED_ARTICLES -> {
                SavedArticlesScreen(
                    onBackClick = {
                        goBack()
                    },
                    onArticleClick = {
                        navigateTo(AppScreen.TRUST_SCORE_EXPLANATION)
                    }
                )
            }
            AppScreen.CHAT -> {
                ChatScreen(
                    onBackClick = {
                        goBack()
                    }
                )
            }
            AppScreen.REMINDERS -> {
                RemindersScreen(
                    onBackClick = {
                        goBack()
                    },
                    onAddReminderClick = {
                        switchTab(AppScreen.HOME)
                    }
                )
            }
            AppScreen.NOTIFICATIONS -> {
                NotificationsScreen(
                    onBackClick = {
                        goBack()
                    },
                    onNotificationClick = {
                        navigateTo(AppScreen.TRUST_SCORE_EXPLANATION)
                    }
                )
            }
            AppScreen.PROFILE -> {
                ProfileScreen(
                    onNavigateToPersonalInfo = {
                        navigateTo(AppScreen.PROFILE_SETUP)
                    },
                    onNavigateToSavedArticles = {
                        navigateTo(AppScreen.SAVED_ARTICLES)
                    },
                    onNavigateToHistory = {
                        navigateTo(AppScreen.HISTORY)
                    },
                    onNavigateToPrivacySecurity = {
                        navigateTo(AppScreen.PRIVACY_SECURITY)
                    },
                    onNavigateToSettings = {
                        navigateTo(AppScreen.SETTINGS)
                    },
                    onNavigateToHelpSupport = {
                        navigateTo(AppScreen.HELP_SUPPORT)
                    },
                    onLogOutClick = {
                        navigateTo(AppScreen.LOGOUT)
                    },
                    onTabSelected = { index ->
                        selectBottomTab(index)
                    }
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    onBackClick = {
                        goBack()
                    },
                    onNavigateToPersonalInfo = {
                        navigateTo(AppScreen.PROFILE_SETUP)
                    },
                    onNavigateToChangePassword = {
                        navigateTo(AppScreen.RESET_PASSWORD)
                    },
                    onNavigateToPreferences = {
                        navigateTo(AppScreen.PROFILE_SETUP)
                    },
                    onNavigateToNotifications = {
                        navigateTo(AppScreen.NOTIFICATIONS)
                    },
                    onNavigateToReminders = {
                        navigateTo(AppScreen.REMINDERS)
                    },
                    onNavigateToPrivacySettings = {
                        navigateTo(AppScreen.PRIVACY_SECURITY)
                    },
                    onNavigateToAbout = {
                        navigateTo(AppScreen.PRESENTATION_BOARD)
                    }
                )
            }
            AppScreen.SOURCE_CREDIBILITY -> {
                SourceCredibilityScreen(
                    onBackClick = {
                        goBack()
                    },
                    onViewDetailsClick = {
                        navigateTo(AppScreen.COMPARE_SOURCES)
                    },
                    onRescanClick = {
                        navigateTo(AppScreen.CAMERA_SCAN)
                    },
                    onShareClick = { }
                )
            }
            AppScreen.EMPTY_STATE -> {
                EmptyStateScreen(
                    onStartCheckingClick = {
                        navigateTo(AppScreen.SCAN)
                    }
                )
            }
            AppScreen.ERROR -> {
                ErrorScreen(
                    onTryAgainClick = {
                        navigateTo(AppScreen.ANALYSIS)
                    },
                    onBackToScanClick = {
                        navigateTo(AppScreen.SCAN)
                    }
                )
            }
            AppScreen.LOGOUT -> {
                LogoutScreen(
                    authViewModel = authViewModel,
                    onConfirmLogoutClick = {
                        navigationStack.clear()
                        navigationStack.add(AppScreen.LOGIN)
                    },
                    onCancelClick = {
                        goBack()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppNavPreview() {
    MyApplicationTheme {
        MainAppNav()
    }
}
