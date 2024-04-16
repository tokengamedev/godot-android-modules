pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "Godot Android Modules"

include ("google-play-review")
include ("app-notification")
include("google-play-billing")
include("google-play-game-services")
include("applovin-max")
include("ogury-consent-manager")
include("android-permission")
