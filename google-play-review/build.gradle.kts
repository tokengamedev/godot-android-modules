plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
// plugins{
//     id("com.android.library")
//     kotlin("android")
// }
// apply {
//     plugin("kotlin-android")
// }

// Version of the Module. It will be used for creating file
// file name = "{moduleName}.{project.version}.{build_type/variant}.aar"
version = "1.0.3"

// all the build outputs under one location
//setBuildDir("${rootProject.buildDir}/${project.name}")
layout.buildDirectory.set(rootProject.layout.buildDirectory.dir(project.name))

extra.apply {
    set("outputLocation",  "${rootDir}/bin")
    set("moduleName", "GooglePlayReview")
    set("binary_type", "local")
    set("remoteDependencies", "\"com.google.android.play:review:2.0.1\"")

}

android {
    namespace="gaml.google.play.review"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
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

    //compileOnly(fileTree(mapOf("dir" to "$rootDir/libs/", "include" to listOf("godot-lib*.aar"))))
    //implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")
}