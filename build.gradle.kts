// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
}
buildscript {
    repositories {
        // Đảm bảo rằng bạn đã có hai kho lưu trữ này
        google() // Google's Maven repository
        mavenCentral()
        // Maven Central repository
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        // Thêm dependency cho Google services Gradle plugin
        // Dependency cho Google services
    }
}