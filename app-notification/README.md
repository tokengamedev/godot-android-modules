# App Notification

AppNotification is an android plugin for Godot Game engine.

It enables the app made in Godot to notify the end-user outside the app in android devices. It only supports Local notification.

To understand more about the Notification feature in android, go through the [documentation](https://developer.android.com/guide/topics/ui/notifiers/notifications).

### Basic Usage:

To access the plugin you have to use the standard code for Godot plugin in the code

```gdscript
# Check if the plugin is available
if Engine.has_singleton("AppNotification"):

    # Get a reference to the singleton
    var notification_plugin = Engine.get_singleton("AppNotification")    
```

### Features:

Example of usage of various features can be referred in the file *[usage/notification_manager.md](usage/notification_manager.gd)*

#### 1. Immediate Notification:

Shows a notification immediately. 
  
Note: CHANNEL_ID can be blank. If CHANNEL_ID is blank:
* For android devices with api Level 26(Oreo) or higher a DEFAULT channel will be created for notification.
* For android devices with api level lower than 26(Oreo), the channel id is ignored even if channel_id is provided.
This is applicable for other notification APIs a;so

**Usage:**

```gdscript

# Call to the method for notification
notification_plugin.showNotification(CHANNEL_ID, NOTIFICATION_ID, title, message)
    
```
#### 2. Delayed Notification:

Shows a notification after certain interval of time.
The delay has to be in seconds (Integer)

**Usage:**

```gdscript
# Call to the method for notification
notification_plugin.showNotificationAfter(CHANNEL_ID, NOTIFICATION_ID, delay, title, message)
    
```

#### 3. Repeating Notification:

Shows notifications at repeating interval of time.
There are two parts to it, when to start(delay) and interval for repeat(interval)

**Usage:**

```gdscript
# Call to the method for setting up notification

# delay = time in seconds after which repeatition start
# interval = time in seconds after which the notification will repeat
# e.g., If a 24 hrs repeating notification at 1300 HRS is required and current time is 1100 HRS
# then delay = 2 * 3600 and interval = 24 * 3600 
  
notification_plugin.setup_repeating_notification(CHANNEL_ID, NOTIFICATION_ID, delay, inetrval, title, message)
  
```
#### 4. Channel Management:

Ability to create channels for communication and also add customizations to it.
***Channels are available from android API LEVEL 26 (O) and higher***. If the apis are used for devices below API LEVEL 26 then nothing will happen. It is safe to call, irrespective of API Level of android devices.  

These methods should be called during start of the application rather when sending the notification.

**Usage:**

```gdscript
# To create a channel
notification_plugin.setupNotificationChannel(channel_id, channel_name, importance, channel_description)

# To customise a channel
notification_plugin.setChannelCustomOptions(channel_id, show_when, small_icon_id, large_icon_id, notification_color_id)
```

### Customization Notes:

You can customise the following:
1. **show_when:** Boolean (default: false) - controls to show when the notification arrived or not.
2. **small_icon_id:** String (default: "notification_small_ic" | "icon") - controls the display of the small notification icon.
   * It is must required for notification to work. 
   * The icon should be provided as image under the location **"/android/build/res/drawable"**
   * The icon should be white with transparent background as per guidelines from android or else it will display as a solid circle or square.
   * It will colored based on the color_id customization
3. **large_icon_id:** String (default: "notification_large_ic") - controls the display of the large notification icon
   * The icon should be provided as image under the location **"/android/build/res/drawable"**
4. **color_id:** String(default: "notification_color") - Controls the color of the small notification icon.  
   * It is the id of the color mentioned in the xml file, and not the Hex code of the color.
   * The color should be mentioned in any xml file stored in the **"/android/build/res/values"**

### APIs

**Methods**
- **getDefaultChannelId()**
  Gets the id of the default channel
  
  **Parameters**
  - None
  
  **Returns:**
  - **[String]** Id of the default channel

- **setupNotificationChannel(channelId, channelName, importance, channelDescription)** 
  Creates a notification channel. For devices with android api version 26 and higher a channel will be created.
  
  **Parameters**
  - **channelId [String]** Unique Id of the channel
  - **channelName [String]** Display name of the channel
  - **importance [Int]** the importance of the messages through this channel. See [NotificationManager].IMPORTANCE_* for possible values
  - **channelDescription [String]** a descriptive text about the channel
  
  **Returns:**
  - **[Int]** NO_ERROR(0), INCOMPATIBLE_OS_VERSION(-1) or CHANNEL_ALREADY_EXISTS(1)

- **setChannelCustomOptions(channelId, showWhen, smallIconId, largeIconId, colorId)**
  Setup the channel customization to customize icons and notification color. Any customization not provided (i.e blank) will take up default values.
  
  **Parameters**
  - **channelId [String]** the channel id on which the customization is applied
  - **showWhen [Boolean]** will display showWhen or not
  - **smallIconId [String]** displays the small notification icon for the channel
  - **largeIconId [String]** displays the large icon for the channel
  - **colorId [String]** colorid of the color of the small notification Icon
  
  **Returns:**
  - **[Int]** NO_ERROR(0), INCOMPATIBLE_OS_VERSION(-1) or NO_CHANNEL_AVAILABLE(2)


- **showNotification(channelId, notificationId, title, message)**
  Shows a Notification immediately

  **Parameters**
  - **channelId [String]** the id of the channel the notification has to be sent
  - **notificationId [Int]** the unique id of the notification (can be used to cancel also)
  - **title [String]** the title of the notification
  - **message [String]** the message of the notification
  
  **Returns:**
  - None
  
- **showNotificationAfter(channelId, notificationId, delay, title, message)** 
  Shows a notification after a certain time, only once. 
  
  **Parameters**
  - **channelId [String]** the id of the channel the notification has to be sent
  - **notificationId [Int]** the unique id of the notification (can be used to cancel also)
  - **delay [Int]** the amount of time(in seconds) after which the notification will be sent. It is an approximate value depending upon the device state.
  - **title [String]** the title of the notification
  - **message [String]** the message of the notification
  
  **Returns:**
  - None
  

- **setupRepeatingNotification(channelId, notificationId, delay, interval, title, message)**
  Sets up a repeating notification schedule

  **Parameters**
  - **channelId [String]** the id of the channel the notification has to be sent
  - **notificationId [Int]** the unique id of the notification (can be used to cancel also)
  - **delay [Int]** the amount of time(in Seconds) after which the Notification repetition will start.
  - **interval [Int]** the duration after which the notification will happen again. It is an approximate value depending upon the device state
  - **title [String]** the title of the notification
  - **message [String]** the message of the notification
  
  **Returns:**
  - None
  
- **cancelNotification(notificationId)**
  Cancels any pending notification. It may be one time or it may be repeating.
  **Parameters**
  - **notificationId [Int]** the id of the notification to be cancelled


**Signals**
  - None

### Developer Notes:

    - Android SDK:  Minimum - 23, Target - 32
    - Java/JDK: 11 
    - Kotlin: 1.6.21 (Minimum)

  