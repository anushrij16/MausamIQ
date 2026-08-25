# MAUSAMIQ (मौसमआईक्यू / மௌசமிக்)
### AI-Powered Personalized Weather Intelligence & Decision-Support System

**Team:** AETHERX  
**Hackathon:** Smart India Hackathon 2026 (SIH 2026)  
**Problem Statement:** SIH26076  
**Tagline:** *"Not Just Weather. Weather That Matters to You."*

---

## 📌 Executive Summary & Problem Context
Traditional weather applications follow a passive broadcast model:
$$\text{Weather Data} \longrightarrow \text{User}$$
Such applications present raw meteorological metrics (atmospheric pressure in hPa, raw dew point, millimeter precipitation totals, percentage probabilities) without mapping those metrics to the user's immediate real-world context, schedule, occupational risks, or transit corridors.

**MAUSAMIQ** shifts the paradigm from simple weather viewing to an active, personalized **Weather Decision-Support System**:
$$\text{Weather Data} \longrightarrow \text{User Context} \longrightarrow \text{Persona} \longrightarrow \text{Activity} \longrightarrow \text{Location} \longrightarrow \text{Schedule} \longrightarrow \text{AI Personalization} \longrightarrow \text{Explainable Recommendation} \longrightarrow \text{Action}$$

MAUSAMIQ answers three critical questions for every citizen:
1. **What is the weather?** (Hyper-local precision forecast & atmospheric radar)
2. **What matters to ME?** (Persona-specific risk evaluation & commute window impact)
3. **What should I do?** (Explainable, actionable advisories before stepping out)

---

## 🚀 Key Innovations & Architectural Differentiators

### 1. Dedicated AI Personalization Engine
Evaluates live weather and multi-hour forecasts against the user's active persona lens:
* 🎓 **Student:** Monitors campus transit times (e.g., 5:00 PM return commute), afternoon sudden monsoon showers, and outdoor lecture transitions.
* 🌾 **Farmer:** Evaluates 48-hour dry spraying windows, pesticide wash-off risks, wind velocity thresholds, soil moisture, and severe storm warnings.
* ✈️ **Traveller:** Intercity highway visibility, dangerous crosswinds on flyovers/ghat sections, and destination weather discrepancies.
* 🏃 **Fitness Enthusiast:** Computes optimal aerobic workout windows (e.g., 6:00 AM – 7:30 AM) factoring in heat index, wet-bulb temperature, and UV radiation.
* 🦺 **Outdoor Worker:** Occupational safety alerts, mandatory shade intervals, and OSHA heat stress hydration protocols.
* 🌤 **General Everyday:** Practical daily living summaries, laundry drying indices, and weekend planning.

### 2. Explainable AI ("Why Am I Seeing This?")
Every recommendation is accompanied by an open factor breakdown bottom sheet explaining:
* Exact contributing factors (Rain Probability, Commute Window Overlap, Precipitation Volume, Wind Gusts).
* Mathematical formula and confidence score (e.g., $\text{Risk Score} = 0.45 \times P_{\text{rain}} + 0.35 \times \text{Overlap} + 0.20 \times \text{Volume}$).
* **Meteorological Clarity:** Clearly explains statistical likelihood vs. surface coverage (e.g., *A 70% probability means a 70% statistical likelihood of precipitation in your area, not that 70% of the terrain will get wet*).

### 3. Commute Intelligence
Does not assume current GPS represents the entire journey. Users save key nodes (`Home`, `College`, `Work`, `Farm`), and MausamIQ analyzes the weather specifically for the departure and arrival time windows.

### 4. Atmospheric Radar & Multi-Layer Visualizer
A custom Jetpack Compose Canvas radar system featuring:
* Real-time rotating Doppler radar sweep.
* Multi-layer toggle: **Precipitation Radar**, **Temperature Heat Contours**, **Wind Streamlines**, and **Severe Storm Alerts**.
* Interactive weather station markers with live telemetry.

### 5. Multilingual Inclusivity (English, Tamil, Hindi)
Native Android resource localization (`values/strings.xml`, `values-ta/strings.xml`, `values-hi/strings.xml`) supporting regional languages out-of-the-box.

### 6. Controlled SIH 2026 Demo Mode
A zero-friction toggle designed specifically for hackathon evaluation:
* **Student Scenario:** 85% monsoon downpour at 5:00 PM college return commute.
* **Farmer Scenario:** 48-hour harvest & pesticide spray window.
* **Fitness Scenario:** 6:30 AM morning jogging comfort window.
* **Traveller Scenario:** Highway crosswind & visibility alert.

---

## 🏗 Technology Stack & Native Android Architecture

