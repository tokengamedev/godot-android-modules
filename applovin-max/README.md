# Applovin Max

AppLovin Max is an android plugin for Godot Game engine.

This plugin enables the monetization of your games through display of ads in various formats from ad networks through "Max" Mediator.
Mediator does not show ads by itself, it needs ad networks to serve ads. Mediators are better than serving ads through ad networks SDK directly as mediators can serve ads from various networks through bidding or waterfall methods, which will provide better fill rate and eCPM for your game ads.

By default AppLovin network is added as part of this plugin. To add other plugins check the ad network plugins below:

To understand more about the Applovin Max. Go through the [Website](https://www.applovin.com/max/).


## Pre-Requisites and Plugins:

Installing the plugin by itself will not work, Following steps needs to be done also: 

- Account has to be created in applovin max [APPLOVIN DashBoard](https://dash.applovin.com/login)
- Account has to be created in corresponding ad networks (if used)
- Integrate the mediator with the ad networks.
- Create the Ad units in AppLovin 

The various Plugins available for integration as part of GAML are as follows:

| Ad Network |  Plugin | Version | SDK Version | Description |
|------------|---------|---------|-------------|-------------|


Note: Ideally all the ad networks integrated should be added into one plugins 

## Basic Usage:

Add the SDK key in the Android manifest
```xml
    <application>
        ...
        <!-- Find the SDK Key under (Account -> Keys in Applovin Dashboard) -->
        <meta-data android:name="applovin.sdk.key"
                   android:value="XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"/>
        ...
    </application>
```

To access the plugin you have to use the standard code for Godot plugin in the code

```gdscript
# Check if the plugin is available
if Engine.has_singleton("AppLovinMax"):

    # Get a reference to the singleton
    var _ads_plugin = Engine.get_singleton("AppLovinMax")
    
    # initialize the SDK
    # on initialized app will be displayed in AppLovinMax dashboard automatically.
    _ads_plugin.initialize(mediation_options)    
```

 
Example of usage of various features can be referred in the file *[usage/ads_manager.md]()*

## Features:

### 1. Display Ads:

#### a. Rewarded Ads
Show Full screen Video ads which provides rewards to the end user on watching. 
  
**Usage:**

```gdscript
# ad_type = 4 for rewarded ad

# Call to the method for Setup of Ad
_ads_plugin.setupAd(ad_type, ad_unit_id)

# Call to the method for loading of ad
_ads_plugin.loadAd(ad_type)

# Call to the method for displaying of ad
_ads_plugin.showAd(ad_type)
```

  
#### b. Interstitial Ads:
Show a quick full screen ads as image or video.

**Usage:**

```gdscript
# ad_type = 4 for rewarded ad

# Call to the method for Setup of Ad
_ads_plugin.setupAd(ad_type, ad_unit_id)

# Call to the method for loading of ad
_ads_plugin.loadAd(ad_type)

# Call to the method for displaying of ad
_ads_plugin.showAd(ad_type)
```

## APIs

### Enums

##### AdType
Maps the Type of Ad
- BANNER (1)
- INTERSTITIAL (2)
- NATIVE (3)
- REWARDED (4)
- MREC (5)


### Objects

##### MediationOptions
Holds the options for configuring, the mediation or display of Ad.

- **autoload_ads [Boolean]** (Default: true)
  Controls to autoload the ads when the ads are created and when the ads have finished showing. Applicable for Interstitial and Rewarded Ads only.
- **retry_on_load_failure** [Boolean] (Default: true)
  Retries if the ad load failed but with exponential delay
- **maxRetries**[Int] (Default: 6)
  Number of retries that will happen before abandoning retries.

##### AdDetails
Holds the details about the Ad

- **unit_id [String]** The unit id based on which the ad has been created.
- **type [String]** The format of the ad
- **network_name [String]** The network from which the ad was loaded.
- **network_placement [String]** The placement info from the network
- **placement [String]** The placement id from the mediator
  

##### AdReward
Holds the details about the Ad
- **label [Int]** The label of the reward configured with the mediator.
- **amount [String]** The amount of reward configured with mediator.


##### AdError
Holds the details about the Ad
- **code [Int]** The error code of the error.
- **message [String]** The error message of the error.
- **network_code [Int]** The mediated network's error code.
- **network_message [String]** The mediated network's error message.


### Methods

- ##### initialize(mediationOptions)
  Initializes the underlying Ad mediators SDK and sets up for ad creation, load, and display.
    
  **Parameters**
  - **mediationOptions [[MediationOptions](#mediationoptions)]** the options passed to initialize the sdk
  

- ##### setPersonalizedContent(allow)
  Sets the personalized Ads flag. This is mostly applicable for GDPR applicable regions.

  **Parameters**
  - **allow [Boolean]** true if allow personalized Ads

- ##### setThirdPartyDataUsage(allow)
  Sets the flag to allow data usage by third party. This is mostly applicable for CCPA applicable regions.

  **Parameters**
  - **allow [Boolean]** true if allow data usage by third party.

- ##### setAgeRestricted(value)
  Sets the flag true to exclude adult content, else allow.
    
  **Parameters**
  - **allow [Boolean]** true if to exclude adult content
  

- ##### setupAd(adType, adUnitId)
  Creates a ad of the given type and the unique Id. This adUnit Id should be from AppLovinMax and not from ad networks.
    
  **Parameters**
  - **adType [[AdType](#adtype)]** the type ad to be created.
  - **adUnitId [String]** the unique id of the ad unit created in AppLovin dashboard


- ##### loadAd(adType)
  Loads an ad of the given type. This ad of adType should have been created before load is asked using `setupAd` function.
  
    **Parameters**
  - **adType [[AdType](#adtype)]** the type ad to be loaded.
  
- ##### showAd(adType)
  Shows an ad of the given type. This ad of adType should have been Loaded before show is asked.
    
  **Parameters**
  - **adType [[AdType](#adtype)]** the type ad to be shown.

- ##### showAdWithPlacement(adType, placement)
  Variation of `showAd`, which allows user to further categorize based on the provided values for placement. 
  Note: If placement is provided, when ad details are fetched during signal callbacks, placement will be filled.
    
  **Parameters**
  - **adType [[AdType](#adtype)]** the type ad to be shown.
  - **placement [String]** The placement id to feed to the mediator



### Signals

- ##### initialized()
  Raised when the SDK is initialized after the call of the initialize method.

  
- ##### interstitial_ad_loaded(adDetails)
  Raised when the interstitial ad is loaded with ads.

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad loaded.
  

- ##### interstitial_ad_load_failed(adUnitId, adError)
  Raised when the interstitial ad load fails.

  **Parameters**
  - **adUnitId [String]** the unique id of the ad unit created in AppLovin dashboard if valid else blank
  - **adError [[AdError](#aderror)]** Error code and message for the load failure


- ##### interstitial_ad_displayed(adDetails)
  Raised when the interstitial ad is displayed.

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad displayed.
  

- ##### interstitial_ad_display_failed(adUnitId, adError)
  Raised when the interstitial ad display fails.

  **Parameters**
  - **adUnitId [String]** the unique id of the ad unit created in AppLovin dashboard if valid else blank
  - **adError [[AdError](#aderror)]** Error code and message for the display failure

- ##### interstitial_ad_hidden(adDetails)
  Raised when the interstitial ad is closed or hidden

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad closed/hidden.
  

- ##### interstitial_ad_clicked(adDetails)
  Raised when the interstitial ad is clicked by the user.

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad clicked by the user.


- ##### rewarded_ad_loaded(adDetails)
  Raised when the rewarded ad is loaded with ads.

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad loaded.
  

- ##### rewarded_ad_load_failed(adUnitId, adError)
  Raised when the rewarded ad load fails.

  **Parameters**
  - **adUnitId [String]** the unique id of the ad unit created in AppLovin dashboard if valid else blank
  - **adError [[AdError](#aderror)]** Error code and message for the load failure


- ##### rewarded_ad_displayed(adDetails)
  Raised when the rewarded ad is displayed.

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad displayed.
  

- ##### rewarded_ad_display_failed(adUnitId, adError)
  Raised when the rewarded ad display fails.

  **Parameters**
  - **adUnitId [String]** the unique id of the ad unit created in AppLovin dashboard if valid else blank
  - **adError [[AdError](#aderror)]** Error code and message for the display failure


- ##### rewarded_ad_hidden(adDetails)
  Raised when the rewarded ad is closed or hidden

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad closed/hidden.
  

- ##### rewarded_ad_clicked(adDetails)
  Raised when the rewarded ad is clicked by the user.

  **Parameters**
  - **adDetails [[AdDetail](#addetails)]** Details of the Ad clicked by the user.




## Developer Notes:

|             | Minimum  | Maximum |
|-------------|----------|---------|
| Android SDK | 23       | 32      |
| Java/JDK    | 11       |         |
| Kotlin      | 1.10.1   |         | 

    
  