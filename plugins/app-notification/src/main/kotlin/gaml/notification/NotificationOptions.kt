package gaml.notification

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
        const val DEFAULT_EXPANDABLE_IMAGE = ""
        const val DEFAULT_CATEGORY = NotificationCompat.CATEGORY_STATUS
        const val DEFAULT_SUB_TEXT = ""
        const val DEFAULT_GROUP_KEY = ""
        const val DEFAULT_SUMMARY_ID = 0
        const val DEFAULT_SUMMARY_TEXT = ""
        const val DEFAULT_TAG = ""


        const val TAG_SHOW_WHEN = "show_when"
        const val TAG_SMALL_ICON = "small_icon_id"
        const val TAG_LARGE_ICON = "large_icon_id"
        const val TAG_COLOR = "color_id"
        const val TAG_CATEGORY = "category"
        const val TAG_EXPANDABLE = "expandable"
        const val TAG_EXPANDABLE_IMAGE = "expandable_image"
        const val TAG_SUB_TEXT = "sub_text"
        const val TAG_GROUP_KEY = "group_key"
        const val TAG_SUMMARY_ID = "group_summary_id"
        const val TAG_SUMMARY_TEXT = "group_summary_text"
        const val TAG_TAG = "tag"

    }

    var showWhen: Boolean = DEFAULT_SHOW_WHEN
    var smallIconId: String = DEFAULT_SMALL_ICON_ID
    var largeIconId: String = DEFAULT_LARGE_ICON_ID
    var colorId: String = DEFAULT_COLOR_ID
    var expandable: Int = DEFAULT_EXPANDABLE
    var expandableImage: String = DEFAULT_EXPANDABLE_IMAGE
    var category: String = DEFAULT_CATEGORY
    var subText: String = DEFAULT_SUB_TEXT
    var groupKey: String = DEFAULT_GROUP_KEY
    var summaryId: Int = DEFAULT_SUMMARY_ID
    var summaryText: String = DEFAULT_SUMMARY_TEXT
    var tag: String = DEFAULT_TAG

    constructor(
        showWhen: Boolean?,
        smallIconId: String?,
        largeIconId: String?,
        colorId: String?,
        expandable: Int?,
        expandableImage: String?,
        category: String?,
        subText: String?,
        groupKey: String?,
        summaryId: Int?,
        summaryText: String?,
        tag: String?
        ) :this (){

        if (showWhen != null) this.showWhen = showWhen
        if (!smallIconId.isNullOrEmpty()) this.smallIconId = smallIconId
        if (!largeIconId.isNullOrEmpty()) this.largeIconId = largeIconId
        if (!colorId.isNullOrEmpty()) this.colorId = colorId
        if (!category.isNullOrEmpty()) this.category = category
        if (!subText.isNullOrEmpty()) this.subText = subText
        if (!groupKey.isNullOrEmpty()) this.groupKey = groupKey
        if (summaryId!= null) this.summaryId = summaryId
        if (!summaryText.isNullOrEmpty()) this.summaryText = summaryText
        if (expandable!= null) this.expandable = expandable
        if (!expandableImage.isNullOrEmpty()) this.expandableImage = expandableImage
        if (!tag.isNullOrEmpty()) this.tag = tag
    }

    constructor (options: Dictionary):this() {
        if (options.containsKey(TAG_SHOW_WHEN)) this.showWhen = options[TAG_SHOW_WHEN] as Boolean
        if (options.containsKey(TAG_EXPANDABLE)) this.expandable = options[TAG_EXPANDABLE] as Int
        if (options.containsKey(TAG_EXPANDABLE_IMAGE)) this.expandableImage = options[TAG_EXPANDABLE_IMAGE] as String
        if (options.containsKey(TAG_SMALL_ICON)) this.smallIconId = options[TAG_SMALL_ICON] as String
        if (options.containsKey(TAG_LARGE_ICON)) this.largeIconId = options[TAG_LARGE_ICON] as String
        if (options.containsKey(TAG_CATEGORY)) this.category = options[TAG_CATEGORY] as String
        if (options.containsKey(TAG_COLOR)) this.colorId = options[TAG_COLOR] as String
        if (options.containsKey(TAG_SUB_TEXT)) this.subText = options[TAG_SUB_TEXT] as String
        if (options.containsKey(TAG_GROUP_KEY)) this.groupKey = options[TAG_GROUP_KEY] as String
        if (options.containsKey(TAG_SUMMARY_ID)) this.summaryId = options[TAG_SUMMARY_ID] as Int
        if (options.containsKey(TAG_SUMMARY_TEXT)) this.summaryText = options[TAG_SUMMARY_TEXT] as String
        if (options.containsKey(TAG_TAG)) this.tag = options[TAG_TAG] as String
    }
}