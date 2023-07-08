package gaml.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.godotengine.godot.Dictionary

/**
 * Helper class to send notification
 */
class NotificationHelper (private val context: Context){

    companion object{
        private const val DEFAULT_CHANNEL_ID = "default"
        private const val DEFAULT_CHANNEL_NAME = "Default"
        private const val DEFAULT_NOTIFICATION_ICON_COLOR = Color.MAGENTA

        // Channel creation Error
        val DEFAULT_NOTIFICATION_OPTIONS = NotificationOptions()
    }

    private val notificationManager: NotificationManager =
        (context.getSystemService(Context.NOTIFICATION_SERVICE) ?: throw IllegalStateException()) as NotificationManager

    /**
     * Checks if the App Notification is blocked by User
     * @return true if enabled else blocked by user
     */
    fun areNotificationsEnabled(): Boolean
    {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else true
    }

    /**
     * Checks if the App Notification is blocked by User
     * @return true if enabled else blocked by user
     */
    fun areNotificationsPaused(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            notificationManager.areNotificationsPaused()
        }else false
    }

    /**
     * Checks if the Channel is Blocked is blocked by User
     * @param channelId unique id of the channel
     * @return true if it is blocked else enabled fro notification
     */
    fun isChannelBlocked(channelId: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = getNotificationChannel(getNotificationChannelId(channelId))
            return channel != null && channel.importance == NotificationManager.IMPORTANCE_NONE
        }
        else false
    }

    /**
     * Creates a Notification channel (if not existing with the same id). This can be called
     * during start of the application/game, without issues.
     * @param channelId unique id of the channel
     * @param channelName display name of the channel
     * @param importance the importance of the messages through this channel.
     * See [NotificationManager].IMPORTANCE_* for possible values
     * @param channelDescription a descriptive text about the channel
     */
    fun createNotificationChannel(channelId: String,
                                  channelName: String,
                                  importance: Int,
                                  channelDescription: String) {

        // Cannot do any channel creation below android api version 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    /**
     * Removes the notification channel if exists. It will not delete default channel
     */
    fun removeNotificationChannel(channelId: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannelId = getNotificationChannelId(channelId)
            if (notificationChannelId != DEFAULT_CHANNEL_ID){
                notificationManager.deleteNotificationChannel(notificationChannelId)
            }
        }
    }

    /**
     * Returns a list of active status bar notifications
     * @return array of trimmed notification objects
     */
    fun getAllActiveNotifications(): MutableList<Dictionary> {
        val allNotifications = mutableListOf<Dictionary>()
        for (notification in notificationManager.activeNotifications){
            val notificationDict = Dictionary()
            notificationDict["id"] = notification.id
            notificationDict["tag"] = notification.tag
            notificationDict["package_name"] = notification.packageName
            notificationDict["post_time"] = notification.postTime
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationDict["channel_id"] = notification.notification.channelId
            }
            else{
                notificationDict["channel_id"] = ""
            }

            allNotifications.add(notificationDict)
        }

        return allNotifications
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

        val notificationChannelId: String = getNotificationChannelId(channelId)
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
                if(notifyOptions.expandable == NotificationOptions.EXPANDABLE_IMAGE){
                    builder.setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(largeIcon)
                        .bigLargeIcon(largeIcon)
                        )
                }

            }

        }
        // Showing time
        builder.setShowWhen(notifyOptions.showWhen)

        // Badge Icon Type
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        builder.setNumber(1)

        // What happens when you click the notification
        builder.setContentIntent(getPendingIntentForAction())
        builder.setAutoCancel(true)
        builder.setCategory(notifyOptions.category)

        // Show the notification
        notificationManager.notify(notificationId, builder.build())
    }

    /**
     * Gets the pending intent for opening the app when the notification is clicked
     * @return Pending intent for opening the app, and null if it fails to find the application
     */
    private fun getPendingIntentForAction(): PendingIntent?{

        // Get the godot game class
        val appClass: Class<*>? = try {

            Class.forName("com.godot.game.GodotApp")
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
    fun flagPendingIntent(mutable: Boolean): Int {
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
     * Gets a default channel. Recommended to create a channel and use it.
     * Default channel is for safeguard purpose only.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDefaultChannelId() : String {
        val channel = getNotificationChannel(DEFAULT_CHANNEL_ID)
        if (channel == null) {
            createNotificationChannel(
                DEFAULT_CHANNEL_ID,
                DEFAULT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
                "Default channel"
            )
        }
        return  DEFAULT_CHANNEL_ID
    }

    /**
     * gets a notification channel based on id and other factors
     * @return NotificationChannel if able to find else null
     */
    private fun getNotificationChannel(channelId: String): NotificationChannel?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.getNotificationChannel(channelId)
        }else{
            null
        }
    }

    private fun getNotificationChannelId(channelId: String): String{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return channelId.ifEmpty { getDefaultChannelId() }
        }
        else ""
    }
}