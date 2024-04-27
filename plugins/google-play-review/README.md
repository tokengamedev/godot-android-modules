# Google Play Review 

GooglePlayReview is an android plugin for Godot Game engine.

This plugin allows the app/game to launch the review flow of the app/game 
from inside, without losing the context of app.(In-App review).
It also provides a way to launch Google Play app for the game to do review(In-Store review) 

![Review Flow](../../assets/review-flow.jpg)
courtesy: developer.android.com
### Basic Usage:

To access the plugin you have to use the standard code for Godot plugin

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("GooglePlayReview"):

    # Get a reference to the singleton
    var review_plugin = Engine.get_singleton("GooglePlayReview")    
```

### Features:

#### 1. In-App review:

The app will launch an In-App review flow to review the app without leaving the context of current app. The benefit of this process is user does not have to go out of context of app and have signals to handle when review is completed or error has happened.

Note: In case there is a failure to launch may start the In-Store review process.

**Usage:**

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("GooglePlayReview"):

    # Get a reference to the singleton
    var review_plugin = Engine.get_singleton("GooglePlayReview")

    # connect to flow_completed signal
    review_plugin.connect("flow_completed", _self, "_on_review_flow_completed")

    # Call to the appropriate method for launching review flow
    review_plugin.launchInAppReview()
    
```

> ⚠️ **Limitations:**
> Sometimes nothing happens on invoking the flow:
> It is a very common problem, which mostly happens due to number of review submitted limitations by Google play. Please ensure you go through the google play in-app review process [here](https://developer.android.com/guide/playcore/in-app-review) to understand more and work accordingly.



#### 2. In-Store Review:
The app will invoke a link which will start the Google play app and open the app in it for review. In case it fails (like when google play is updating itself). It will launch the play store in browser.

**Usage:**

```gdscript
# Check if the Plugin is available
if Engine.has_singleton("GooglePlayReview"):

    # Get a reference to the singleton
    var review_plugin = Engine.get_singleton("GooglePlayReview")

    # Call to the appropriate method for launching review flow
    review_plugin.launchInStoreReview()
    
```

### APIs

#### Methods

- ##### launchInAppReview()
  Launches the in-app review flow within the app.

  
- ##### launchInStoreReview()
   Launches the google play store app and opens the app for providing review.
    
 

**Signals**
  - ##### flow_completed
    Raised when the review flow is completed. It is called only for InApp Review flow.

  - ##### flow_launch_error(errorCode)
    Raised when the review flow launch failed. It is called only for InApp Review flow. This will not be raised if the review launch was successful but window did not show.
    **Parameters:**
    - **errorCode[int]** Corresponds to the error code mentioned in the enum [here](https://developer.android.com/reference/com/google/android/play/core/review/model/ReviewErrorCode.html)
    

### Developer Notes:

**Library Dependencies:**
>    `com.google.android.play:review:2.0.1`
  