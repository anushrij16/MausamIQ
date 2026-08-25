# MausamIQ Android Prototype — Implementation Brief

## Product purpose

MausamIQ is a personal weather decision-support assistant, not a generic weather dashboard. It transforms weather data into user context, personal relevance, risk and priority assessment, explainable recommendations, and actionable next steps.

## Prototype priorities

1. **Personalization:** The homepage adapts to the selected persona and user context.
2. **Context:** Persona, location, routine, time, activity, saved places, and destination affect relevance.
3. **Intelligence:** Rule-based risk and recommendation engines translate weather data into useful decisions.
4. **Explainability:** Major recommendations expose what, why, when, where, action, and a “Why am I seeing this?” audit trail.
5. **Trust:** Demo data is labeled, freshness is visible, unavailable data is stated, and the app never invents route, marine, medical, or forecast-confidence information.

## Android direction

The target is a native Android Studio project using Kotlin, Jetpack Compose, Material 3, Kotlin coroutines/Flow, MVVM/Clean Architecture, repository boundaries, Room/DataStore-compatible local persistence, secure backend-oriented API boundaries, Android location and notification capabilities, and optional voice interaction. The prototype should remain buildable from the command line.

## Main user flow

Open app → create or select user profile → choose one or more personas → save Home and another important location → set a routine or travel time → dashboard computes context → dashboard dynamically prioritizes relevant weather → user opens recommendation explanation → user gives feedback → profile and ranking update. The demo should support switching between Student, Farmer, Traveller, and other personas while keeping the same weather scenario so the change in priorities is obvious.

## Required functional areas

### Profile and personalization

Support multiple personas, editable later; weather personality fields; important weather factors; saved places; routines; activities; alert sensitivity; smart timing; language/accessibility controls; adaptive personalization pause; profile reset.

### Dashboard

Show current weather, data freshness, source, observed/forecast/interpretation distinctions, personalized recommendation, risk summary, prioritized location, dynamic content sections, and demo-mode labeling. Student ordering should emphasize college commute rain risk; Farmer ordering should emphasize rainfall outlook and farm windows; Traveller ordering should emphasize trip status, destination, route risk, and travel window.

### Weather intelligence

Model rain, heat, cold, wind, lightning, visibility, severe-weather, outdoor activity, and travel risk. Include comparison of locations and periods, a best-time engine for activities, proactive event weather, and multi-location monitoring. Route-level claims must only appear when route data exists.

### Explainability and feedback

Every important recommendation answers What, Why, When, Where, and Action. Provide a transparent factor list and optional demo decision trace. Support Helpful, Not useful, Too many alerts, Wrong timing, Not relevant, and Hide similar recommendations. Use a simple adaptive weighted ranking system rather than falsely claiming machine learning.

### Alerts

Prioritize alerts using severity, distance, time, persona, activity, preferences, seen state, and duplication. Consolidate duplicate alerts, support escalation, and respect immediate/smart timing, quiet hours, critical override, and daily limits.

### Offline and trust

Use a local repository/cache abstraction and visibly label cached data with last updated time. Continue with manual location selection if location permission is denied. Request only necessary permissions. Keep sensitive/health preferences optional, consent-based, and easy to disable; never diagnose.

## Initial demo scenario

Use controlled demo data for a Chennai student with Home and College locations, a usual morning college commute, and heavy rain overlapping travel time. Allow switching persona to Farmer and Traveller to demonstrate that the weather data remains the same while the prioritized cards, explanation, and recommendation change.

## Implementation boundary

The first deliverable is a polished, interactive native Android prototype with local/demo data and clean seams for future Ktor backend integration. External weather/AI services should not be called directly from screens. The initial personalization engine is deterministic and explainable; backend integration, real push notifications, and live provider normalization remain explicit extension points unless the environment supports them safely.

## Trust copy constraints

Use wording such as “Marine conditions unavailable from the current data source,” “Environmental conditions may be uncomfortable for sensitive users,” and “Weather information may be outdated.” Never present mock data as live, invent wave/route data, claim arbitrary AI confidence, or present farming/fishing/medical recommendations as guarantees.

## Acceptance criteria

The prototype should have working navigation and interactions; a genuinely adaptive homepage; visible demo/live distinction; profile/persona switching; saved locations/routines; recommendation explanation; feedback loop; alert list; settings/personalization center; offline/freshness state; and a buildable Android project with README instructions.

## Source attachments

- `/home/ubuntu/upload/pasted_content.txt`
- `/home/ubuntu/upload/pasted_content_2.txt`
