# Proguard rules for MausamIQ
-keepattributes *Annotation*
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}
