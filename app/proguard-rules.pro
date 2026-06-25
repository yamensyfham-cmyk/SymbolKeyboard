-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract <methods>;
}

-keep class com.symbolkeyboard.data.model.** { *; }

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep class kotlinx.coroutines.** { *; }

-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
