package gaml.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.service.notification.StatusBarNotification
import org.godotengine.godot.Dictionary

/**
 * Utility class to convert objects into Godot Dictionary
 */
class NotificationUtil {
    companion object {

        fun getNotificationChannelDictionary(channel: NotificationChannel): Dictionary{
            val dictionary = Dictionary()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dictionary["id"] = channel.id
                dictionary["description"] = channel.description
                dictionary["importance"] = channel.importance
                dictionary["name"] = channel.name
                dictionary["group"] = channel.group
                dictionary["show_badge"] = channel.canShowBadge()
                dictionary["lock_screen_visibility"] = channel.lockscreenVisibility
            }
            return dictionary
        }

        fun getNotificationOptionsDictionary(options: NotificationOptions): Dictionary {
            val dictionary = Dictionary()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dictionary["category"] = options.category
                dictionary["color"] = options.colorId
                dictionary["small_icon"] = options.smallIconId
                dictionary["large_icon"] = options.largeIconId
                dictionary["expandable"] = options.expandable
                dictionary["show_when"] = options.showWhen
                dictionary["sub_text"] = options.subText
                dictionary["group_key"] = options.groupKey
            }

            return dictionary
        }

        fun getNotificationDictionary(notification: StatusBarNotification): Dictionary {

            val notificationDict = Dictionary()

            notificationDict["id"] = notification.id
            notificationDict["tag"] = notification.tag
            notificationDict["package_name"] = notification.packageName
            notificationDict["post_time"] = notification.postTime
            notificationDict["is_group"] = notification.isGroup
            notificationDict["group_key"] = notification.groupKey

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                notificationDict["channel_id"] = notification.notification.channelId
            else
                notificationDict["channel_id"] = ""

            return notificationDict
        }
    }
}