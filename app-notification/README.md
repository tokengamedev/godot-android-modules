# App Notification

AppNotification is an android plugin for Godot Game engine.

It enables the app made in Godot to notify the end-user outside the app in android devices.

To understand more about the Notification feature in android, go through the [documentation](https://developer.android.com/guide/topics/ui/notifiers/notifications).

## Basic Usage:

To access the plugin you have to use the standard code for Godot plugin in the code

```gdscript
# Check if the plugin is available
if Engine.has_singleton("AppNotification"):

    # Get a reference to the singleton
    var notification_plugin = Engine.get_singleton("AppNotification")    
```

Example of usage of various features can be referred in the file *[usage/notification_manager.md](usage/notification_manager.gd)*

## Features:

### 1. Posting Notification:

#### a. Immediate Notification
Show a notification immediately. 
  
**Usage:**

```gdscript

# Call to the method for notification
notification_plugin.showNotification(channelId, notificationId, title, message)
    
```
Note: `channelId` is required for android devices with api level 26(Oreo) or higher and `channelId` can be blank for supporting lower api levels:

* For api Level 26(Oreo) or higher a DEFAULT channel will be created if `channelId` is blank.
* For api level lower than 26(Oreo), the `channelId` is ignored even if `channelId` is provided.
* This is applicable for other notification APIs also
  
#### b. Delayed Notification:

Show a notification after certain amount of time.

**Usage:**

```gdscript
# delay (seconds): the time after which the notification will be sent

# Call to the method for notification
notification_plugin.showNotificationAfter(channelId, notificationId, title, message, delay)
    
```

#### c. Repeating Notification:

Show notification at repeating interval of time.

**Usage:**

```gdscript
# Call to the method for setting up notification at regular intervals

# delay (seconds): time after which repetition will start or when the first notification will be posted
# interval (seconds) after which the notification will repeat
# Example:
# If you require a notification to happen at 1300 HRS every day(24hrs). Current time being 1100 HRS 
# then delay = 2 * 3600 and interval = 24 * 3600 
  
notification_plugin.setupRepeatingNotification(channelId, notificationId, title, message, delay, interval)
  
```
### 2. Cancelling Notification:

Cancel notification that are pending or active.
**Usage:**

```gdscript
# To cancel the pending notifications
notification_plugin.cancelNotification(notificationId)

# To Cancel notifications that has not been cancelled by user yet (active).
notification_plugin.cancelActiveNotifications(notificationId)
```


### 3. Channel Management:

Manage channels for notifications.

**Usage:**

```gdscript
# To create/update a channel
notification_plugin.setupNotificationChannel(channel_id, channel_name, importance, channel_description)

# To delete a channel
notification_plugin.removeNotificationChannel(channel_id)
```
Note: ***Channels are available from android API LEVEL 26 (O) and higher***. If the apis are used for devices below API LEVEL 26 then nothing will happen. It is safe to call, irrespective of API Level of android devices.  

This method should be called during start of the application rather when when posting the notification.

### 4. Customize Notification:

Customize the notification to match the game or stand unique.

```gdscript

var option = {"small_icon_id": "my_custom_icon", "color_id": "violet_red", "expandable": 1, "show_when": true }

# To create/update a channel
notification_plugin.setNotificationCustomOptions(notificationId, option)

```
Notes: 
* All the images should be part of the **`/android/build/res/`** directory so that it can be identified by the android app. It can be one file under `drawable` or multiple distributed across `drawable-**dpi` directories.
* small_icon should be white with transparent background as per guidelines from android or else it will display as a solid circle or square.
* color_id should be mentioned in an xml file under **`/android/build/res/values`**
* all ot the attributes inside options are optional.If not provided, then default values will be picked up. Check [NotificationOption](#notificationoption) for more details.


## APIs

### Objects

##### NotificationOption
Holds the options for configuring, how the notification displayed.

- **show_when [Boolean]** (Default: false)
  Controls to show when the notification generated or not.
- **small_icon_id** [String] (Default: "notification_small_ic")
  Holds the name of the image file to display the small notification icon
- **color_id**[String] (Default: "notification_color")
  Holds the id of the color in the resources inside android build directory
- **large_icon_id** [String] (Default: "notification_large_ic")
  Holds he name of the image file to display the large notification icon
- **category** [String] (Default: [NotificationCompat.CATEGORY_STATUS](https://developer.android.com/reference/androidx/core/app/NotificationCompat#CATEGORY_STATUS()))
- Holds the category for the notification. The value should be one of the CATEGORY_* values from [NotificationCompat](https://developer.android.com/reference/androidx/core/app/NotificationCompat)
- **sub_text** [string] (Default: "")
  Holds the addition text displayed between app name and When
- **expandable** [int] (Default: 0)
  Holds the value to customize the display of notification Possible values, NO_CUSTOMIZATION(0), BIG_TEXT(1), BIG_PICTURE(2) 

##### NotificationInfo
Holds the notification info of a notification
- **id [Int]**
  The notification id of the notification.
- **tag [String]**
  The notification tag of the notification.
- **package [String]**
  The name of the package from which the notification is.
- **post_time [Int]**
  The time at which the the notification was posted(Unix Time)
- **channel_id [String]**
  The id of the channel on which the notification was posted.



### Methods

- ##### setupNotificationChannel(channelId, channelName, importance, channelDescription)
  Creates or updates a notification channel. For devices with android api version 26 and higher a channel will be created, or it will be ignored. If the channel already exists, then it will overwrite the channel.
    
  **Parameters**
  - **channelId [String]** Unique Id of the channel
  - **channelName [String]** Display name of the channel
  - **importance [Int]** the importance of the messages through this channel. See [NotificationManager.IMPORTANCE_* for possible values
  - **channelDescription [String]** a descriptive text about the channel
  
- ##### removeNotificationChannel(channel_id)
  Removes the channel from the system for the app.

  **Parameters**
  - **channelId [String]** Unique Id of the channel to be removed
  

- ##### setNotificationCustomOptions(notificationId, option)
  Sets the custom options for the given `notificationId`. This should be called before posting notifications. 
  Any customization values not provided, will take up default values. If  a custom option already exists for the given `notificationId`, then it will replace the option.
    
  **Parameters**
  - **notificationId [Int]** the unique id of the notification
  - **option [[NotificationOption](#notificationoption)]** the custom option to be set for the notification Id.
  
- ##### canPostNotifications(channelId)
  Checks if the user has blocked the notifications for app and/or channel. From `ANDROID.O(26)` version or higher, user has abilities to block the notifications outside of app, in the settings.
  **Returns**
  - **[Boolean]** true if blocked by user.
  
- ##### showNotification(channelId, notificationId, title, message)
  Shows a Notification immediately
  Note: if channel is not found then notification is sent through default channel

  **Parameters**
  - **channelId [String]** the id of the channel the notification has to be sent
  - **notificationId [Int]** the unique id of the notification (can be used to cancel also)
  - **title [String]** the title of the notification
  - **message [String]** the message of the notification
  
  
- ##### showNotificationAfter(channelId, notificationId, title, message, delay)
  Shows a notification after a certain time delay, but only once.
  
  **Parameters**
  - **channelId [String]** the id of the channel the notification has to be sent
  - **notificationId [Int]** the unique id of the notification (can be used to cancel also)
  
  - **title [String]** the title of the notification
  - **message [String]** the message of the notification
  - **delay [Int]** the amount of time(in seconds) after which the notification will be sent. It is an approximate value depending upon the device state.
  

- ##### setupRepeatingNotification(channelId, notificationId, title, message, delay, interval)
  Sets up a notification schedule, which will make the app post notification at regular interval of time. 

  **Parameters**
  - **channelId [String]** the id of the channel the notification has to be sent
  - **notificationId [Int]** the unique id of the notification (can be used to cancel also)
  - **title [String]** the title of the notification
  - **message [String]** the message of the notification
  - **delay [Int]** the amount of time(in Seconds) after which the first notification will be sent. It is an approximate value depending upon the device state
  - **interval [Int]** the duration of time after which the notification will be sent again. It is an approximate value depending upon the device state
  
  
- ##### cancelNotification(notificationId)
  Cancels any pending notification. It may be one time or it may be repeating.

  **Parameters**
  - **notificationId [Int]** the id of the notification to be cancelled


- ##### cancelActiveNotifications(notificationId)
  Cancels any active notifications that has not been cancelled by user.

  **Parameters**
  - **notificationId [Int]** the id of the notification to be cancelled

- ##### getActiveNotifications()
  Gets all the active notifications that has not been cancelled by the user.

  **Returns**
  - **{notifications: [[NotificationInfo](#notificationinfo)]**} summary details of all the active notifications

### Signals
   - None

## Developer Notes:

|             | Minimum | Maximum |
|-------------|---------|---------|
| Android SDK | 23      | 32      |
| Java/JDK    | 11      |         |
| Kotlin      | 1.6.21  |         | 

    
  