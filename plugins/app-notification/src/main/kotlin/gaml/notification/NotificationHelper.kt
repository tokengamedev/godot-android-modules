package gaml.notification

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.Calendar

/**
 * Helper class to send notification
 */
class NotificationHelper (private val context: Context){

    companion object{
        private const val DEFAULT_CHANNEL_ID = "default"
        private const val DEFAULT_CHANNEL_NAME = "Default"
        private const val DEFAULT_CHANNEL_DESC = "Default channel"
        private const val DEFAULT_NOTIFICATION_ICON_COLOR = Color.MAGENTA

        private const val APP_CLASS_NAME = "com.godot.game.GodotApp"

    }

    private val notificationManager: NotificationManager =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) ?: throw IllegalStateException()) as NotificationManager

    /**
     * Creates a Notification channel (if not existing with the same id). This can be called
     * during start of the application/game, without issues.
     * @param channelId unique id of the channel
     * @param channelName display name of the channel
     * @param importance the importance of the messages through this channel.
     * See [NotificationManager].IMPORTANCE_* for possible values
     * @param channelDescription a descriptive text about the channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(channelId: String,
                                  channelName: String,
                                  importance: Int,
                                  channelDescription: String) {

        // Cannot do any channel creation below android api version 26

        val channel = notificationManager.getNotificationChannel(channelId)
        if (channel != null) {
            if (channel.name != channelName ||
                channel.importance != importance ||
                channel.description != channelDescription) {

                notificationManager.deleteNotificationChannel(channelId)
            }else return
        }
        notificationManager.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                importance
            ).apply { description = channelDescription }
        )
    }

    /**
     * Removes the notification channel if exists.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeNotificationChannel(channelId: String){

        val channel = getNotificationChannel(channelId)
        if (channel != null){
            notificationManager.deleteNotificationChannel(channel.id)
        }
    }

    /**
    * Removes the notification channel if exists. It will not delete default channel
    */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setupChannelOptions(channelId: String, options: ChannelOptions){

        val channel = getNotificationChannel(channelId)
        if (channel != null){
            channel.setShowBadge(options.showBadge)
            channel.lockscreenVisibility = options.lockScreenVisibility
        }
    }

    /**
     * gets a notification channel based on id and Android version
     * @return NotificationChannel if able to find else null
     */
    fun getNotificationChannel(channelId: String): NotificationChannel?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.getNotificationChannel(channelId)
        }else{
            null
        }
    }

    /**
     * Fetch the channel id to send notification. It validates the channel id passed by the user.
     * @return String channel_id if channel is supported else empty("")
     */
    private fun getChannelIdForNotification(channelId: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = getNotificationChannel(channelId)
            if (channel != null) {
                return channelId
            }
            else {

                // The creation logic will handle if there is a channel already
                createNotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    DEFAULT_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT,
                    DEFAULT_CHANNEL_DESC
                )
                return DEFAULT_CHANNEL_ID
            }
        }
        else ""
    }

    fun notifyAfter(channelId: String,
                    notificationId: Int,
                    title: String,
                    message: String,
                    delay: Int,
                    notifyOptions: NotificationOptions = NotificationOptions()){
        val pendingIntent = getAlarmPendingIntent(channelId, notificationId, title, message, notifyOptions)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, delay)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

    fun notifyRepeating(channelId: String,
                        notificationId: Int,
                        title: String,
                        message: String,
                        delay: Int,
                        interval: Int,
                        notifyOptions: NotificationOptions = NotificationOptions()){

        val pendingIntent = getAlarmPendingIntent(channelId, notificationId, title, message, notifyOptions)

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.SECOND, delay)

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        am.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            (interval * 1000).toLong(), pendingIntent)

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
                                      message: String,
                                      notificationOptions: NotificationOptions): PendingIntent {

        val i = Intent(context, NotificationReceiver::class.java)

        i.putExtra("notification_id", notificationId)
        i.putExtra("message", message)
        i.putExtra("title", title)
        i.putExtra("channel_id", channelId)
        i.putExtra(NotificationOptions.TAG_SHOW_WHEN, notificationOptions.showWhen)
        i.putExtra(NotificationOptions.TAG_SMALL_ICON, notificationOptions.smallIconId)
        i.putExtra(NotificationOptions.TAG_LARGE_ICON, notificationOptions.largeIconId)
        i.putExtra(NotificationOptions.TAG_COLOR, notificationOptions.colorId)
        i.putExtra(NotificationOptions.TAG_EXPANDABLE, notificationOptions.expandable)
        i.putExtra(NotificationOptions.TAG_CATEGORY, notificationOptions.category)
        i.putExtra(NotificationOptions.TAG_SUB_TEXT, notificationOptions.subText)
        i.putExtra(NotificationOptions.TAG_GROUP_KEY, notificationOptions.groupKey)


        return PendingIntent.getBroadcast(context, notificationId, i, flagPendingIntent(false))
    }
    /**
     * Builds the Notification and sends a notification in the given channel
     * @param channelId the id of the channel the notification has to be sent
     * @param notificationId the unique id of the notification (can be used to cancel also)
     * @param title the title of the notification
     * @param message the message of the notification
     */
    fun notify(channelId: String,
               notificationId: Int,
               title: String,
               message: String,
               notifyOptions: NotificationOptions = NotificationOptions()){

        val notificationChannelId: String = getChannelIdForNotification(channelId)
        // Create the builder
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, notificationChannelId)

        // Set the priority
        builder.priority = NotificationCompat.PRIORITY_HIGH

        // Set the title and message
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.setTicker(message)

        if (notifyOptions.expandable == NotificationOptions.EXPANDABLE_TEXT){
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
        }

        if (notifyOptions.subText.isNotEmpty())
            builder.setSubText(notifyOptions.subText)

        // Sets the default sounds and vibrations mode
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setColorized(false)

        // Set the Images

        /// *** This is the default app Icon to handle in case small icon is not provided
        val appIconID = context.resources.getIdentifier("icon", "mipmap", context.packageName)

        // Small Icon
        val smallIconID = if (notifyOptions.smallIconId.isNotEmpty()){
            context.resources.getIdentifier(notifyOptions.smallIconId, "drawable", context.packageName)
        }else{
            0
        }
        if (smallIconID <= 0)
            builder.setSmallIcon(appIconID)
        else
            builder.setSmallIcon(smallIconID)

        // Small Icon - Color
        val notificationColorID =if (notifyOptions.colorId.isNotEmpty()) {
            context.resources.getIdentifier(notifyOptions.colorId, "color", context.packageName)
        }else{
            0
        }

        if (notificationColorID > 0)
            builder.color = context.getColor(notificationColorID)
        else
            builder.color = DEFAULT_NOTIFICATION_ICON_COLOR

        // Large Icon
        if (notifyOptions.largeIconId.isNotEmpty()){
            val largeIconID = context.resources.getIdentifier(notifyOptions.largeIconId, "drawable", context.packageName)
            if (largeIconID >= 0){
                val largeIcon = BitmapFactory.decodeResource(context.resources, largeIconID)
                builder.setLargeIcon(largeIcon)

            }
        }

        // Expandable Image
        if (notifyOptions.expandable == NotificationOptions.EXPANDABLE_IMAGE){
            if(notifyOptions.expandableImage.isNotEmpty()){
                val expandImageID = context.resources.getIdentifier(notifyOptions.expandableImage, "drawable", context.packageName)
                if (expandImageID >= 0) {
                    val expandImage = BitmapFactory.decodeResource(context.resources, expandImageID)
                    builder.setStyle( NotificationCompat.BigPictureStyle()
                            .bigPicture(expandImage)
                    )
                }
            }
        }

        val isGrouped = if (notifyOptions.groupKey.isNotEmpty()) {
            builder.setGroup(notifyOptions.groupKey)
            true
        }
        else false

        // Showing time
        builder.setShowWhen(notifyOptions.showWhen)

        // Badge Icon Type
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        builder.setNumber(1)

        // What happens when you click the notification
        builder.setContentIntent(getPendingIntentForAction())
        builder.setAutoCancel(true)
        builder.setCategory(notifyOptions.category)

        val tag = notifyOptions.tag

        // Show the notification based on tag
        if (tag.isEmpty())
            notificationManager.notify(notificationId, builder.build())
        else
            notificationManager.notify(tag, notificationId, builder.build())

        // Show a summary Notification
        if (isGrouped) {
            val summaryId = notifyOptions.summaryId
            val notification = getSummaryNotification(channelId,
                notifyOptions.summaryText,
                notifyOptions.groupKey,
                if (smallIconID >= 0) smallIconID else appIconID
            )
            if (tag.isEmpty())
                notificationManager.notify(summaryId, notification)
            else
                notificationManager.notify(tag, summaryId, notification)
        }

    }

    private fun getSummaryNotification(channelId: String,
                                       summaryText: String,
                                       groupKey: String,
                                       iconId: Int  ): Notification{
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)

        builder.setContentTitle(summaryText)
        builder.setSmallIcon(iconId)
        builder.setGroup(groupKey)
        builder.setGroupSummary(true)

        return builder.build()
    }

    /**
     * Gets the pending intent for opening the app when the notification is clicked
     * @return Pending intent for opening the app, and null if it fails to find the application
     */
    private fun getPendingIntentForAction(): PendingIntent?{

        // Get the godot game class
        val appClass: Class<*>? = try {
            Class.forName(APP_CLASS_NAME)
        } catch (e: ClassNotFoundException) {
            // app not found, do nothing
            return null
        }

        val appIntent = Intent(context, appClass)

        appIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                          Intent.FLAG_ACTIVITY_NEW_TASK or
                          Intent.FLAG_ACTIVITY_CLEAR_TASK


        return PendingIntent.getActivity(context, 0,
                                         appIntent,
                                         flagPendingIntent(false)
        )
    }

    /**
     * Gets the flags for pending intent based on pending intent can be mutable or not
     * @param mutable: true if mutable pending intent else false
     */
    private fun flagPendingIntent(mutable: Boolean): Int {
        return if (Build.VERSION.SDK_INT >= 31) {
                if (mutable) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                }
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
    }


    /**
     * Checks if the App Notification is blocked by User
     */
    fun areNotificationsEnabled(): Boolean
    {
        return notificationManager.areNotificationsEnabled()
    }

    /**
     * Checks if the App Notification is blocked by User
     */
    fun areNotificationsPaused(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notificationManager.areNotificationsPaused()
        }else false
    }

    /**
     * Checks if the Channel is Blocked is blocked by User
     */
    fun isChannelBlocked(channelId: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = getNotificationChannel(channelId)
            return channel != null && channel.importance == NotificationManager.IMPORTANCE_NONE
        }
        else false
    }

    /**
     * Returns a list of active status bar notifications
     */
    fun getAllActiveNotifications(): Array<out StatusBarNotification> {
        return notificationManager.activeNotifications
    }

    /**
     * Cancels any active notification, if tag is provided then tag is used also to filter
     */
    fun cancelActiveNotification(notificationId: Int, tag: String = ""){

        if (tag.isNotEmpty()){
            notificationManager.cancel(tag, notificationId)
        }
        else{
            notificationManager.cancel(notificationId)
        }
    }

    /**
     * Cancels any pending notification, (Notification waiting to be sent)
     */
    fun cancelPendingNotification(notificationId: Int, notifyOptions: NotificationOptions){
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sender: PendingIntent = getAlarmPendingIntent("", notificationId, "", "", notifyOptions)
        am.cancel(sender)
    }

}