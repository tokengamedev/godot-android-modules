plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val pluginName = "GooglePlayReview"
val pluginPackage = "gaml.google.play.review"
val pluginVersion = "2.0.0"
val pluginCompileSdk = 34
val pluginMinSdk = 24

extra.apply { 
    set("outputLocation", "${rootDir}/bin") 
    set("moduleName", pluginName)
    set("templateLocation", "${projectDir}/template")
    set("author", "Token Gamedev")
    set("pluginDescription", "Android plugin for in-app review")
}

version = pluginVersion
layout.buildDirectory.set(rootProject.layout.buildDirectory.dir(project.name))

android {
    namespace=pluginPackage
    compileSdk = pluginCompileSdk

    defaultConfig {
        minSdk = pluginMinSdk
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

// Apply the GDAP plugin
apply<GdapPlugin>()

dependencies{
    implementation(libs.godot.library)
    implementation(libs.play.services.review)
}