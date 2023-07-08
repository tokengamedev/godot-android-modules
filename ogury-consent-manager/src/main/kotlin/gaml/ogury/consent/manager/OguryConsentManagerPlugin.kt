package gaml.ogury.consent.manager

import android.app.Activity
import com.ogury.cm.OguryChoiceManager
import com.ogury.cm.OguryChoiceManager.Answer
import com.ogury.cm.OguryConsentListener
import com.ogury.core.OguryError
import com.ogury.sdk.Ogury
import com.ogury.sdk.OguryConfiguration
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


/**
 * Android plugin to incorporate Ogury choice manager to expose consent management
 * for Godot Apps in android devices
 */
class OguryConsentManagerPlugin(godot: Godot): GodotPlugin(godot) {

    companion object{
        const val SIGNAL_CONSENT_SYNC_COMPLETED :String = "consent_sync_completed"
        const val SIGNAL_CONSENT_SYNC_FAILED :String = "consent_sync_failed"
    }
    /**
     * Gets the name of the plugin to be used in Godot.
     */
    override fun getPluginName() = "OguryConsentManager"

    /**
     * This Activity
     */
    private val thisActivity: Activity = activity ?: throw IllegalStateException()


    /**
     * Implementation of the listener to be used for ask and edit callbacks
     */
    private val oguryConsentListener: OguryConsentListener = object : OguryConsentListener {
        override fun onComplete(answer: Answer) {
            emitSignal(SIGNAL_CONSENT_SYNC_COMPLETED, ConsentHelper.getAnswer(answer))
        }

        override fun onError(error: OguryError) {
            emitSignal(SIGNAL_CONSENT_SYNC_FAILED, ConsentHelper.getError(error))
        }

    }

    /**
     * Method to synchronize the content status with server. In case it is required to show consent
     * dialog for user to consent it will be shown.
     * It should be called at the start of the Application, so that synchronization happens for the
     * consent status, after which consent methods can be utilised.
     * Note: The dialog display also depends on configuration done in the ogury configuration
     * for the app asset in Ogury Registration)
     * @param assetKey the application asset key in Ogury system.
     * This is of format "OGY-XXXXXXXXXXXX"
     */
    @UsedByGodot
    fun ask(assetKey: String) {

        val oguryConfigurationBuilder = OguryConfiguration.Builder(
            thisActivity.applicationContext,
            assetKey
        )

        // Create an instance of Ogury SDK
        Ogury.start(oguryConfigurationBuilder.build())

        // Request for choice manager
        OguryChoiceManager.ask(thisActivity, oguryConsentListener)

    }

    /**
     * This method is called to manually edit the consent status. This is the case when the
     * user tries to change the consent status. Application should check if [isEditable] before
     * calling the method or else there is a chance of error happening if the Consent status
     * is not editable[e.g., User outside GDPR and CCPA applicable region]
     */
    @UsedByGodot
    fun edit() {
        OguryChoiceManager.edit(thisActivity, oguryConsentListener)
    }

    /**
     * Returns true if you can edit the consent.
     * WARNING: This should be called after [ask] otherwise the result may not be correct
     */
    @UsedByGodot
    fun isEditable(): Boolean = OguryChoiceManager.isEditAvailable()

    /**
     * Returns true if the Gdpr applies for the User, else false
     * WARNING: This should be called after [ask] otherwise the result may not be correct
     */
    @UsedByGodot
    fun gdprApplies(): Boolean = OguryChoiceManager.gdprApplies()

    /**
     * Returns true if the CCPA applies for the User, else false
     * WARNING: This should be called after [ask] otherwise the result may not be correct
     */
    @UsedByGodot
    fun ccpaApplies(): Boolean = OguryChoiceManager.ccpaApplies()


    /**
     * Registers all the signals which the game may need to listen to
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo("consent_sync_completed" ,Any::class.java),
            SignalInfo("consent_sync_failed" ,Any::class.java)
           )
    }
}