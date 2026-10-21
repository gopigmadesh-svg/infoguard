package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.myapplication.ui.TopicInsightsScreen
import com.example.myapplication.ui.UrlAnalysisScreen
import com.example.myapplication.ui.WhyResultScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

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
    CAMERA_SCAN,
    IMAGE_ANALYSIS,
    URL_ANALYSIS,
    COMPARE_SOURCES,
    TOPIC_INSIGHTS,
    HELP_SUPPORT,
    PRIVACY_SECURITY,
    PRESENTATION_BOARD,
    ANALYSIS,
    COMPLETE,
    RESULT,
    WHY_RESULT,
    REPORT,
    HISTORY,
    SAVED_ARTICLES,
    CHAT,
    REMINDERS,
    NOTIFICATIONS,
    PROFILE,
    SETTINGS,
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
    var currentScreen by remember { mutableStateOf(AppScreen.SPLASH) }

    Crossfade(
        targetState = currentScreen,
        animationSpec = tween(durationMillis = 500),
        label = "AppScreenTransition"
    ) { screen ->
        when (screen) {
            AppScreen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = AppScreen.ONBOARDING
                    }
                )
            }
            AppScreen.ONBOARDING -> {
                OnboardingScreen(
                    onGetStartedClick = {
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
            AppScreen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = AppScreen.PROFILE_SETUP
                    },
                    onNavigateToSignUp = {
                        currentScreen = AppScreen.SIGN_UP
                    },
                    onForgotPasswordClick = {
                        currentScreen = AppScreen.FORGOT_PASSWORD
                    }
                )
            }
            AppScreen.SIGN_UP -> {
                SignUpScreen(
                    onSignUpSuccess = {
                        currentScreen = AppScreen.PROFILE_SETUP
                    },
                    onNavigateToLogin = {
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
            AppScreen.FORGOT_PASSWORD -> {
                ForgotPasswordScreen(
                    onSendCodeClick = {
                        currentScreen = AppScreen.OTP_VERIFICATION
                    },
                    onBackToLoginClick = {
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
            AppScreen.OTP_VERIFICATION -> {
                OtpVerificationScreen(
                    onVerifyClick = {
                        currentScreen = AppScreen.RESET_PASSWORD
                    },
                    onResendClick = { }
                )
            }
            AppScreen.RESET_PASSWORD -> {
                ResetPasswordScreen(
                    onUpdatePasswordSuccess = {
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
            AppScreen.PROFILE_SETUP -> {
                ProfileSetupScreen(
                    onSavePreferences = {
                        currentScreen = AppScreen.HOME
                    },
                    onBackClick = {
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }
            AppScreen.HOME -> {
                HomeScreen(
                    onBackToSplash = {
                        currentScreen = AppScreen.PRESENTATION_BOARD
                    },
                    onTabSelected = { index ->
                        when (index) {
                            1 -> currentScreen = AppScreen.SCAN
                            2 -> currentScreen = AppScreen.HISTORY
                            3 -> currentScreen = AppScreen.PROFILE
                        }
                    }
                )
            }
            AppScreen.SCAN -> {
                ScanScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HOME
                    },
                    onScanFinished = {
                        currentScreen = AppScreen.URL_ANALYSIS
                    },
                    onTabSelected = { index ->
                        when (index) {
                            0 -> currentScreen = AppScreen.HOME
                            2 -> currentScreen = AppScreen.HISTORY
                            3 -> currentScreen = AppScreen.PROFILE
                        }
                    }
                )
            }
            AppScreen.CAMERA_SCAN -> {
                CameraScanScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SCAN
                    },
                    onCaptureClick = {
                        currentScreen = AppScreen.IMAGE_ANALYSIS
                    },
                    onGalleryClick = {
                        currentScreen = AppScreen.IMAGE_ANALYSIS
                    }
                )
            }
            AppScreen.IMAGE_ANALYSIS -> {
                ImageAnalysisScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SCAN
                    },
                    onStartFullAnalysisClick = {
                        currentScreen = AppScreen.ANALYSIS
                    },
                    onShareClick = { }
                )
            }
            AppScreen.URL_ANALYSIS -> {
                UrlAnalysisScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SCAN
                    },
                    onAnalyzePageClick = {
                        currentScreen = AppScreen.ANALYSIS
                    },
                    onCopyLinkClick = { }
                )
            }
            AppScreen.COMPARE_SOURCES -> {
                CompareSourcesScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SOURCE_CREDIBILITY
                    },
                    onOpenReportClick = {
                        currentScreen = AppScreen.REPORT
                    },
                    onFilterClick = { }
                )
            }
            AppScreen.TOPIC_INSIGHTS -> {
                TopicInsightsScreen(
                    onBackClick = {
                        currentScreen = AppScreen.PROFILE
                    },
                    onClaimClick = {
                        currentScreen = AppScreen.RESULT
                    },
                    onTabSelected = { index ->
                        when (index) {
                            0 -> currentScreen = AppScreen.HOME
                            1 -> currentScreen = AppScreen.SCAN
                            2 -> currentScreen = AppScreen.HISTORY
                            3 -> currentScreen = AppScreen.PROFILE
                        }
                    }
                )
            }
            AppScreen.HELP_SUPPORT -> {
                HelpSupportScreen(
                    onBackClick = {
                        currentScreen = AppScreen.PROFILE
                    },
                    onStartConversationClick = {
                        currentScreen = AppScreen.CHAT
                    },
                    onReportIssueClick = {
                        currentScreen = AppScreen.ERROR
                    }
                )
            }
            AppScreen.PRIVACY_SECURITY -> {
                PrivacySecurityScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SETTINGS
                    },
                    onDeleteAccountClick = {
                        currentScreen = AppScreen.LOGOUT
                    }
                )
            }
            AppScreen.PRESENTATION_BOARD -> {
                PresentationBoardScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HOME
                    },
                    onScreenSelect = { _ ->
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.ANALYSIS -> {
                AnalysisScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SCAN
                    },
                    onAnalysisComplete = {
                        currentScreen = AppScreen.COMPLETE
                    }
                )
            }
            AppScreen.COMPLETE -> {
                CompleteScreen(
                    onViewResultClick = {
                        currentScreen = AppScreen.RESULT
                    },
                    onBackToHomeClick = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.RESULT -> {
                ResultScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HOME
                    },
                    onViewDetailsClick = {
                        currentScreen = AppScreen.SOURCE_CREDIBILITY
                    },
                    onShareClick = {
                        currentScreen = AppScreen.REPORT
                    }
                )
            }
            AppScreen.WHY_RESULT -> {
                WhyResultScreen(
                    onBackClick = {
                        currentScreen = AppScreen.RESULT
                    }
                )
            }
            AppScreen.REPORT -> {
                ReportScreen(
                    onBackClick = {
                        currentScreen = AppScreen.RESULT
                    },
                    onDownloadClick = {
                        currentScreen = AppScreen.SAVED_ARTICLES
                    },
                    onShareClick = { }
                )
            }
            AppScreen.HISTORY -> {
                HistoryScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HOME
                    },
                    onItemClick = {
                        currentScreen = AppScreen.SOURCE_CREDIBILITY
                    },
                    onTabSelected = { index ->
                        when (index) {
                            0 -> currentScreen = AppScreen.HOME
                            1 -> currentScreen = AppScreen.SCAN
                            3 -> currentScreen = AppScreen.PROFILE
                        }
                    },
                    onStartCheckingClick = {
                        currentScreen = AppScreen.SCAN
                    }
                )
            }
            AppScreen.SAVED_ARTICLES -> {
                SavedArticlesScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HISTORY
                    },
                    onArticleClick = {
                        currentScreen = AppScreen.SOURCE_CREDIBILITY
                    }
                )
            }
            AppScreen.CHAT -> {
                ChatScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HELP_SUPPORT
                    }
                )
            }
            AppScreen.REMINDERS -> {
                RemindersScreen(
                    onBackClick = {
                        currentScreen = AppScreen.SETTINGS
                    },
                    onAddReminderClick = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }
            AppScreen.NOTIFICATIONS -> {
                NotificationsScreen(
                    onBackClick = {
                        currentScreen = AppScreen.HOME
                    },
                    onNotificationClick = {
                        currentScreen = AppScreen.RESULT
                    }
                )
            }
            AppScreen.PROFILE -> {
                ProfileScreen(
                    onNavigateToSavedArticles = {
                        currentScreen = AppScreen.SAVED_ARTICLES
                    },
                    onNavigateToHistory = {
                        currentScreen = AppScreen.TOPIC_INSIGHTS
                    },
                    onNavigateToSettings = {
                        currentScreen = AppScreen.SETTINGS
                    },
                    onLogOutClick = {
                        currentScreen = AppScreen.LOGOUT
                    },
                    onTabSelected = { index ->
                        when (index) {
                            0 -> currentScreen = AppScreen.HOME
                            1 -> currentScreen = AppScreen.SCAN
                            2 -> currentScreen = AppScreen.HISTORY
                        }
                    }
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    onBackClick = {
                        currentScreen = AppScreen.PROFILE
                    },
                    onNavigateToReminders = {
                        currentScreen = AppScreen.REMINDERS
                    }
                )
            }
            AppScreen.SOURCE_CREDIBILITY -> {
                SourceCredibilityScreen(
                    onBackClick = {
                        currentScreen = AppScreen.RESULT
                    },
                    onViewDetailsClick = {
                        currentScreen = AppScreen.COMPARE_SOURCES
                    },
                    onRescanClick = {
                        currentScreen = AppScreen.CAMERA_SCAN
                    },
                    onShareClick = { }
                )
            }
            AppScreen.EMPTY_STATE -> {
                EmptyStateScreen(
                    onStartCheckingClick = {
                        currentScreen = AppScreen.SCAN
                    }
                )
            }
            AppScreen.ERROR -> {
                ErrorScreen(
                    onTryAgainClick = {
                        currentScreen = AppScreen.ANALYSIS
                    },
                    onBackToScanClick = {
                        currentScreen = AppScreen.SCAN
                    }
                )
            }
            AppScreen.LOGOUT -> {
                LogoutScreen(
                    onConfirmLogoutClick = {
                        currentScreen = AppScreen.LOGIN
                    },
                    onCancelClick = {
                        currentScreen = AppScreen.PROFILE
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
