# Google Play Game Services (V2)

Google Play Game Services is an android plugin for Godot Game engine.
It allows the user to integrate google play game services to provide access to popular gaming features such as achievements, leaderboards, events and others in your games. 

These Features are managed through Google play console.

For more details about Google play game services, you can go through [here](https://developers.google.com/games/services)


### Basic Usage:

To access the plugin you have to use the standard code for Godot plugin

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("GooglePlayGameServices"):

    # Get a reference to the singleton
    var billing = Engine.get_singleton("GooglePlayGameServices")    
```

### Features:

The features supported areas follows:
* SignIn and  Player basic Info



### Additional Configuration:
For the plugin to work following changes has to be done
1. Setup the Game project and add proper credentials for the project. Follow the steps mentioned [here](https://developers.google.com/games/services/console/enabling)
2. In your app's `AndroidManifest.xml` file, add the following `<meta-data>` element and attributes to the `<application>` element:

```xml
<manifest>
  <application>
    <meta-data android:name="com.google.android.gms.games.APP_ID"
               android:value="@string/game_services_project_id"/>
  </application>
</manifest>
```
3. In your `res/values/strings.xml` file, add a string resource reference and set your project ID as the value. In Google Play Console, you can find your project ID under your game name in the Configuration page. For example:


```xml
<!-- res/values/strings.xml -->
<resources>
  <!-- Replace 0000000000 with your game’s project id. Example value shown above.  -->
  <string translatable="false"  name="game_services_project_id"> 0000000000 </string>
</resources>
```


### APIs:

#### Objects:

- ##### Player:
  Represents the player as represented by the game service
  - **player_id [String]** - Id of the player
  - **title [String]** - Title of the player
  - **display_name [String]** - Name as displayed for the player.

#### Methods

- ##### isReady()
  Checks if the user signed in into the game services or not.

  **Returns:**
  - **[Boolean]** true if signed in else false


- ##### signIn()
  Sign in the player, if the player is not signed in. This method is there for specific cases, where automatic signin has not happened.

  ***Ideally this method is not required***

- ##### fetchPlayerInfo()
  Invokes the process of getting the player info from the services. [player_info_fetched](#player_info_fetched-player_info) signal is raised when the player info is available or not.

  ***Ideally this method is not required***

#### Signals

- ##### sign_in_success()
  Raised when the signed in is successful. If the game is integrated successfully, then this signal will be raised automatically on the launch of the game.


- ##### sign_in_failed()
  Raised when the signed in is not successful. If the game is integrated successfully, then this signal will be raised automatically on the launch of the game.

- ##### player_info_fetched (player_info)
  Raised when the player info is fetched from the services.

  **Parameters**
  - **player_info [Player]** The player data if the fetch has been successful else empty dictionary

### Developer Notes:

|             | Minimum  | Maximum |
|-------------|----------|---------|
| Android SDK | 23       | 32      |
| Java/JDK    | 11       |         |
| Kotlin      | 1.8.0    |         | 

- Library Dependencies:
  - com.google.android.gms:play-services-games-v2:17.0.0




