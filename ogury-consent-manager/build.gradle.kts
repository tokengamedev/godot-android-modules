plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
//plugins{
//    id("com.android.library")
//    kotlin("android")
//}
//apply {
//    plugin("kotlin-android")
//}

repositories {
    maven(url = "https://maven.ogury.co")
}
// Version of the Module. It will be used for creating file
// file name = "{moduleName}.{project.version}.{build_type/variant}.aar"
version = "1.0.0"

// all the build outputs under one location
//setBuildDir("${rootProject.buildDir}/${project.name}")
layout.buildDirectory.set(rootProject.layout.buildDirectory.dir(project.name))

extra.apply {
    set("outputLocation",  "${rootDir}/bin")
    set("moduleName", "OguryConsentManager")
    set("binary_type", "local")
    set("remoteDependencies", "\"co.ogury:ogury-sdk:5.5.0\"")
    set("customMavenRepos","\"https://maven.ogury.co\"")

}

android {
    namespace="gaml.ogury.consent.manager"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
//        targetSdk = 32
    }

//    buildTypes {
//        release {
//            isMinifyEnabled = false
//        }
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}


// Apply the GDAP plugin to move to bin folder and create .gdap file
apply<GdapPlugin>()

dependencies{
    implementation("org.godotengine:godot:4.1.3.stable")
    implementation("co.ogury:ogury-sdk:5.5.0")
}