package gaml.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationReceiver: BroadcastReceiver() {

    companion object{
        private const val TAG = "Notification"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        //Log.i(TAG, "Received notification:")

        // do not do anything if context or intent is null
        if (context == null || intent == null) return

        // Get the Message details from Intent
        val message = intent.getStringExtra("message") ?: ""
        val notificationId = intent.getIntExtra("notification_id", 0)
        val title = intent.getStringExtra("title") ?: ""
        val channelId = intent.getStringExtra("channel_id") ?: ""

        //Log.i(TAG, "title=$title, message=$message, channel=$channelId")

        // Get the Custom Options details
        val options = NotificationOptions(
            intent.getBooleanExtra(NotificationOptions.TAG_SHOW_WHEN, NotificationOptions.DEFAULT_SHOW_WHEN),
            intent.getStringExtra(NotificationOptions.TAG_SMALL_ICON),
            intent.getStringExtra(NotificationOptions.TAG_LARGE_ICON),
            intent.getStringExtra(NotificationOptions.TAG_COLOR),
            intent.getIntExtra(NotificationOptions.TAG_EXPANDABLE, 0),
            intent.getStringExtra(NotificationOptions.TAG_CATEGORY),
            intent.getStringExtra(NotificationOptions.TAG_SUB_TEXT),

        )

        val notifyHelper = NotificationHelper(context)

        notifyHelper.notify(channelId, notificationId, title, message, options)

    }
}