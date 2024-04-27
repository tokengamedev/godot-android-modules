<p align="center">
  <a href="#">
    <img src="./assets/gaml%20logo.svg" width="128" alt="GAML logo">
  </a>
</p>

# Godot Android Modules

Godot android modules is a free and open source collection of android plugins for Godot Game engine. 
It is the list of all commonly used plugins like billing, notifications and others.

The goals of the repository are:
1. Create a list of commonly used plugins in one place.
2. Ability to pick and choose from the plugins. (Less unwanted code / plugins)
3. Use latest APIs as much as possible
4. Automate script(v2) file generation. To check for (v1) check out the v1 branch


> :zap: **Important GAML Update:**
> 
> 1. All the plugins related to Advertisement display has been deprecated.  It includes ApplovinMax and Ogury Consent Manager.
>  The reason being the complexity of the advertisement media networks and multitude of SDKs. This requires a separate approach and hence a separate plugin/repo altogether
> 2. GAML has been split into two branches **v1** - for older V1 plugin architecture and **main** for V2  Plugin architecture
> 3. The versions have changed again due to plugin versions. Going forward for v1 plugins it will be 1.\*.* and v2 plugins it will be 2.\*.*. Also all plugins will have same version


### ⚙️Installation and usage

There are two ways you can get the plugins for integrating into your game:


##### Releases:
   * Get the latest zip or tar package from the release section and extract it.

   * Pick and choose whichever plugins you want to integrate.   

   * To integrate the plugins into your project, place the selected plugins folder (containing both script files and .aar file) into ***```res://addons```*** directory in your godot project.


##### Manual Build:
   * Download the source code (clone or zip)
   * Open the Project with Android Studio (Giraffe or higher)
   * Ensure all the configurations are met as per developer notes and build it (Make the Project)
   * The output by default should be in the root **bin** directory
   * To integrate the plugins into your project, place the selected plugins folder (containing both script files and .aar file) into ***```res://addons```*** directory in your godot project.
  
##### Assets Library:
   You can fetch the GAML plugin from the assets library. 
   TODO: Update the Github and assets library link

 
To understand how to use plugins in Godot refer to [Godot docs](https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html)


### 📓Plugins list

List of plugins or modules:

| Plugin or Module | Description | Supported API Version |
|------------------|-------------|-----------------------|
| [AppNotification](app-notification/README.md) | Plugin to send local notification in android devices | N.A |
| [AndroidPermission](android-permission/README.md) | Plugin to query or request runtime permissions in android  | N.A |
| [GooglePlayReview](google-play-review/README.md) | Plugin to include in-app review or review link component in your game | com.google.android.play:review:2.0.1 |
| [GooglePlayBilling](google-play-billing/README.md) | Plugin to integrate google play billing in the game for managing a store | com.android.billingclient:billing:6.2.0 |
| [GooglePlayGameServices](google-play-game-services/README.md) | Plugin to integrate google play game services. | com.google.android.gms:play-services-games-v2:20.0.0 |


### :notebook_with_decorative_cover:External Plugins (Reference):
Below are list of plugins which have not been added, as implementation by the user is well supported.

| Plugin or Module                                                     | Description                                                 | Type   |
|----------------------------------------------------------------------|-------------------------------------------------------------|--------|
| [godot-sqlite](https://github.com/2shady4u/godot-sqlite)             | Godot plugin for integrating SQLite DB into the game        | Native |
| [GodotAdMob](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin) | Godot plugin for integrating Admob Ad Network into the game | Plugin |
| [AppLovinMax](https://github.com/AppLovin/AppLovin-MAX-Godot) | Godot plugin for integrating AppLovin MAX Ad Network into the game | Plugin |
| [Godot Play game services](https://github.com/Iakobs/godot-play-game-services) | Google play game services integration for Godot | Plugin |


### 💟Contributing and Support

If you want to contribute please send a note to [Token GameDev](token.gamedev@gmail.com) or raise a pull request.
If you are facing a problem in usage, raise an issue providing details about the problem.

Should this aid in your development and prove beneficial, feel free to spread the word. 
Your support through a modest donation on ☕ [Buying a coffee](https://www.buymeacoffee.com/tokengamedev) would be greatly valued and appreciated.


### ✍️ Developer Notes

|                       | Versions                                   | 
|-----------------------|--------------------------------------------|
| Godot Engine Version  | 4.2.2+                      |
| Android SDK (Minimum) | 24 (Build.VERSION_CODES.N)                 |
| Android SDK (Target)  | 34 (Build.VERSION_CODES.UPSIDE_DOWN_CAKE)  |
| Java Version(jdk)     | 17                                         |
| Kotlin version(JVM)   | 1.9.10                                     |
| Android Gradle Plugin | 8.2.0                                      |
| Gradle                | 8.2                                        |
