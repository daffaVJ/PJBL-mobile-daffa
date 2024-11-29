// build.gradle.kts (Project level)
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.3.15") // Update versi Google Services
        classpath("com.android.tools.build:gradle:8.1.0") // Pastikan versi ini yang terbaru
    }
}

