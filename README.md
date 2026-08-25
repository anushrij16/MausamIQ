# MausamIQ Android Prototype

MausamIQ is a native Android weather-intelligence prototype built around the product idea **Weather data → user context → personal relevance → risk/priority → explainable recommendation → action**.

## What is included

The app is implemented with Kotlin, Jetpack Compose, Material 3, MVVM-style state management, a domain-level rule engine, normalized weather models, a demo weather repository seam, and a DataStore preference abstraction. The prototype demonstrates a genuinely adaptive home experience rather than a static weather dashboard.

The main screens are:

| Screen | Demonstrated behavior |
| --- | --- |
| Home | Current conditions, freshness, demo-mode disclosure, personalized recommendation, risk snapshot, location comparison, and dynamic content priority. |
| Alerts | Consolidated, context-ranked alerts and notification-timing controls. |
| Places | Saved Home/College locations, comparison context, and Smart Travel with an explicit route-data limitation. |
| Profile | Multi-persona selection, adaptive personalization, notification controls, quiet hours, privacy/trust messaging, and profile reset. |

## Judge/demo flow

Open the app in demo mode. The default profile is a Student with Home = Chennai, College = Tambaram, and an 08:00 college commute. On Home, open **Why this matters** to see the What/When/Where/Why/Action explanation and the factor audit trail. Open **Trace** to see the rule-based decision path. Switch the persona to Farmer or Traveller in Profile; the weather values remain controlled demo data while the recommendation and homepage priorities change.

## Build in Android Studio

Open `/home/ubuntu/mausamiq` as an existing Gradle project in Android Studio. Let Android Studio download the Android Gradle Plugin and dependencies, then run the `app` configuration on an Android emulator or device. The intended command-line build is:

```bash
./gradlew assembleDebug
```

This sandbox does not include the Android SDK or Gradle executable, so the source project could not be assembled here. Android Studio with an Android SDK is the expected build environment.

## Architecture

```text
Compose UI
   ↓
AppViewModel
   ↓
Domain intelligence engine
   ↓
WeatherRepository / local preferences seam
   ↓
Future Ktor REST API + normalized weather providers
```

The current intelligence implementation is deterministic and explainable. It is **not** presented as machine learning. A future production backend should own authentication, weather-provider normalization, user-context filtering, AI calls, response validation, alert deduplication, rate limiting, and notification scheduling. The Android screens should call the repository/service layer rather than external providers directly.

## Trust and safety behavior

The prototype labels controlled data as `DEMO MODE`, shows freshness and source text, and states when route or marine data is unavailable. It does not make medical diagnoses, guaranteed agricultural guidance, guaranteed fishing safety claims, arbitrary AI-confidence claims, or unsupported route-level weather claims.

## Suggested next implementation steps

1. Add a Ktor backend with authenticated endpoints for profile, saved places, normalized weather, recommendations, alerts, and feedback.
2. Replace `DemoWeatherRepository` with a Retrofit or Ktor Client implementation that consumes the backend.
3. Add Room entities for cached forecasts, alerts, recommendations, and locations; use DataStore for lightweight preferences.
4. Add WorkManager for sensible periodic refresh and Android notification channels for prioritized alerts.
5. Add explicit runtime permission rationale screens for approximate/precise location, notifications, and optional microphone voice interaction.
6. Add live/demo mode selection and backend-controlled feature flags.
