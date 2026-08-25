# MausamIQ Backend Contract (Future Ktor Service)

The Android client should depend on one authenticated MausamIQ API rather than calling multiple weather or AI providers directly.

| Endpoint | Purpose |
| --- | --- |
| `GET /v1/weather?locationId=` | Return normalized `WeatherSnapshot`, freshness, source, and forecast period. |
| `GET /v1/locations` | Return saved places and priority metadata. |
| `POST /v1/profile/context` | Store personas, routines, activities, alert preferences, and consent flags. |
| `GET /v1/recommendations/current` | Return a validated recommendation with What/Why/When/Where/Action and explanation factors. |
| `GET /v1/alerts` | Return deduplicated, context-ranked alerts with escalation state. |
| `POST /v1/feedback` | Record non-sensitive feedback signals for adaptive ranking. |
| `POST /v1/travel/analyze` | Analyze origin, destination, time, and mode; only return route-level weather if route data is available. |

The service should normalize external provider responses into `WeatherData`, `Forecast`, `WeatherAlert`, `LocationWeather`, and `RiskData`. It should keep observed facts, forecast facts, AI/rule interpretations, and user preferences as separate fields. Authentication, authorization, input validation, rate limiting, response validation, and secret management belong on the server.

For the initial implementation, the Android app uses a deterministic Kotlin rule engine and controlled demo data. An optional Python/FastAPI AI service should only be introduced when statistical modeling or data processing provides a real advantage; the prototype must not claim machine learning when it has not been implemented.