```
                      MAUSAMIQ ANDROID APP
 ┌───────────────────────────────────────────────────────────┐
 │                   PRESENTATION LAYER                      │
 │  Jetpack Compose • Material 3 • Canvas Weather Effects    │
 │  Navigation Compose • MVVM StateFlow • Dynamic Theming    │
 └─────────────────────────────┬─────────────────────────────┘
                               │
 ┌─────────────────────────────▼─────────────────────────────┐
 │                      DOMAIN LAYER                         │
 │  AI Personalization Engine • Commute Intelligence         │
 │  Explainability Model • Use Cases • Domain Entities       │
 └─────────────────────────────┬─────────────────────────────┘
                               │
 ┌─────────────────────────────▼─────────────────────────────┐
 │                       DATA LAYER                          │
 │  Room Database (Offline Cache) • Retrofit & OkHttp        │
 │  Open-Meteo REST API • DataStore Preferences • WorkManager│
 └───────────────────────────────────────────────────────────┘
```

* **Language:** Kotlin 2.0.21 (100% Native)
* **UI Framework:** Jetpack Compose + Material 3
* **Architecture:** Clean Architecture + MVVM
* **Async & State:** Kotlin Coroutines + StateFlow
* **Local Persistence:** Room Database 2.6.1 + DataStore Preferences
* **Networking:** Retrofit 2.11.0 + OkHttp 4.12.0 + Kotlinx Serialization JSON
* **Background Tasks:** WorkManager 2.10.0
* **Graphics & Visuals:** Pure Compose Canvas animations (Rain, Thunderstorm Lightning, Clouds, Starry Night, Sun Glow, Snow)

---

## 📂 Modular Package Structure (Built for 6-Developer Scalability)

```
com.aetherx.mausamiq
├── core/
│   ├── designsystem/ (Theme, Color, Type, Shape, WeatherCanvasEffects, GlassCard, AnimatedWeatherIcon)
│   ├── datastore/ (PreferencesManager, UserSession, ThemePreferences)
│   └── utils/ (DateUtils, WeatherProbabilityFormatter, Resource)
├── data/
│   ├── local/
│   │   ├── dao/ (UserDao, SavedLocationDao, WeatherCacheDao, AlertDao)
│   │   ├── entity/ (UserEntity, SavedLocationEntity, WeatherCacheEntity, AlertEntity)
│   │   └── MausamDatabase.kt
│   ├── remote/
│   │   ├── dto/ (OpenMeteoResponseDto, GeocodingDto)
│   │   ├── OpenMeteoApi.kt
│   │   └── RetrofitClient.kt
│   └── repository/
│       ├── WeatherRepositoryImpl.kt
│       ├── DemoWeatherRepository.kt
│       ├── UserRepositoryImpl.kt
│       └── AlertRepositoryImpl.kt
├── domain/
│   ├── model/ (WeatherInfo, CurrentWeather, HourlyWeather, DailyWeather, Persona, CommutePlan, TravelPlan, WeatherAlert, ExplainableInsight)
│   └── repository/ (IWeatherRepository, IUserRepository, IAlertRepository)
├── ai/
│   ├── PersonalizationEngine.kt
│   ├── CommuteIntelligence.kt
│   └── DemoScenarios.kt
├── presentation/
│   ├── splash/ (SplashScreen)
│   ├── welcome/ (WelcomeScreen)
│   ├── auth/ (LoginScreen, RegisterScreen, ForgotPasswordScreen, AuthViewModel)
│   ├── onboarding/ (7-step animated wizard)
│   ├── main/ (MainAppShell, TopAppBar, BottomNavBar, NavRail)
│   ├── dashboard/ (DashboardScreen, HeroWeatherCard, ImportantForYouCard, RecommendationCard, CommuteIntelligenceCard)
│   ├── forecast/ (ForecastScreen, HourlyForecastTab, DailyForecastTab, WeatherTrendChart)
│   ├── insights/ (AiInsightsScreen, ExplainabilityBottomSheetContent)
│   ├── map/ (WeatherMapScreen, AtmosphericRadarVisualizer)
│   ├── travel/ (SmartTravelScreen)
│   ├── alerts/ (AlertsScreen)
│   ├── profile/ (ProfileScreen)
│   └── settings/ (SettingsScreen)
└── work/
    ├── WeatherSyncWorker.kt
    └── AlertNotificationWorker.kt
```

---

## 🛠 Build & Run Instructions

### Prerequisites
* **Android Studio:** Ladybug (2024.2+) or newer
* **JDK:** Java 17 or Java 21
* **Android SDK:** CompileSdk 35, MinSdk 26

### Building from Command Line
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd Mass
   ```
2. Build the Debug APK:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run Unit Tests:
   ```bash
   ./gradlew testDebugUnitTest
   ```
4. Install on Connected Android Device / Emulator:
   ```bash
   ./gradlew installDebug
   ```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 👥 Team AETHERX (SIH 2026 / SIH26076)
* **Member 1:** Android UI, Compose Theming & Dashboard Architecture
* **Member 2:** Dynamic Weather Canvas Animation Engine & Visual Assets
* **Member 3:** Authentication, DataStore Security & Session Persistence
* **Member 4:** AI Personalization Engine & Explainability Algorithms
* **Member 5:** Open-Meteo Integration, Forecast Timeline & Interactive Radar Map
* **Member 6:** Smart Alerts, Commute Intelligence, Room Database & Test Automation
