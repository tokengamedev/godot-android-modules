package gaml.notification

/**
 * Helper class to customize notification
 */
class NotificationOptions() {
    companion object {
        private const val DEFAULT_SMALL_ICON_ID = "notification_small_ic"
        private const val DEFAULT_LARGE_ICON_ID = "notification_large_ic"
        private const val DEFAULT_COLOR_ID = "notification_color"

        // Following constant is not private as it is required during intent extra extraction
        const val DEFAULT_SHOW_WHEN = false
    }

    var showWhen: Boolean = false
    var smallIconId: String = ""
    var largeIconId: String = ""
    var colorId: String = ""


    constructor(
        showWhen: Boolean?,
        smallIconId: String?,
        largeIconId: String?,
        colorId: String?
    ) : this() {
        this.showWhen = showWhen ?: DEFAULT_SHOW_WHEN
        this.smallIconId = if (smallIconId.isNullOrEmpty()) DEFAULT_SMALL_ICON_ID else smallIconId
        this.largeIconId = if (largeIconId.isNullOrEmpty()) DEFAULT_LARGE_ICON_ID else largeIconId
        this.colorId = if (colorId.isNullOrEmpty()) DEFAULT_COLOR_ID else colorId
    }
}