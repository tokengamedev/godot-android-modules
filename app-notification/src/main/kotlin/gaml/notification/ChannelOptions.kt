package gaml.notification

import org.godotengine.godot.Dictionary

/**
 * Helper class to customize channel of the notification
 */
class ChannelOptions(options: Dictionary) {
    companion object {
        // Expandable Values
        const val VISIBILITY_PRIVATE = 0
        const val VISIBILITY_PUBLIC = 1
        const val VISIBILITY_SECRET = -1

        const val DEFAULT_SHOW_BADGE = false
        const val DEFAULT_VISIBILITY = VISIBILITY_PRIVATE

        const val TAG_SHOW_BADGE = "show_badge"
        const val TAG_VISIBILITY = "lock_screen_visibility"

    }

    var lockScreenVisibility: Int = DEFAULT_VISIBILITY
    var showBadge: Boolean = DEFAULT_SHOW_BADGE


    init {
        if (options.containsKey(TAG_SHOW_BADGE)) this.showBadge = options[TAG_SHOW_BADGE] as Boolean
        if (options.containsKey(TAG_VISIBILITY)) this.lockScreenVisibility = options[TAG_VISIBILITY] as Int
    }
}