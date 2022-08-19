package gaml.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

/**
 * Helper class to send notification
 */
class NotificationHelper (private val context: Context){

    companion object{
        const val DEFAULT_CHANNEL_ID = "default"
        private const val DEFAULT_CHANNEL_NAME = "Default"
        private const val DEFAULT_NOTIFICATION_ICON_COLOR = Color.MAGENTA

        // Channel creation Error
        const val NO_ERROR = 0
        const val INCOMPATIBLE_OS_VERSION = -1
        const val CHANNEL_ALREADY_EXISTS = 1
        const val NO_CHANNEL_AVAILABLE = 2
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
     * @return [Int] NO_ERROR(0), INCOMPATIBLE_OS_VERSION(-1), CHANNEL_ALREADY_EXISTS(1)
     */
    fun createNotificationChannel(channelId: String,
                                  channelName: String,
                                  importance: Int,
                                  channelDescription: String): Int{

        // Cannot do any channel creation below android api version 26
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return INCOMPATIBLE_OS_VERSION

        if(notificationManager.getNotificationChannel(channelName) == null) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    importance
                ).apply { description = channelDescription }
            )
            return NO_ERROR
        }
        else {
            return CHANNEL_ALREADY_EXISTS
        }
    }

    /**
     * Gets a channel registered in the OS for Notification
     * @return [NotificationChannel] if the channel is registered with the name else null
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNotificationChannel(channelId: String) :NotificationChannel?{
        return notificationManager.getNotificationChannel(channelId)
    }

    /**
     * Gets a default channel. Recommended to create a channel and use it.
     * Default channel is for safeguard purpose only.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDefaultChannel() :NotificationChannel?{
        createNotificationChannel(
            DEFAULT_CHANNEL_ID,
            DEFAULT_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
            "Default channel"
        )
        return getNotificationChannel(DEFAULT_CHANNEL_ID)
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

        val notificationChannelId = getNotificationChannelId(channelId)
        // Create the builder
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, notificationChannelId)

        // Set the priority
        builder.priority = NotificationCompat.PRIORITY_HIGH

        // Set the title and message
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.setTicker(message)

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
        // Showing time
        builder.setShowWhen(notifyOptions.showWhen)

        // Badge Icon Type
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
        builder.setNumber(1)

        // What happens when you click the notification
        builder.setContentIntent(getPendingIntentForAction())
        builder.setAutoCancel(true)
        builder.setCategory(Notification.CATEGORY_EVENT)

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

    private fun getNotificationChannelId(channelId: String): String{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Get the channel
            val channel:NotificationChannel? =
                getNotificationChannel(channelId) ?:
                getDefaultChannel()

            if (channel != null)
                channel.id
            else
                "Stub!"
        }
        else {
            "Stub!"
        }
    }
}