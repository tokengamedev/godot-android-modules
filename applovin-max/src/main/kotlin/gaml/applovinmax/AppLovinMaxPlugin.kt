package gaml.applovinmax

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.applovin.mediation.*
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.ads.MaxRewardedAd
import com.applovin.mediation.ads.MaxRewardedInterstitialAd
import com.applovin.sdk.AppLovinPrivacySettings
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkSettings
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import java.security.InvalidParameterException
import kotlin.math.pow
/**
 * Android plugin to wrap Applovin SDK so that Ads can be shown in games built with Godot
 */
class AppLovinMaxPlugin(godot: Godot): GodotPlugin(godot) {

    companion object{
        const val TAG = "AppLovinMaxPlugin"

        // Ad Types:
        // All Ads as power of 2 for being flags
        const val BANNER_AD: Int = 1
        const val INTERSTITIAL_AD: Int = 2
        const val NATIVE_AD:Int = 3
        const val REWARDED_AD: Int = 4
        const val MREC_AD: Int = 5


        // Signal Names
        const val SDK_INITIALIZED = "initialized"

        const val INTERSTITIAL_AD_LOADED = "interstitial_ad_loaded"
        const val INTERSTITIAL_AD_DISPLAYED = "interstitial_ad_displayed"
        const val INTERSTITIAL_AD_HIDDEN = "interstitial_ad_hidden"
        const val INTERSTITIAL_AD_CLICKED = "interstitial_ad_clicked"
        const val INTERSTITIAL_AD_LOAD_FAILED = "interstitial_ad_load_failed"
        const val INTERSTITIAL_AD_DISPLAY_FAILED = "interstitial_ad_display_failed"

        const val REWARDED_AD_LOADED = "rewarded_ad_loaded"
        const val REWARDED_AD_DISPLAYED = "rewarded_ad_displayed"
        const val REWARDED_AD_HIDDEN = "rewarded_ad_hidden"
        const val REWARDED_AD_CLICKED = "rewarded_ad_clicked"
        const val REWARDED_AD_LOAD_FAILED = "rewarded_ad_load_failed"
        const val REWARDED_AD_DISPLAY_FAILED = "rewarded_ad_display_failed"
        const val REWARDED_AD_USER_REWARDED = "rewarded_ad_user_rewarded"
    }

    private val currentActivity: Activity =
        activity ?: throw IllegalStateException()

    private val context: Context =
        activity?.applicationContext ?: throw IllegalStateException()


    private var options: MediationOptions = MediationOptions(Dictionary())
    private var isInitialized: Boolean = false


    private var interstitialAd: MaxInterstitialAd? = null
    private var retryAttemptsInterstitial = 0

    private var rewardedAd: MaxRewardedAd? = null
    private var retryAttemptsRewarded = 0

    /**
     * Gets the name of the plugin to be used in Godot.Required by Godot
     * @return name of the plugin
     */
    override fun getPluginName() = "AppLovinMax"

    /**
     * Registers all the signals which the game may need to listen to
     * Empty as no signals
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            // Signal when the SDK is initialized
            SignalInfo(SDK_INITIALIZED),

            /// INTERSTITIAL_ADS Signals
            SignalInfo(INTERSTITIAL_AD_LOADED, Any::class.java),
            SignalInfo(INTERSTITIAL_AD_DISPLAYED, Any::class.java),
            SignalInfo(INTERSTITIAL_AD_HIDDEN, Any::class.java),
            SignalInfo(INTERSTITIAL_AD_CLICKED, Any::class.java),
            SignalInfo(INTERSTITIAL_AD_LOAD_FAILED, String::class.java, Any::class.java),
            SignalInfo(INTERSTITIAL_AD_DISPLAY_FAILED, String::class.java, Any::class.java),

            //REWARDED_ADS Signals
            SignalInfo(REWARDED_AD_LOADED, Any::class.java),
            SignalInfo(REWARDED_AD_DISPLAYED, Any::class.java),
            SignalInfo(REWARDED_AD_HIDDEN, Any::class.java),
            SignalInfo(REWARDED_AD_CLICKED, Any::class.java),
            SignalInfo(REWARDED_AD_LOAD_FAILED, String::class.java, Any::class.java),
            SignalInfo(REWARDED_AD_DISPLAY_FAILED, String::class.java, Any::class.java),
            SignalInfo(REWARDED_AD_USER_REWARDED, Any::class.java, Any::class.java),
        )
    }

    /**
     * Checks if the Applovin is ready to request for ads
     */
    @UsedByGodot
    fun isReady() : Boolean = this.isInitialized

    /**
     * Helper function to add a log error message
     */
    private fun isInitialized() : Boolean {
        if (!isInitialized)
            Log.e(TAG, "AppLovin SDK not initialized.")
        return isInitialized
    }

