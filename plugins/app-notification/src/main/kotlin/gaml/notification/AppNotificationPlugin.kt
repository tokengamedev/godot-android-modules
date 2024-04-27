package gaml.notification

import android.app.Activity
import android.app.NotificationManager
import android.os.Build
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


/**
 * Android plugin to show notifications in android devices for events
 */
class AppNotificationPlugin(godot: Godot): GodotPlugin(godot) {

    companion object{
        val DEFAULT_NOTIFICATION_OPTIONS = NotificationOptions(Dictionary())
        private val RC_NOTIFICATION = 999
    }

    private val currentActivity: Activity = activity ?: throw IllegalStateException()

    private val notifyHelper: Lazy<NotificationHelper> = lazy { NotificationHelper(currentActivity) }


    // Holds list of custom notification options, if the custom options are not here then default
    // option will be applied
    private val notificationOptions = mutableMapOf<Int, NotificationOptions>()

    // region Godot specific methods

    /**
     * Gets the name of the plugin to be used in Godot. Required by Godot.
     * @return Name of the plugin
     */
    override fun getPluginName() = "AppNotification"

    /**
     * Registers all the signals which the game may need to listen to. Required by Godot.
     *  @return list of signals that will be triggered by the plugin
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> = mutableSetOf()

    // endregion

    // region Channel management methods

    /**
     * Creates a notification channel. It will work with android api version 26 and higher.
     * If this is called in different version of OS, then INCOMPATIBLE_OS_VERSION (-1) is returned.
     * @param channelId Unique Id of the channel
     * @param channelName Display name of the channel
     * @param importance the importance of the messages through this channel.
     * See [NotificationManager].IMPORTANCE_* for possible values
     * @param channelDescription a descriptive text about the channel
     * @return [Int] NO_ERROR(0), INCOMPATIBLE_OS_VERSION(-1), CHANNEL_ALREADY_EXISTS(1)
     */
    @UsedByGodot
    fun setupNotificationChannel(channelId: String, channelName: String, importance: Int,
                                 channelDescription: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyHelper.value.createNotificationChannel(
                channelId,
                channelName,
                importance,
                channelDescription
            )
        }
    }

    /**
     * Removes a notification channel. If the android version is lower than 26 or the channel does
     * not exist nothing will happen
     * @param channelId Id of the channel to be deleted
     */
    @UsedByGodot
    fun removeNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyHelper.value.removeNotificationChannel(channelId)
        }
    }

    /**
     * Gets the notification channel for the given channel Id.
     * @param channelId Id of the channel to be fetched
     */
    @UsedByGodot
    fun getNotificationChannel(channelId: String): Dictionary {
        val channel = notifyHelper.value.getNotificationChannel(channelId)
        return if (channel != null)
            NotificationUtil.getNotificationChannelDictionary(channel)
        else
            Dictionary()
    }

    /**
     * Sets up the customization for the notification channel for the given channel Id.
     * @param channelId Id of the channel to be customized
     */
    @UsedByGodot
    fun setupChannelOptions(channelId: String, options: Dictionary) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyHelper.value.setupChannelOptions(channelId, ChannelOptions(options))
        }
    }

    // endregion

    // region Notification option methods

    /**
     * Setup the Notification customization to customize icons, color and more
     * Any customization not provided will take up default values.
     * @param notificationId the notification id on which the customization is applied
     * @param options the dictionary of various options for customisation
     *
     */
    @UsedByGodot
    fun setNotificationCustomOptions( notificationId: Int,
                                      options: Dictionary){
        notificationOptions[notificationId] = NotificationOptions(options)
    }

    /**
     * Gets the channel notification options from the list.
     * If no custom notification options found for the channel then Default notification channel
     * options will be returned
     * @param notificationId the channel id for which options is required
     *
     * @return Notification options for sending the notification
     */
    @UsedByGodot
    fun getNotificationOptions(notificationId: Int): Dictionary{
        val option = getOptions(notificationId)
        return NotificationUtil.getNotificationOptionsDictionary(option)
    }

    /**
     * Gets the channel notification options from the list or else returns default notification options
     */
    private fun getOptions(notificationId: Int): NotificationOptions {
        return if (notificationOptions.isNotEmpty() && notificationOptions.containsKey(notificationId))
            notificationOptions.getValue(notificationId)
        else
            DEFAULT_NOTIFICATION_OPTIONS
    }
    // endregion

    // region Notification Methods

    /**
     * Show a Notification immediately
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param title the title of the notification
     * @param message the message of the notification
     */
    @UsedByGodot
    fun showNotification(channelId: String, notificationId: Int, title: String, message: String) {

        val notifyOption = getOptions(notificationId)

        notifyHelper.value.notify(channelId, notificationId, title, message, notifyOption)
    }

    /**
     * Shows or setup notification with tags.
     * For Immediate notification send title and message only
     * For Delayed notification,  delay should be > 0
     * For Recurring Notification, delay > 0 and Interval > 0
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param tag a unique string for further classification of notification
     * @param title the title of the notification
     * @param message the message of the notification
     * @param delay the amount of time(in Seconds) after which the first notification will be posted
     * @param interval the duration of time (in Seconds) after which the next notification will be posted
     *
     */
    @UsedByGodot
    fun showTaggedNotification(channelId: String, notificationId: Int, tag: String,
                               title: String, message: String, delay: Int, interval: Int){
        val notifyOptions = getOptions(notificationId)
        notifyOptions.tag = tag

        if (delay > 0)
            if (interval > 0)
                setupRepeatingNotification(channelId, notificationId, title, message, delay, interval)
            else
                showNotificationAfter(channelId, notificationId, title, message, delay)
        else
            showNotification(channelId, notificationId, title, message)
    }

    /**
     * Shows a Notification after a certain time once
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param title the title of the notification
     * @param message the message of the notification
     * @param delay the amount of time(in Seconds) after which the Notification will be sent.
     * It is an approximate value depending upon the device state
     */
    @UsedByGodot
    fun showNotificationAfter(channelId: String, notificationId: Int, title: String, message: String,
                              delay: Int) {
        // Interval has to be greater than 0
        if (delay <= 0)
            showNotification(channelId, notificationId, title, message)
        else {
            val notifyOptions = getOptions(notificationId)

            notifyHelper.value.notifyAfter(
                channelId,
                notificationId,
                title,
                message,
                delay,
                notifyOptions
            )
        }
    }

    /**
     * Sets up a repeating notification schedule
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param title the title of the notification
     * @param message the message of the notification
     * @param delay the amount of time(in Seconds) after which the first notification will be posted
     * @param interval the duration of time (in Seconds) after which the notification will be posted
     * again. It is an approximate value depending upon the device state
     */
    @UsedByGodot
    fun setupRepeatingNotification(channelId: String, notificationId: Int, title: String, message: String,
                                   delay: Int, interval: Int) {

        // Interval and Delay has to be greater than 0
        if (interval <= 0 || delay <= 0) return

        val notifyOptions = getOptions(notificationId)
        notifyHelper.value.notifyRepeating( channelId,
                                            notificationId,
                                            title,
                                            message,
                                            delay,
                                            interval,
                                            notifyOptions
                                            )
    }

    // endregion

    // region Helper Methods
    /**
     * Checks if the notifications is not blocked by user for the app and channel
     * @return true if not blocked else blocked
     */
    @UsedByGodot
    fun canPostNotifications(channelId: String): Boolean{
        val helper = notifyHelper.value
        return helper.areNotificationsEnabled() &&
                !helper.areNotificationsPaused() &&
                !helper.isChannelBlocked(channelId)
    }

    /**
     * Fetches all the active notifications that is not currently cancelled by user
     * @return Dictionary containing Array of trimmed Status Bar Notification Info
     */
    @UsedByGodot
    fun getActiveNotifications(): Dictionary {

        val activeNotifications = Dictionary()
        val notifications = mutableListOf<Dictionary>()


        for (notification in notifyHelper.value.getAllActiveNotifications()){
            notifications.add(NotificationUtil.getNotificationDictionary(notification))
        }

        activeNotifications["notifications"] = notifications.toTypedArray<Any>()

        return activeNotifications
    }

    /**
     * Cancels the active notification for the notification Id and tag
     * @param notificationId The id of the notification being cleared
     * @param tag the tag attached to the notification, to filter further.
     * It can be empty for cancelling all notifications for notificationId
     */
    @UsedByGodot
    fun cancelActiveNotification(notificationId: Int, tag: String = "") =
        notifyHelper.value.cancelActiveNotification(notificationId, tag)

    /**
     * Cancels any pending notification. It may be one time or it may be repeating.
     * @param notificationId the id of the notification to be cancelled
     */
    @UsedByGodot
    fun cancelNotification(notificationId: Int) {

        val notifyOptions = getOptions(notificationId)
        notifyHelper.value.cancelPendingNotification(notificationId, notifyOptions)
    }
    // endregion
}