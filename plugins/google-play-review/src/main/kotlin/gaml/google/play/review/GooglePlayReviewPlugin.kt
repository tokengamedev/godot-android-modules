package gaml.google.play.review

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

/**
 * Android plugin to incorporate review flow strategies for games built with Godot game engine
 * for android devices
 */
class GooglePlayReviewPlugin(godot: Godot): GodotPlugin(godot) {

    companion object{
        const val SIGNAL_FLOW_COMPLETED = "flow_completed"
        const val SIGNAL_FLOW_LAUNCH_ERROR = "flow_launch_error"
    }

    private val currentActivity: Activity = activity ?: throw IllegalStateException()
    /**
     * Gets the name of the plugin to be used in Godot.
     */
    override fun getPluginName() = "GooglePlayReview"

    /**
     * Registers all the signals which the game may need to listen to
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo(SIGNAL_FLOW_COMPLETED ),
            SignalInfo(SIGNAL_FLOW_LAUNCH_ERROR ,Int::class.java)
        )
    }

    /**
     * Starts the review flow. In case the In-app review dialog could not be opened, it will
     * redirect to the play store for review
     */
    @UsedByGodot
    fun launchInAppReview() {

        val manager = ReviewManagerFactory.create(currentActivity)

        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->

            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result!!
                val flow = manager.launchReviewFlow(currentActivity, reviewInfo)
                flow.addOnCompleteListener {
                    // REVIEW Flow is completed here
                    emitSignal(SIGNAL_FLOW_COMPLETED)
                }

            } else {
                @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode

                // emit then signal that something went wrong
                emitSignal(SIGNAL_FLOW_LAUNCH_ERROR, reviewErrorCode)

                // As a fail over launch on the play store/website
                launchReviewInStore()
            }
        }
    }

    /**
     * Launches the Play Store app and opens the app in play store for review
     */
    @UsedByGodot
    fun launchReviewInStore() {
        activity?.let{ currentActivity ->
            try {
                val rateIntent = getIntentForUrl("market://details")
                currentActivity.startActivity(rateIntent)

            } catch (e: ActivityNotFoundException) {
                val rateIntent = getIntentForUrl("https://play.google.com/store/apps/details")
                currentActivity.startActivity(rateIntent)
            }
        }
    }

    /**
     * Gets the intent based on the url
     * @param url the url for which the intent has to created
     *
     * @return returns an Intent to start Activity
     */
    private fun getIntentForUrl(url: String): Intent {

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("$url?id=${activity?.packageName}")
        )

        // These flags are required to handle the back button. Back button should bring back to
        // the requested app/game
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        return intent
    }
}