    /**
     * Initialises the AddLovin Mediator
     * @param mediationOptions options to configure the Mediatior
     */
    @UsedByGodot
    fun initialize(mediationOptions: Dictionary){

        options = MediationOptions(mediationOptions)
        AppLovinSdk.getInstance(context).mediationProvider = "max"
        AppLovinSdk.getInstance(context).initializeSdk {

            Log.d(TAG, "Mediator initialized Successfully.")

            isInitialized = true

            emitSignal(SDK_INITIALIZED)
        }
    }

    /**
     * Sets the option for Ads to be displayed based on user interest.
     * @param allow true if user will see interest based ads else false.
     * Note: For users under GDPR region should set the values here
     */
    @UsedByGodot
    fun setPersonalizedContent(allow: Boolean){
        AppLovinPrivacySettings.setHasUserConsent(allow, context)
    }

    /**
     * Sets the option for data to be shared with third parties for interest based advertisements
     * and others.
     * @param allow true if data can be used by third parties for interest based ads else false.
     * Note: For users under CCPA region should set the value here
     */
    @UsedByGodot
    fun setThirdPartyDataUsage(allow: Boolean){
        AppLovinPrivacySettings.setDoNotSell(!allow, context)
    }

    /**
     * Sets if the user can see age restricted ads or not
     * @param value, false if user can see restricted ads or true if user has to see family friendly ads
     */
    @UsedByGodot
    fun setAgeRestricted(value: Boolean){
        AppLovinPrivacySettings.setIsAgeRestrictedUser(value, context)
    }

    /**
     * Create the Ads
     * @param adType: One of type [MediationOptions.*_AD]
     * @param adUnitId: the id based on which the ad is created
     */
    @UsedByGodot
    fun setupAd(adType: Int, adUnitId: String): Boolean {

        if (isInitialized()) {

            if (adUnitId.isEmpty()) {
                Log.e(TAG, "Valid adUnitId is required")
                return false
            }

            when (adType) {
                INTERSTITIAL_AD -> {
                    createInterstitialAd(adUnitId)
                    if (options.autoloadAds)
                        loadAd(adType)
                    return true
                }
                REWARDED_AD -> {
                    createRewardedAd(adUnitId)
                    if (options.autoloadAds)
                        loadAd(adType)
                    return true
                }
                else -> {
                    Log.e(TAG, "Invalid AdType for creating Ads: $adType")
                    return false
                }
            }
        }
        return false
    }

    /**
     * Loads the ad based on the type. if type is not available then ignores
     * @param adType type of AD to load
     */
    @UsedByGodot
    fun loadAd(adType: Int) {
        if (isInitialized()) {

            when (adType) {
                INTERSTITIAL_AD -> interstitialAd?.loadAd()
                REWARDED_AD -> rewardedAd?.loadAd()
                else -> Log.e(TAG, "Invalid AdType for loading Ads: $adType")

            }
        }
    }

    /**
     * Displays the ad based on the type. if type is not available then ignores
     * @param adType type of AD to show
     */
    @UsedByGodot
    fun showAd(adType: Int){

        if (isInitialized()) {

            when (adType) {
                INTERSTITIAL_AD -> interstitialAd?.showAd()
                REWARDED_AD -> rewardedAd?.showAd()
                else -> Log.e(TAG, "Invalid AdType for showing Ads: $adType")
            }
        }
    }
    /**
     * Displays the ad based on the type. if type is not available then ignores
     * @param adType type of AD to show
     * @param placement string to identify different category of ads of the type
     */
    @UsedByGodot
    fun showAdWithPlacement(adType: Int, placement: String){

        if (placement.isEmpty())
            throw IllegalArgumentException("Argument [placement] cannot be empty")

        if (isInitialized()) {

            when (adType) {
                INTERSTITIAL_AD -> interstitialAd?.showAd(placement)
                REWARDED_AD -> rewardedAd?.showAd(placement)
                else -> Log.e(TAG, "Invalid AdType for showing Ads: $adType")
            }
        }
    }

