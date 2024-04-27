pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "Godot Android Modules"

include ("google-play-review")
project(":google-play-review").projectDir = file("plugins/google-play-review")

include ("app-notification")
project(":app-notification").projectDir = file("plugins/app-notification")

include("google-play-billing")
project(":google-play-billing").projectDir = file("plugins/google-play-billing")

include("google-play-game-services")
project(":google-play-game-services").projectDir = file("plugins/google-play-game-services")

include("android-permission")
project(":android-permission").projectDir = file("plugins/android-permission")