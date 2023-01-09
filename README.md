# Godot Android Modules

Godot android modules is a free and open source collection of android plugins for Godot Game engine. 
It is the list of all commonly used plugins like billing, notifications and others.

The goals of the repository are:
1. Create a list of commonly used plugins in one place.
2. Ability to pick and choose from the plugins. (Less unwanted code)
3. Use latest APIs as much as possible
4. Automate gdap file generation.

### Installation and usage

There are two ways you can get the plugins for integrating into your game:


##### Releases:
   * Get the latest zip or tar package from the release section and extract it.

   * Pick and choose whichever plugins you want to integrate.   

   * To integrate the plugins into your project, place the selected plugins(both .gdap and .aar file) into ***android/plugins*** directory in your godot project.


##### Manual Build:
   * Download the source code (clone or zip)
   * Open the Project with Android Studio (Chipmunk or higher)
   * Ensure all the configurations are met as per developer notes and build it (Make the Project)
   * The output by default should be in the root **bin** directory

    * To integrate the plugins into your project, place the selected plugins(both .gdap and .aar file) into ***android/plugins*** directory in your godot project.

 
To understand how to use plugins in Godot refer to [Godot docs](https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html)


### Plugins list

List of plugins or modules:

| Plugin or Module | Description | Supported API Version |
| ---------------- | ----------- | --------------------- |
| [AppNotification](app-notification/README.md) | Plugin to send local notification in android devices | N.A |
| [GooglePlayReview](google-play-review/README.md) | Plugin to include in-app review or review link component in your game | com.google.android.play:review:2.0.1 |
| [GooglePlayBilling](google-play-billing/README.md) | Plugin to integrate google play billing in the game for managing a store | com.android.billingclient:billing:5.1.0 |
| [GooglePlayGameServices](google-play-game-services/README.md) | Plugin to integrate google play game services like achievments, events and others | com.google.android.gms:play-services-games-v2:17.0.0 |


### External Plugins (Reference):
Below are list of plugins which have not been added, as implementation by the user is well supported.

| Plugin or Module | Description | Type |
| ---------------- | ----------- | ---- |
| [godot-sqlite](https://github.com/2shady4u/godot-sqlite) | Godot plugin for integrating SQLite DB into the game | Native |
| [GodotAdMob](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin) | Godot plugin for integrating Admob Ad Network into the game | Plugin |

### Contributing and Support

If you want to contribute please send a note to token.gamedev@gmail.com or raise a pull request.
If you are facing a problem in usage, raise an issue providing details about the problem.

### Developer Notes

      Godot Engine Version(Minimum) - 3.5 (stable)
      Android SDK (Minimum) - 23 (Build.VERSION_CODES.O)
      Android SDK (Target) - 32 (Build.VERSION_CODES.S_V2)
      Java Version(jdk)(Minimum) - 11
      Kotlin version(Minimum) - 1.6.21
      Android Gradle Plugin Version - 7.2.2
      Gradle Version - 7.5.1

If anything else missed out, please send a note 



