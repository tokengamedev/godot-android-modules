plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

tasks.create<Delete>("clean") {
    delete(
        fileTree("bin"),
        fileTree("build")
    )
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle.tools)
        classpath(libs.kotlin.gradle.plugin)
        gradleApi()

    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        // # Additional Repository
        //maven("https://plugins.gradle.org/m2/")

        // # Repo for are building on snapshot versions of Godot Library
        //maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}
