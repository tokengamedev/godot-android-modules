plugins{
    id("com.android.library")
    kotlin("android")
}
apply {
    plugin("kotlin-android")
}

// Version of the Module. It will be used for creating file
// file name = "{moduleName}.{project.version}.{build_type/variant}.aar"
version = "1.0.0"

// all the build outputs under one location
setBuildDir("${rootProject.buildDir}/${project.name}")

// Needed to create GDAP file
extra.apply {
    set("outputLocation",  "${rootDir}/bin")
    set("moduleName", "AppLovinMax")
    set("binary_type", "local")
    set("remoteDependencies", "\"com.applovin:applovin-sdk:11.10.1\"")

}

android {
    namespace="gaml.applovinmax"
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


dependencies{
    compileOnly(fileTree(mapOf("dir" to "$rootDir/libs/", "include" to listOf("godot-lib*.aar"))))
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("com.applovin:applovin-sdk:11.10.1")
    implementation("com.google.android.gms:play-services-ads-identifier")
//    implementation(kotlin("stdlib", "1.6.21"))
}