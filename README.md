# Godot Android Modules

Godot android modules is a free and open source collection of android plugins for Godot Game engine. 
It is the list of all commonly used plugins like billing, notifications and others.

The goals of the repository are:
1. Create a list of commonly used plugins in one place.
2. Ability to pick and choose from the plugins. (Less unwanted code)
3. Use latest APIs as much as possible
4. Automate gdap file generation.


> :zap: **Important Versioning Update:**
> 
> The version numbering of bundle releases has been changed from semantic versioning(e.g., 1.1.0, 1.1.1) to numeric versioning(e.g., 1, 2, 3).
> 
> It is due to challenge of identifying the next version for bundle releases.
> 
> This does not impact the semantic versioning of the individual modules. The version number of the release will signify the next bundle release, nothing more, nothing less.


### ⚙️Installation and usage

There are two ways you can get the plugins for integrating into your game:


##### Releases:
   * Get the latest zip or tar package from the release section and extract it.

   * Pick and choose whichever plugins you want to integrate.   

   * To integrate the plugins into your project, place the selected plugins(both .gdap and .aar file) into ***```res://android/plugins```*** directory in your godot project.


##### Manual Build:
   * Download the source code (clone or zip)
   * Open the Project with Android Studio (Chipmunk or higher)
   * Ensure all the configurations are met as per developer notes and build it (Make the Project)
   * The output by default should be in the root **bin** directory

    * To integrate the plugins into your project, place the selected plugins(both .gdap and .aar file) into ***android/plugins*** directory in your godot project.

 
To understand how to use plugins in Godot refer to [Godot docs](https://docs.godotengine.org/en/stable/tutorials/platform/android/android_plugin.html)


### 📓Plugins list

List of plugins or modules:

| Plugin or Module                                              | Description                                                                       | Supported API Version                                |
|---------------------------------------------------------------|-----------------------------------------------------------------------------------|------------------------------------------------------|
| [AppLovinMax](applovin-max/README.md)                 | Plugin to include mediation service for Ads from ApplovinMax                              | com.applovin:applovin-sdk:11.10.1                                                  |
| [AppNotification](app-notification/README.md)                 | Plugin to send local notification in android devices                              | N.A                                                  |
| [GooglePlayReview](google-play-review/README.md)              | Plugin to include in-app review or review link component in your game             | com.google.android.play:review:2.0.1                 |
| [GooglePlayBilling](google-play-billing/README.md)            | Plugin to integrate google play billing in the game for managing a store          | com.android.billingclient:billing:6.0.1              |
| [GooglePlayGameServices](google-play-game-services/README.md)  🚫| (**On Hold**)Plugin to integrate google play game services like achievments, events and others | com.google.android.gms:play-services-games-v2:17.0.0 |
| [OguryConsentManager](ogury-consent-manager/README.md) | Plugin to integrate consent management from Ogury | co.ogury:ogury-sdk:5.5.0 |


### :notebook_with_decorative_cover:External Plugins (Reference):
Below are list of plugins which have not been added, as implementation by the user is well supported.

| Plugin or Module                                                     | Description                                                 | Type   |
|----------------------------------------------------------------------|-------------------------------------------------------------|--------|
| [godot-sqlite](https://github.com/2shady4u/godot-sqlite)             | Godot plugin for integrating SQLite DB into the game        | Native |
| [GodotAdMob](https://github.com/Shin-NiL/Godot-Android-Admob-Plugin) | Godot plugin for integrating Admob Ad Network into the game | Plugin |

### 💟Contributing and Support

If you want to contribute please send a note to token.gamedev@gmail.com or raise a pull request.
If you are facing a problem in usage, raise an issue providing details about the problem.

### ✍️ Developer Notes

|                       | Minimum                    | Target/Compile                |
|-----------------------|----------------------------|-------------------------------|
| Godot Engine Version  | 3.5(Stable)                |                               |
| Android SDK           | 23 (Build.VERSION_CODES.O) | 32 (Build.VERSION_CODES.S_V2) |
| Java Version(jdk)     | 11                         |                               |
| Kotlin version        | 1.10.0                      |                               |
| Android Gradle Plugin | 7.4.2                      |                               |
| Gradle                | 7.6                        |                               |


If anything else missed out, please send a note 



