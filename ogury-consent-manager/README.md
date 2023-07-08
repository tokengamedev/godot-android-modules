# Ogury Consent Manager

Ogury consent manager is an android plugin for Godot Game engine. 

It allows the user to integrate choice manager from ogury to handle consent management.

To understand more about Ogury Choice Manager https://ogury-ltd.gitbook.io/choice-manager-android/

## Pre-Requisites and Plugins:

Installing the plugin by itself will not work, Following steps needs to be done also:
1. Create an Account in [Ogury dashboard](https://publishers.ogury.co/identity/login?redirect=%2F)
2. Register the app/game.
3. Configure the Choice Manager for data usage and how the dialogs will be displayed to user.
   
### Basic Usage:

To access the plugin you have to use the standard code for Godot plugin

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("OguryConsentManager"):

    # Get a reference to the singleton
    var consent_manager = Engine.get_singleton("OguryConsentManager")    
```

### Features:

#### 1. Manage Consent:

Consent will be required based on the configuration and Geo location, e.g., CCPA for california residents or GDPR for european users. This will be automatically managed by Ogury and dialog will be shown if required to for the user.
Apart from taking consent for user data usage, it also handles the storing and synchronization, in case of changes.
This is done by calling a single method `ask`
Signals `consent_sync_completed` or `consent_sync_failed` will be generated accordingly.

**Usage:**

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("OguryConsentManager"):

    # Get a reference to the singleton
    var consent_manager = Engine.get_singleton("OguryConsentManager")

    # Call during the start of the application once
    # asset_key is of the format "OGY-XXXXXXXXXXXX"
    consent_manager.ask(asset_key)
```

#### 2. Edit Consent:
Ability to edit existing consent status for the user, in case the user wants to change it. This has to be called manually when a user requests for it.

Note: 
1. Consent synchronization should have happened before the call, otherwise default values will be available.
2. Check if the consent is editable before editing.


**Usage:**

```gdscript
# Check if the consent is editable
if consent_manager.isEditable():

    # Forces to show the dialog for editing 
    consent_manager.edit()
```

#### 3. Check Consent:
Ability to check consent provided by the user.

Note: Consent synchronization should have happened before the calls, otherwise default values will available.

**Usage:**

```gdscript
    # checks if gdpr applies to the user
    consent_manager.gdprApplies()
```

### APIs


### Objects

##### OguryAnswer
Holds the answer for Ogury consent synchronization.

- **name [String]**
  Holds the name
- **value [Int]**
  Holds the value of the consent. It will be one of the following values:
  - NO_ANSWER (0)
  - FULL_APPROVAL (1)
  - PARTIAL_APPROVAL (2)
  - REFUSAL (3)
  - CCPAF_SALE_ALLOWED (10)
  - CCPAF_SALE_DENIED (11)
  

##### OguryError
Holds the error info for Ogury consent synchronization.

- **message [String]**
  Holds the description of the message
- **code [Int]**
  Holds the error code for the error. It may be one of the following values:
  - NO_INTERNET_CONNECTION (0)
  - ASSET_KEY_UNKNOWN (1)
  - BUNDLE_NOT_MATCHING (2)
  - SERVER_NOT_RESPONDING (3)
  - SYSTEM_ERROR (4)
  - REGION_RESTRICTED (1000)
  - TIMEOUT_ERROR (1002)
  - FORM_ERROR (1003)
  - PARSING_ERROR (1004)
  - EDIT_DISABLED_DEVICE_ID_RESTRICTED (1007)
  - EDIT_DISABLED_GEORESTRICTED_USER (1008)
  


#### Methods

- ##### ask(asset_key)
  Synchronizes the consent of the user (if applies with the server).

  - **asset_key [String]** ID as registered in the Ogury dashboard. This is of the format "OGY-XXXXXXXXXXXX"

  
- ##### edit()
  Forces the consent manager to edit the consent status for the user, by showing the dialog. It will do nothing if it is not editable.


- ##### isEditable()
  Checks if the consent can be edited by the user. e.g., Consent status is not editable outside GDPR and CCPA applicable region.
  **Returns**
  - **[Boolean]** true if editable else not.

- ##### gdprApplies()
  Checks if the gdpr flag is applicable for the user and app as per the consent status given by user. .
  **Returns**
  - **[Boolean]** true if user is in the gdpr region and data usage consent is not given.

- ##### ccpaApplies()
  Checks if the ccpa flag is applicable for the user and app as per the consent status given by user.
  **Returns**
  - **[Boolean]** true if user is in the ccpa region and data usage consent is not given.


**Signals**
  - ##### consent_sync_completed(answer)
    Raised when the synchronization has been completed successfully.

    **Parameters**
    - **answer [[OguryAnswer](#oguryanswer)]** Result of Synchronization


  - ##### consent_sync_failed(error)
    Raised when the synchronization has failed.
    **Parameters**
    - **error [[OguryError](#oguryerror)]** The error



### Developer Notes:

|             | Minimum  | Maximum |
|-------------|----------|---------|
| Android SDK | 23       | 32      |
| Java/JDK    | 11       |         |
| Kotlin      | 1.10.1   |         | 

- Library Dependencies:
    - co.ogury:ogury-sdk:5.5.0
  