    /**
     * Creates the Interstitial Ad
     * @param adUnitId the id based on which the ad is created
     */
    private fun createInterstitialAd(adUnitId: String){

        Log.i(TAG, "Creating Interstitial AD")
        interstitialAd = MaxInterstitialAd(adUnitId, currentActivity)

        Log.i(TAG, "Created Interstitial AD :" + interstitialAd?.toString())
        interstitialAd!!.setListener( object: MaxAdListener {

            override fun onAdLoaded(ad: MaxAd?) {
                Log.i(TAG, "Interstitial AD loaded.")
                emitSignal(INTERSTITIAL_AD_LOADED, AdsHelper.getAdDetails(ad))
                retryAttemptsInterstitial = 0
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                Log.i(TAG, "Interstitial AD displayed.")
                emitSignal(INTERSTITIAL_AD_DISPLAYED, AdsHelper.getAdDetails(ad))
            }

            override fun onAdHidden(ad: MaxAd?) {
                Log.i(TAG, "Interstitial AD hidden.")
                emitSignal(INTERSTITIAL_AD_HIDDEN, AdsHelper.getAdDetails(ad))
                if (options.autoloadAds){
                    loadAd(INTERSTITIAL_AD)
                }

            }

            override fun onAdClicked(ad: MaxAd?) {
                Log.i(TAG, "Interstitial AD clicked.")
                emitSignal(INTERSTITIAL_AD_CLICKED, AdsHelper.getAdDetails(ad))
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                Log.i(TAG, "Interstitial AD load failed. [$error]")

                if (options.retryOnLoadFailure && retryAttemptsInterstitial < options.maxRetries){

                    retryAttemptsInterstitial += 1
                    val delayMillis: Long =  2.0.pow(retryAttemptsInterstitial.toDouble()).times(1000).toLong()
                    Handler(Looper.getMainLooper()).postDelayed({
                        interstitialAd?.loadAd()
                    }, delayMillis)

                }else {
                    emitSignal(INTERSTITIAL_AD_LOAD_FAILED, adUnitId?: "", AdsHelper.getAdError(error) )
                }

            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                Log.i(TAG, "Interstitial AD display failed. [$error]")
                emitSignal(INTERSTITIAL_AD_DISPLAY_FAILED, ad?.adUnitId ?: "", AdsHelper.getAdError(error))
                loadAd(INTERSTITIAL_AD)
            }
        })
        retryAttemptsInterstitial = 0
    }

    /**
     * Creates the Rewarded Ad
     * @param adUnitId the id based on which the ad is created
     */
    private fun createRewardedAd(adUnitId: String){
        rewardedAd= MaxRewardedAd.getInstance(adUnitId, currentActivity)
        rewardedAd!!.setListener(object: MaxRewardedAdListener{
            override fun onAdLoaded(ad: MaxAd?) {
                Log.i(TAG, "Rewarded AD loaded.")
                emitSignal(REWARDED_AD_LOADED,  AdsHelper.getAdDetails(ad))
                retryAttemptsRewarded = 0
            }

            override fun onAdDisplayed(ad: MaxAd?) {
                Log.i(TAG, "Rewarded AD Displayed.")
                emitSignal(REWARDED_AD_DISPLAYED,  AdsHelper.getAdDetails(ad))
            }

            override fun onAdHidden(ad: MaxAd?) {
                Log.i(TAG, "Rewarded AD Hidden.")
                emitSignal(REWARDED_AD_HIDDEN,  AdsHelper.getAdDetails(ad))
                if (options.autoloadAds){
                    loadAd(REWARDED_AD)
                }
            }

            override fun onAdClicked(ad: MaxAd?) {
                Log.i(TAG, "Rewarded AD Clicked.")
                emitSignal(REWARDED_AD_CLICKED,  AdsHelper.getAdDetails(ad))
            }

            override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                Log.i(TAG, "Rewarded AD Load failed. [$error]")

                if (options.retryOnLoadFailure && retryAttemptsRewarded < options.maxRetries){

                    retryAttemptsRewarded += 1
                    val delayMillis: Long =  2.0.pow(retryAttemptsRewarded.toDouble()).times(1000).toLong()

                    Handler(Looper.getMainLooper()).postDelayed({
                        rewardedAd?.loadAd()
                    }, delayMillis)

                }else {
                    emitSignal(REWARDED_AD_LOAD_FAILED, adUnitId?: "", AdsHelper.getAdError(error) )
                }
            }

            override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                Log.i(TAG, "Rewarded AD Display failed. [$error]")
                emitSignal(REWARDED_AD_DISPLAY_FAILED, ad?.adUnitId ?: "", AdsHelper.getAdError(error) )
                loadAd(REWARDED_AD)
            }

            override fun onUserRewarded(ad: MaxAd?, reward: MaxReward?) {
                Log.i(TAG, "Rewarded AD - User Rewarded. $reward")
                emitSignal(REWARDED_AD_USER_REWARDED, AdsHelper.getAdDetails(ad), AdsHelper.getAdReward(reward) )
            }

            @Deprecated("Deprecated in Java")
            override fun onRewardedVideoStarted(ad: MaxAd?) {}

            @Deprecated("Deprecated in Java")
            override fun onRewardedVideoCompleted(ad: MaxAd?) {}
        })
        retryAttemptsRewarded = 0
    }
}