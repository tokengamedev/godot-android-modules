# Godot Android Modules

Godot android modules is a free and open source collection of android plugins for Godot Game engine. 
It is the list of all commonly used plugins like billing, notifications and others.

The goals of the repository are:
1. Create a list of commonly used plugins in one place.
2. Ability to pick and choose from the plugins. (Less unwanted code / plugins)
3. Use latest APIs as much as possible
4. Automate gdap (v1)/script(v2) file generation.


> :zap: **Important GAML Update:**
> 
> All the plugins related to Advertisement display has been deprecated.  It includes ApplovinMax and Ogury Consent Manager.
>  The reason being the complexity of the advertisement media networks and multitude of SDKs. 
> This requires a separate approach and hence a separate plugin/repo altogether  
> 


### ⚙️Installation and usage

There are two ways you can get the plugins for integrating into your game:


##### Releases:
   * Get the latest zip or tar package from the release section and extract it.

   * Pick and choose whichever plugins you want to integrate.   

   * To integrate the plugins into your project, place the selected plugins(both .gdap and .aar file) into ***```res://android/plugins```*** directory in your godot project.


##### Manual Build:
   * Download the source code (clone or zip)
   * Open the Project with Android Studio (Giraffe or higher)
   * Ensure all the configurations are met as per developer notes and build it (Make the Project)
   * The output by default should be in the root **bin** directory

    * To integrate the plugins into your project, place the selected plugins(both .gdap and .aar file) into ***android/plugins*** directory in your godot project.

 
To understand how to use plugins in Godot refer to [Godot docs](https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html)


### 📓Plugins list

List of plugins or modules:

| Plugin or Module | Description | Supported API Version |
|------------------|-------------|-----------------------|
| ~~[AppLovinMax](applovin-max/README.md)~~ | **Deprecated**. <br>If required there is an official plugin available from Applovin [here](https://godotengine.org/asset-library/asset/2141). | com.applovin:applovin-sdk:11.10.1 |
| [AppNotification](app-notification/README.md) | Plugin to send local notification in android devices | N.A |
| [AndroidPermission](android-permission/README.md) 🆕| Plugin to query or request dangerous permissions in android  | N.A |
| [GooglePlayReview](google-play-review/README.md) | Plugin to include in-app review or review link component in your game | com.google.android.play:review:2.0.1 |
| [GooglePlayBilling](google-play-billing/README.md) | Plugin to integrate google play billing in the game for managing a store | com.android.billingclient:billing:6.2.0 |
| [GooglePlayGameServices](google-play-game-services/README.md) | **On Hold** <br>Plugin to integrate google play game services. | com.google.android.gms:play-services-games-v2:20.0.0 |
| ~~[OguryConsentManager](ogury-consent-manager/README.md)~~| **Deprecated**. <br>Plugin to integrate consent management from Ogury | co.ogury:ogury-sdk:5.5.0 |


### :notebook_with_decorative_cover:External Plugins (Reference):
Below are list of plugins which have not been added, as implementation by the user is well supported.

| Plugin or Module                                                     | Description                                                 | Type   |
|----------------------------------------------------------------------|-------------------------------------------------------------|--------|
| [godot-sqlite](https://github.com/2shady4u/godot-sqlite)             | Godot plugin for integrating SQLite DB into the game        | Native |
| [GodotAdMob](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin) | Godot plugin for integrating Admob Ad Network into the game | Plugin |
| [AppLovinMax](https://github.com/AppLovin/AppLovin-MAX-Godot) | Godot plugin for integrating AppLovin MAX Ad Network into the game | Plugin |
| [Godot Play game services](https://github.com/Iakobs/godot-play-game-services) | Google play game services integration for Godot | Plugin |


### 💟Contributing and Support

If you want to contribute please send a note to token.gamedev@gmail.com or raise a pull request.
If you are facing a problem in usage, raise an issue providing details about the problem.

### ✍️ Developer Notes

|                       | Versions                                   | 
|-----------------------|--------------------------------------------|
| Godot Engine Version  | 3.x, 4.x (< 4.2) - V1                      |
| Android SDK (Minimum) | 24 (Build.VERSION_CODES.N)                 |
| Android SDK (Target)  | 34 (Build.VERSION_CODES.UPSIDE_DOWN_CAKE)  |
| Java Version(jdk)     | 17                                         |
| Kotlin version(JVM)   | 1.9.21                                     |
| Android Gradle Plugin | 8.2.0                                      |
| Gradle                | 8.2                                        |


If anything else missed out, please send a note 



