package gaml.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot
import java.util.*

/**
 * Android plugin to to show notifications in android devices for events
 */
class AppNotificationPlugin(godot: Godot): GodotPlugin(godot) {

    private val context: Context =
        activity?.applicationContext ?: throw IllegalStateException()

    private val notifyHelper: Lazy<NotificationHelper> = lazy { NotificationHelper(context) }


    private val customOptions = mutableMapOf<Int, NotificationOptions>()
    /**
     * Gets the name of the plugin to be used in Godot.Required by Godot
     * @return name of the plugin
     */
    override fun getPluginName() = "AppNotification"

    /**
     * Registers all the signals which the game may need to listen to
     * Empty as no signals
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf()
    }

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
    fun setupNotificationChannel(channelId: String,
                                 channelName: String,
                                 importance: Int,
                                 channelDescription: String){


        notifyHelper.value.createNotificationChannel(
            channelId,
            channelName,
            importance,
            channelDescription
        )
    }

    /**
     * Removes a notification channel. If the android version is lower than 26 or the channel does
     * not exist nothing will happen
     * @param channelId Id of the channel to be deleted
     */
    @UsedByGodot
    fun removeNotificationChannel(channelId: String){
        notifyHelper.value.removeNotificationChannel(channelId)
    }

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
        customOptions[notificationId] = NotificationOptions(options)
    }

    /**
     * Shows a Notification immediately
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param title the title of the notification
     * @param message the message of the notification
     */
    @UsedByGodot
    fun showNotification(channelId: String, notificationId: Int, title: String, message: String) {

        val notifyOption = getNotificationOptions(notificationId)

        notifyHelper.value.notify(channelId, notificationId, title, message, notifyOption )

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
    fun showNotificationAfter(channelId: String,
                              notificationId: Int,
                              title: String,
                              message: String,
                              delay: Int) {

        // Interval has to be greater than 0
        if (delay <= 0)
            showNotification(channelId, notificationId, title, message)

        val pendingIntent = getAlarmPendingIntent(channelId, notificationId, title, message)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, delay)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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
    fun setupRepeatingNotification(channelId: String,
                                   notificationId: Int,
                                   title: String,
                                   message: String,
                                   delay: Int,
                                   interval: Int) {

        // Interval and Delay has to be greater than 0
        if (interval <= 0 || delay <= 0) return

        val pendingIntent = getAlarmPendingIntent(channelId, notificationId, title, message)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, delay)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            (interval * 1000).toLong(), pendingIntent)
    }

    /**
     * Fetches all the active notifications that is not currently cancelled by user
     * @return Dictionary containing Array of trimmed Status Bar Notification Info
     */
    @UsedByGodot
    fun getActiveNotifications(): Dictionary {
        val activeNotifications = Dictionary()

        activeNotifications["notifications"] =
            notifyHelper.value.getAllActiveNotifications().toTypedArray<Any>()

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
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sender: PendingIntent = getAlarmPendingIntent("", notificationId, "", "")
        am.cancel(sender)
    }

    /**
     * Gets the pending intent required to invoke the notification when alarm goes off
     * @param channelId the channel id on which notification has to be sent
     * @param notificationId unique notification id
     * @param title the title of the notification
     * @param message the message of the notification
     *
     * @return the PendingIntent to be used for notification in the receiver class
     */
    private fun getAlarmPendingIntent(channelId: String,
                                      notificationId: Int,
                                      title: String,
                                      message: String): PendingIntent {

        val notificationOption = getNotificationOptions(notificationId)

        val i = Intent(context, NotificationReceiver::class.java)
        i.putExtra("notification_id", notificationId)
        i.putExtra("message", message)
        i.putExtra("title", title)
        i.putExtra("channel_id", channelId)
        i.putExtra(NotificationOptions.TAG_SHOW_WHEN, notificationOption.showWhen)
        i.putExtra(NotificationOptions.TAG_SMALL_ICON, notificationOption.smallIconId)
        i.putExtra(NotificationOptions.TAG_LARGE_ICON, notificationOption.largeIconId)
        i.putExtra(NotificationOptions.TAG_COLOR, notificationOption.colorId)
        i.putExtra(NotificationOptions.TAG_EXPANDABLE, notificationOption.expandable)
        i.putExtra(NotificationOptions.TAG_CATEGORY, notificationOption.category)
        i.putExtra(NotificationOptions.TAG_SUB_TEXT, notificationOption.subText)


        return PendingIntent.getBroadcast(activity, notificationId, i,
            notifyHelper.value.flagPendingIntent(false))
    }

    /**
     * Gets the channel notification options from the list.
     * If no notification options found for the channel then Default notification channel
     * options will be used
     * @param notificationId the channel id for which options is required
     *
     * @return Notification options for sending the notification
     */
    private fun getNotificationOptions(notificationId: Int): NotificationOptions{
        return if (customOptions.isNotEmpty() && customOptions.containsKey(notificationId))
            customOptions.getValue(notificationId)
        else
            NotificationHelper.DEFAULT_NOTIFICATION_OPTIONS
    }

}