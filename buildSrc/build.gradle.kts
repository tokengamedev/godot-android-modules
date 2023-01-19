import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
}

repositories {
    google()
    mavenCentral()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apiVersion = "1.8"
    kotlinOptions.jvmTarget = "11"
}

dependencies {
    implementation("com.android.tools.build:gradle-api:7.4.0")
    implementation(kotlin("stdlib"))
    gradleApi()
}
