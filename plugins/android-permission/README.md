# Android Permission

Android Permission is an android plugin for Godot Game engine.
It allows the game built with android to check and/or request runtime permissions in android. 
Permissions are required in android for accessing user sensitive data or actions.

To learn more about android permissions, checkout [here](https://developer.android.com/guide/topics/permissions/overview)

Ensure you follow the best practices of Android permission, especially when and how to request permissions, [here](https://developer.android.com/training/permissions/usage-notes)

> [!NOTE] 
> There are methods to request permission inside Godot core and check granted permissions, but it can be improved, hence to address some gaps specifically for android, this module exists.  


### Basic Usage:

To access the plugin you have to use the standard code for Godot plugin

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("AndroidPermission"):

    # Get a reference to the singleton
    var permission_plugin = Engine.get_singleton("AndroidPermission")    
```

### Features:


#### 1. Check Permission:

Game can check certain permission(s) has been granted by the user or not.
There are multiple ways the request can be done as given below in the code snippet"

The result from permission check is either GRANTED, DENIED or DENIED_SHOW_RATIONALE

In case there is a failure then InStore review process will happen.

**Usage:**

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("AndroidPermission"):

    # Get a reference to the singleton
    var permission_plugin = Engine.get_singleton("GooglePlayReview")

    # 1. Request using integer ID.(IDs are defined by Plugin and not Android)
    var permission_id = 10 # Manifest.permission.ACCESS_MEDIA_LOCATION
    var result = permission_plugin.checkPermission(permission_id)

    # 2. Request using permission String
    var permission_string = "android.permission.ACCESS_MEDIA_LOCATION"
    var result = permission_plugin.checkPermissionString(permission_string)
   
```


#### 2. Request Permission

Game can request a certain permission, which may or may not be granted by the user. The result will comeback as a callback through signal

**Usage:**

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("AndroidPermission"):

    # Get a reference to the singleton
    var permission_plugin = Engine.get_singleton("AndroidPermission")

    # 1. Request using integer ID.(IDs are defined by Plugin and not Android)
    var permission_id = 10 # Manifest.permission.ACCESS_MEDIA_LOCATION
    permission_plugin.requestPermission(permission_id)

    # 2. Request using permission String
    var permission_string = "android.permission.ACCESS_MEDIA_LOCATION"
    permission_plugin.requestPermissionString(permission_string)

    # Callback to receive request
    permission_plugin.permission_request_completed.connect(_on_permission_request_completed)
    
```

### APIs

#### Enum:

- ##### Permission ID:
  | Permission | ID | String | Android Version|
  | - | - | - | -|
  | ACCESS_COARSE_LOCATION | 1 | _android.permission.ACCESS_COARSE_LOCATION_ | 1 |
  | ACCESS_FINE_LOCATION | 2 | _android.permission.ACCESS_FINE_LOCATION | 1 |
  | READ_EXTERNAL_STORAGE | 3 | _android.permission.READ_EXTERNAL_STORAGE | 1 |
  | READ_CONTACTS | 4 | _android.permission.READ_CONTACTS | 1 |
  | RECORD_AUDIO | 5 | _android.permission.RECORD_AUDIO | 1 |
  | ACCESS_MEDIA_LOCATION | 10 | _android.permission.ACCESS_MEDIA_LOCATION | 29 |
  | POST_NOTIFICATIONS | 20 | _android.permission.POST_NOTIFICATIONS | 33 |
  | READ_MEDIA_IMAGES | 21 | _android.permission.READ_MEDIA_IMAGES | 33 |
  | READ_MEDIA_AUDIO | 22 | _android.permission.READ_MEDIA_AUDIO | 33 |
  | POST_NOTIFICATIONS | 23 | _android.permission.READ_MEDIA_VIDEO | 33 |

> [!NOTE] 
> This is a curated list for most used permissions in a game. If it is not listed here, request can always be done through string approach

#### Methods

- ##### checkPermission(id: int)
  Checks if a certain permission has been provided for the app/game.

  **Parameters**
  - **id [int]** one of the permission id mentioned  in Permission ID enum
  
  **Returns**
  - [int] - One of the following integer value GRANTED (0), DENIED (1), DENIED_SHOW_RATIONALE(2) or INVALID_ID (-1)
  
  
- ##### checkPermissionString(permission_string: String)
  Checks if a certain permission has been provided for the app/game.
 
  **Parameters**
  - **permission_string [String]**  One of the runtime permission string mentioned in the [Manifest.Permission]https://developer.android.com/reference/android/Manifest.permission.

  **Returns**
  - **[int]** - One of the following integer value GRANTED (0), DENIED (1), DENIED_SHOW_RATIONALE(2) or INVALID_ID (-1)


- ##### requestPermission(id: int)
  Requests certain permission from the user for the app/game.

  **Parameters**
  - **id [int]** one of the permission id mentioned  in Permission ID enum
  
  **Returns**
  - **[int]** - INVALID_ID (-1) or OK(0)
  
  
- ##### requestPermissionString(permission_string: String)
  Requests certain permission from the user for the app/game.
 
  **Parameters**
  - **permission_string [String]**  One of the runtime permission string mentioned in the [Manifest.Permission](https://developer.android.com/reference/android/Manifest.permission).

  **Returns**
  - Nothing

#### Signals

- ##### permission_request_completed
  Raised when the permission request asked from the user  has been completed
  **Parameters**

  - **id[int]** Id of the permission requested, if used string method for request then it will be 0,
  - **permission_string[String]** the permission string used for asking permission
  - **result[int]** GRANTED(0), DENIED (1)

