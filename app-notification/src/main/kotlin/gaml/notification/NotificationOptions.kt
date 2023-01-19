package gaml.notification

import android.app.Notification
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import org.godotengine.godot.Dictionary

/**
 * Helper class to customize notification
 */
class NotificationOptions() {
    companion object {
        // Expandable Values
        const val EXPANDABLE_NONE = 0
        const val EXPANDABLE_TEXT = 1
        const val EXPANDABLE_IMAGE = 2

        const val DEFAULT_SHOW_WHEN = false
        const val DEFAULT_SMALL_ICON_ID = "notification_small_ic"
        const val DEFAULT_LARGE_ICON_ID = "notification_large_ic"
        const val DEFAULT_COLOR_ID = "notification_color"
        const val DEFAULT_EXPANDABLE = EXPANDABLE_NONE
        const val DEFAULT_CATEGORY = NotificationCompat.CATEGORY_STATUS
        const val DEFAULT_SUB_TEXT = ""

        const val TAG_SHOW_WHEN = "show_when"
        const val TAG_SMALL_ICON = "small_icon_id"
        const val TAG_LARGE_ICON = "large_icon_id"
        const val TAG_COLOR = "color_id"
        const val TAG_CATEGORY = "category"
        const val TAG_EXPANDABLE = "expandable"
        const val TAG_SUB_TEXT = "sub_text"


    }
    var showWhen: Boolean = DEFAULT_SHOW_WHEN
    var smallIconId: String = DEFAULT_SMALL_ICON_ID
    var largeIconId: String = DEFAULT_LARGE_ICON_ID
    var colorId: String = DEFAULT_COLOR_ID
    var expandable: Int = DEFAULT_EXPANDABLE
    var category: String = DEFAULT_CATEGORY
    var subText: String = DEFAULT_SUB_TEXT

    constructor(
        showWhen: Boolean?,
        smallIconId: String?,
        largeIconId: String?,
        colorId: String?,
        expandable: Int?,
        category: String?,
        subText: String?,
    ) : this() {
        if (showWhen != null) this.showWhen = showWhen
        if (!smallIconId.isNullOrEmpty()) this.smallIconId = smallIconId
        if (!largeIconId.isNullOrEmpty()) this.largeIconId = largeIconId
        if (!colorId.isNullOrEmpty()) this.colorId = colorId
        if (!category.isNullOrEmpty()) this.category = category
        if (!subText.isNullOrEmpty()) this.subText = subText
        if (expandable!= null) this.expandable = expandable
    }
    constructor(options: Dictionary): this(){
        if (options.containsKey(TAG_SHOW_WHEN)) this.showWhen = options[TAG_SHOW_WHEN] as Boolean
        if (options.containsKey(TAG_EXPANDABLE)) this.expandable = options[TAG_EXPANDABLE] as Int
        if (options.containsKey(TAG_SMALL_ICON)) this.smallIconId = options[TAG_SMALL_ICON] as String
        if (options.containsKey(TAG_LARGE_ICON)) this.largeIconId = options[TAG_LARGE_ICON] as String
        if (options.containsKey(TAG_CATEGORY)) this.category = options[TAG_CATEGORY] as String
        if (options.containsKey(TAG_COLOR)) this.colorId = options[TAG_COLOR] as String
        if (options.containsKey(TAG_SUB_TEXT)) this.subText = options[TAG_SUB_TEXT] as String
    }
}