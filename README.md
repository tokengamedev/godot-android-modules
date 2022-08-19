# Godot Android Modules

### Collection of Godot Plugins for Android platform

Godot android modules is a free and open source collection of godot android plugins for advanced integration with the android devices. It is the list of all commonly used plugins like billing, notifications and others.

### Installation and usage

There are two ways you can get the plugins for integrating to your game
1. Get the latest zip or tar package from the release section and extract it. 
2. Build it yourself from the code. To do that:
   * Download the source code (clone or zip)
   * Open the Project with Android Studio (Chipmunk or higher)
   * Ensure all the configurations are met as per developer notes and build it (Make the Project)
   * The output by default should be in the top **bin** directory

To integrate the plugins into your project, place the selected plugins into ***android/plugins*** directory in your godot project.
 
To understand how to use plugins in Godot refer to [Godot docs](https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html)


### Plugins list

List of plugins or modules:

| Plugin or Module | Description |
| ---------------- | ----------- |
| [AppNotification](app-notification/README.md)  | Plugin to send local notification in android devices |
| [GooglePlayReview](google-play-review/README.md) | Plugin to include in-app review or review link component in your game |
| GooglePlayBilling | Plugin to integrate google play billing in the game for managing a store |
| GoogleAdMob | Plugin to integrate ad network from Google admob for displaying various types of ads |


### Contributing and Support

If you want to contribute please send a note to token.gamedev@gmail.com or raise a pull request.
If you are facing a problem in usage, raise an issue providing details about the problem.

### Developer Notes

Minimum Godot Engine - 3.5 (stable)
Minimum Android SDK - 23 (Build.VERSION_CODES.O)
Target Android SDK - 32 (Build.VERSION_CODES.S_V2)
Minimum Java Version(jdk) - 11
Minimum Kotlin version - 1.6.21
Android Gradle Plugin Version - 7.2.2
Gradle Version - 7.5.1

If anything else missed out, please send a note 



