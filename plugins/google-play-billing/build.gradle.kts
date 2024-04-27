plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

val pluginName = "GooglePlayBilling"
val pluginPackage = "gaml.google.play.billing"
val pluginVersion = "2.0.0"
val pluginCompileSdk = 34
val pluginMinSdk = 24

extra.apply {
    set("outputLocation", "${rootDir}/bin")
    set("moduleName", pluginName)
    set("templateLocation", "${projectDir}/template")
    set("author", "Token Gamedev")
    set("pluginDescription", "Android plugin for Integrating Google play billing Sdk")
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

dependencies {
    implementation(libs.godot.library)
    implementation(libs.androidx.ktx)
    implementation(libs.play.billing)
}