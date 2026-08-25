# CONTINUATION PROMPT & DEVELOPER HANDBOOK
## Project: MAUSAMIQ — AI-Powered Personalized Weather Intelligence System
**Team:** AETHERX | **SIH 2026** | **Problem Statement:** SIH26076  
**Repository Path:** `e:\Mass`

---

## 1. Project Overview & Architectural Summary
MAUSAMIQ is a complete, native Android application implemented in **Kotlin** and **Jetpack Compose (Material 3)**. It is built strictly following Clean Architecture + MVVM principles, separating domain logic, AI decision models, Room offline caching, and real-time Open-Meteo REST forecasting.

### Key Capabilities Built & Functional:
1. **Dynamic Visual Engine (`core/designsystem/components/WeatherCanvasEffects.kt`)**: Pure Compose Canvas 60fps animations for Sun Glow & Flares, Drifting Cloud Layers, Particle Rain with Splashes, Thunderstorm Lightning, and Twinkling Starry Nights with Crescent Moon.
2. **AI Personalization Engine (`ai/PersonalizationEngine.kt`)**: Generates tailored decision recommendations for 6 citizen personas:
   - 🎓 *Student*: Commute rain timing (4–6 PM), UV alerts, campus outdoor class advisory.
   - 🌾 *Farmer*: 48h dry spraying window, wind velocity threshold, harvest prep.
   - ✈️ *Traveller*: Highway visibility, crosswind safety on expressways, destination delta.
   - 🏃 *Fitness*: Best aerobic workout hours (6:00 AM – 7:30 AM), heat strain & UV comfort.
   - 🦺 *Outdoor Worker*: OSHA heat safety thresholds, hydration reminders, severe lightning shelter alerts.
   - 🌤 *General Everyday*: Composite daily summary, umbrella alerts.
3. **Explainable AI Modal (`presentation/insights/ExplainabilityBottomSheetContent.kt`)**: Displays exact contributing factors, mathematical formula, confidence score, and clear statistical probability explanations ("Why am I seeing this?").
4. **Commute Intelligence (`ai/CommuteIntelligence.kt` & `presentation/dashboard/CommuteIntelligenceCard.kt`)**: Route analysis between saved nodes (e.g., Home to College/Work) during specific departure and return windows.
5. **Atmospheric Radar Visualizer (`presentation/map/AtmosphericRadarVisualizer.kt`)**: Real-time rotating Doppler radar sweep with layer toggling (Precipitation Radar, Temperature Contours, Wind Streamlines, Severe Alert Zones).
6. **Smart Travel Route Planner (`presentation/travel/SmartTravelScreen.kt`)**: Route risk advisory and recommended departure windows.
7. **Offline-First Room Database (`data/local/MausamDatabase.kt`)**: Caching entities (`UserEntity`, `SavedLocationEntity`, `WeatherCacheEntity`, `AlertEntity`) with offline indicator banner.
8. **7-Step Interactive Onboarding Wizard (`presentation/onboarding/OnboardingScreen.kt`)**: Location selection, persona choosing, interest tags, activities, commute schedules, saved locations, and language selection.
9. **Localization**: English (`values/strings.xml`), Tamil (`values-ta/strings.xml`), and Hindi (`values-hi/strings.xml`).
10. **SIH Demo Mode Toggle**: Zero-network fallback with curated SIH presentation scenarios.

---

## 2. Directory & Module Reference

```
e:\Mass
├── app/
│   ├── build.gradle.kts (Android SDK 35, Jetpack Compose BOM, Room, Retrofit, DataStore, WorkManager)
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/aetherx/mausamiq/
│       │   │   ├── MausamApplication.kt (Dependency setup & WorkManager periodic sync)
│       │   │   ├── MainActivity.kt (State machine router: Splash -> Welcome -> Auth -> Onboarding -> Main)
│       │   │   ├── core/
│       │   │   │   ├── designsystem/ (Colors, Type, Shapes, Theme, WeatherCanvasEffects, GlassCard, AnimatedWeatherIcon)
│       │   │   │   ├── datastore/ (PreferencesManager)
│       │   │   │   └── utils/ (DateUtils, WeatherProbabilityFormatter, Resource)
│       │   │   ├── data/
│       │   │   │   ├── local/ (Room entities, DAOs, MausamDatabase)
│       │   │   │   ├── remote/ (OpenMeteoApi, RetrofitClient, DTOs)
│       │   │   │   └── repository/ (WeatherRepositoryImpl, DemoWeatherRepository, UserRepositoryImpl, AlertRepositoryImpl)
│       │   │   ├── domain/
│       │   │   │   ├── model/ (WeatherInfo, CurrentWeather, HourlyWeather, DailyWeather, Persona, CommutePlan, TravelPlan, WeatherAlert, ExplainableInsight)
│       │   │   │   └── repository/ (IWeatherRepository, IUserRepository, IAlertRepository)
│       │   │   ├── ai/ (PersonalizationEngine, CommuteIntelligence, DemoScenarios)
│       │   │   ├── presentation/
│       │   │   │   ├── splash/ (SplashScreen)
│       │   │   │   ├── welcome/ (WelcomeScreen)
│       │   │   │   ├── auth/ (LoginScreen, RegisterScreen, ForgotPasswordScreen, AuthViewModel)
│       │   │   │   ├── onboarding/ (7-Step Wizard Screen & ViewModel)
│       │   │   │   ├── main/ (MainAppShell, TopAppBar, BottomNavBar, NavRail)
│       │   │   │   ├── dashboard/ (DashboardScreen, HeroWeatherCard, ImportantForYouCard, RecommendationCard, CommuteIntelligenceCard)
│       │   │   │   ├── forecast/ (ForecastScreen, HourlyForecastTab, DailyForecastTab, WeatherTrendChart)
│       │   │   │   ├── insights/ (AiInsightsScreen, ExplainabilityBottomSheetContent)
│       │   │   │   ├── map/ (WeatherMapScreen, AtmosphericRadarVisualizer)
│       │   │   │   ├── travel/ (SmartTravelScreen)
│       │   │   │   ├── alerts/ (AlertsScreen)
│       │   │   │   ├── profile/ (ProfileScreen)
│       │   │   │   └── settings/ (SettingsScreen)
│       │   │   └── work/ (WeatherSyncWorker, AlertNotificationWorker)
│       │   └── res/
│       │       ├── values/ (strings.xml, colors.xml, themes.xml)
│       │       ├── values-ta/ (strings.xml)
│       │       ├── values-hi/ (strings.xml)
│       │       └── drawable/ (Adaptive launcher icons, vectors)
│       └── test/java/com/aetherx/mausamiq/ (Unit tests for AI engine, commute logic, and DTO mappers)
```

---

## 3. How to Build & Test

### Command Line
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
.\gradlew.bat assembleDebug
.\gradlew.bat testDebugUnitTest
```

### Android Studio
1. Open Android Studio.
2. Select **Open** and choose the `e:\Mass` folder.
3. Allow Gradle to sync.
4. Select `app` run configuration and click **Run** (Shift+F10) on an Emulator or Device.
