plugins {
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
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
        classpath("com.android.tools.build:gradle:8.2.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
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
