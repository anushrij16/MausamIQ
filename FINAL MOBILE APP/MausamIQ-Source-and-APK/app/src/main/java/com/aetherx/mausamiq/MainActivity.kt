package com.aetherx.mausamiq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aetherx.mausamiq.core.designsystem.MausamIQTheme
import com.aetherx.mausamiq.presentation.alerts.AlertsViewModel
import com.aetherx.mausamiq.presentation.auth.AuthViewModel
import com.aetherx.mausamiq.presentation.auth.ForgotPasswordScreen
import com.aetherx.mausamiq.presentation.auth.LoginScreen
import com.aetherx.mausamiq.presentation.auth.RegisterScreen
import com.aetherx.mausamiq.presentation.dashboard.DashboardViewModel
import com.aetherx.mausamiq.presentation.forecast.ForecastViewModel
import com.aetherx.mausamiq.presentation.insights.InsightsViewModel
import com.aetherx.mausamiq.presentation.main.MainAppShell
import com.aetherx.mausamiq.presentation.map.WeatherMapViewModel
import com.aetherx.mausamiq.presentation.onboarding.OnboardingScreen
import com.aetherx.mausamiq.presentation.onboarding.OnboardingViewModel
import com.aetherx.mausamiq.presentation.profile.ProfileViewModel
import com.aetherx.mausamiq.presentation.settings.SettingsViewModel
import com.aetherx.mausamiq.presentation.splash.SplashScreen
import com.aetherx.mausamiq.presentation.travel.SmartTravelViewModel
import com.aetherx.mausamiq.presentation.welcome.WelcomeScreen

enum class RootNavState {
    SPLASH,
    WELCOME,
    LOGIN,
    REGISTER,
    FORGOT_PASSWORD,
    ONBOARDING,
    MAIN
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as MausamApplication

        setContent {
            val themePreference by app.preferencesManager.themeMode.collectAsState(initial = "DARK")
            val isDark = when (themePreference) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            MausamIQTheme(darkTheme = isDark) {
                var navState by remember { mutableStateOf(RootNavState.SPLASH) }

                val isLoggedIn by app.preferencesManager.isLoggedIn.collectAsState(initial = false)
                val isOnboardingCompleted by app.preferencesManager.isOnboardingCompleted.collectAsState(initial = false)

                // ViewModels
                val authViewModel = remember {
                    AuthViewModel(app.userRepository, app.preferencesManager)
                }
                val onboardingViewModel = remember {
                    OnboardingViewModel(app.preferencesManager, app.userRepository, app.weatherRepository)
                }
                val dashboardViewModel = remember {
                    DashboardViewModel(app.weatherRepository, app.userRepository, app.alertRepository, app.preferencesManager)
                }
                val forecastViewModel = remember {
                    ForecastViewModel(app.weatherRepository, app.preferencesManager)
                }
                val insightsViewModel = remember {
                    InsightsViewModel(app.weatherRepository, app.preferencesManager)
                }
                val mapViewModel = remember {
                    WeatherMapViewModel(app.preferencesManager, app.userRepository)
                }
                val travelViewModel = remember {
                    SmartTravelViewModel(app.weatherRepository)
                }
                val alertsViewModel = remember {
                    AlertsViewModel(app.alertRepository, app.preferencesManager)
                }
                val profileViewModel = remember {
                    ProfileViewModel(app.userRepository, app.preferencesManager)
                }
                val settingsViewModel = remember {
                    SettingsViewModel(app.preferencesManager, app.userRepository)
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = navState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                        },
                        label = "root_navigation"
                    ) { state ->
                        when (state) {
                            RootNavState.SPLASH -> SplashScreen(
                                onSplashFinished = {
                                    navState = when {
                                        !isLoggedIn -> RootNavState.WELCOME
                                        !isOnboardingCompleted -> RootNavState.ONBOARDING
                                        else -> RootNavState.MAIN
                                    }
                                }
                            )

                            RootNavState.WELCOME -> WelcomeScreen(
                                onGetStarted = { navState = RootNavState.REGISTER },
                                onSignIn = { navState = RootNavState.LOGIN },
                                onDemoMode = {
                                    authViewModel.continueAsGuest {
                                        navState = RootNavState.MAIN
                                        dashboardViewModel.loadDashboardData(forceRefresh = true)
                                    }
                                }
                            )

                            RootNavState.LOGIN -> LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    navState = if (!isOnboardingCompleted) RootNavState.ONBOARDING else RootNavState.MAIN
                                    dashboardViewModel.loadDashboardData(forceRefresh = true)
                                },
                                onNavigateToRegister = { navState = RootNavState.REGISTER },
                                onNavigateToForgotPassword = { navState = RootNavState.FORGOT_PASSWORD }
                            )

                            RootNavState.REGISTER -> RegisterScreen(
                                viewModel = authViewModel,
                                onRegisterSuccess = {
                                    navState = RootNavState.ONBOARDING
                                },
                                onNavigateToLogin = { navState = RootNavState.LOGIN }
                            )

                            RootNavState.FORGOT_PASSWORD -> ForgotPasswordScreen(
                                viewModel = authViewModel,
                                onNavigateBack = { navState = RootNavState.LOGIN }
                            )

                            RootNavState.ONBOARDING -> OnboardingScreen(
                                viewModel = onboardingViewModel,
                                onOnboardingFinished = {
                                    navState = RootNavState.MAIN
                                    dashboardViewModel.loadDashboardData(forceRefresh = true)
                                }
                            )

                            RootNavState.MAIN -> MainAppShell(
                                dashboardViewModel = dashboardViewModel,
                                forecastViewModel = forecastViewModel,
                                insightsViewModel = insightsViewModel,
                                mapViewModel = mapViewModel,
                                travelViewModel = travelViewModel,
                                alertsViewModel = alertsViewModel,
                                profileViewModel = profileViewModel,
                                settingsViewModel = settingsViewModel,
                                onLogout = {
                                    navState = RootNavState.WELCOME
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
