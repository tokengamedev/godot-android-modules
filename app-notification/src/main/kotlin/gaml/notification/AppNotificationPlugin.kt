package gaml.notification

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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

    private val channelOptions = mutableMapOf(
        NotificationHelper.DEFAULT_CHANNEL_ID to
            NotificationOptions(null, null, null, null)
    )
    /**
     * Gets the name of the plugin to be used in Godot.Required by Godot
     * @return name of the plugin
     */
    override fun getPluginName() = "AppNotification"


    /**
     * Returns the Id of the DefaultChannel
     * @return Id of the default channel
     */
    @UsedByGodot
    fun getDefaultChannelId(): String = NotificationHelper.DEFAULT_CHANNEL_ID

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
                                 channelDescription: String): Int{

        return notifyHelper.value.createNotificationChannel(
            channelId,
            channelName,
            importance,
            channelDescription
        )
    }

    /**
     * Setup the channel customization to customize icons and notification color.
     * Any customization not provided will take up default values.
     *
     * @param channelId the channel id on which the customization is applied
     * @param showWhen will display showWhen or not
     * @param smallIconId displays the small notification icon for the channel
     * @param largeIconId displays the large icon for the channel
     * @param colorId colorizes the small notification Icon
     *
     * @return NO_ERROR(0), INCOMPATIBLE_OS_VERSION(-1) and NO_CHANNEL_AVAILABLE(2)
     */
    @UsedByGodot
    fun setChannelCustomOptions(channelId: String,
                                showWhen: Boolean ,
                                smallIconId:String,
                                largeIconId: String,
                                colorId: String ): Int{

        // Check if it is valid OS to work on
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return NotificationHelper.INCOMPATIBLE_OS_VERSION

        // Check if the channel is
        return if (notifyHelper.value.getNotificationChannel(channelId) == null) {
            NotificationHelper.NO_CHANNEL_AVAILABLE
        }
        else {
            // Copy the default options and override whatever is provided
            channelOptions[channelId] =
                NotificationOptions(showWhen, smallIconId, largeIconId, colorId)

            NotificationHelper.NO_ERROR
        }
    }

    /**
     * Shows a Notification immediately
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param title the title of the notification
     * @param message the message of the notification
     */
    @UsedByGodot
    fun showNotification(channelId: String, notificationId: Int, title: String, message: String) {

        val notifyOption = getNotificationOptions(channelId)

        notifyHelper.value.notify(channelId, notificationId, title, message, notifyOption )

    }

    /**
     * Shows a Notification after a certain time once
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param interval the amount of time(in Seconds) after which the Notification will be sent.
     * It is an approximate value depending upon the device state
     * @param title the title of the notification
     * @param message the message of the notification
     */
    @UsedByGodot
    fun showNotificationAfter(channelId: String, notificationId: Int, interval: Int, title: String, message: String) {

        // Interval has to be greater than 0
        if (interval <= 0) return

        val pendingIntent = getAlarmPendingIntent(channelId, notificationId, title, message)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, interval)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }


    /**
     * Sets up a repeating notification schedule
     *
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param interval the amount of time(in Seconds) after which the Notification repetition
     * will start.
     * @param repeat_duration the duration after which the notification will happen again.
     * It is an approximate value depending upon the device state
     * @param title the title of the notification
     * @param message the message of the notification
     */
    @UsedByGodot
    fun setupRepeatingNotification(channelId: String,
                                   notificationId: Int,
                                   interval: Int,
                                   repeat_duration: Int,
                                   title: String,
                                   message: String) {

        // Interval has to be greater than 0
        if (repeat_duration <= 0) return

        val pendingIntent = getAlarmPendingIntent(channelId, notificationId, title, message)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, interval)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            (repeat_duration * 1000).toLong(), pendingIntent)
    }

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
     * Registers all the signals which the game may need to listen to
     * Empty as no signals
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf()
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

        val notificationOption = getNotificationOptions(channelId)

        val i = Intent(context, NotificationReceiver::class.java)
        i.putExtra("notification_id", notificationId)
        i.putExtra("message", message)
        i.putExtra("title", title)
        i.putExtra("channel_id", channelId)
        i.putExtra("show_when", notificationOption.showWhen)
        i.putExtra("small_icon", notificationOption.smallIconId)
        i.putExtra("large_icon", notificationOption.largeIconId)
        i.putExtra("color", notificationOption.colorId)


        return PendingIntent.getBroadcast(activity, notificationId, i,
            notifyHelper.value.flagPendingIntent(false))
    }


    /**
     * Gets the channel notification options from the list.
     * If no notification options found for the channel then Default notification channel
     * options will be used
     * @param channelId the channel id for which options is required
     *
     * @return Notification options for sending the notification
     */
    private fun getNotificationOptions(channelId: String): NotificationOptions{
        return if (channelId.isNotEmpty() && channelOptions.containsKey(channelId))
            channelOptions.getValue(channelId)
        else
            channelOptions.getValue(NotificationHelper.DEFAULT_CHANNEL_ID)
    }

}