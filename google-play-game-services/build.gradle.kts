plugins{
    id("com.android.library")
    kotlin("android")
}
apply {
    plugin("kotlin-android")
}
// Version of the Module. It will be used for creating file
// file name = "{project.name}.{project.version}.{build_type/variant}.aar"
version = "1.0.1"

// all the build outputs under one location
setBuildDir("${rootProject.buildDir}/${project.name}")

extra.apply {
    set("outputLocation",  "${rootDir}/bin")
    set("moduleName", "GooglePlayGameServices")
    set("binary_type", "local")
    set("remoteDependencies", "\"com.google.android.gms:play-services-games-v2:17.0.0\"")

}

android {
    namespace = "gaml.google.play.game.services"
    compileSdk = 32

    defaultConfig {
        minSdk = 23
        //targetSdk = 32
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// Apply the GDAP plugin to move to bin folder and create .gdap file
apply<GdapPlugin>()

dependencies {
    compileOnly(fileTree(mapOf("dir" to "$rootDir/libs/", "include" to listOf("godot-lib*.aar"))))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.google.android.gms:play-services-games-v2:17.0.0")
}