package gaml.android.permission

import android.Manifest
import android.Manifest.permission
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot

/**
 * Android plugin to check and request for runtime  permissions for games built with
 * Godot game engine for android devices.
 */
class AndroidPermissionPlugin(godot: Godot): GodotPlugin(godot) {

    companion object {

        private const val PLUGIN_NAME = "AndroidPermission"
        private const val RC_CODE = 989

        // List of constants for results of permission request
        const val PERMISSION_RESULT_GRANTED = 0
        const val PERMISSION_RESULT_DENIED = 1
        const val PERMISSION_RESULT_DENIED_SHOW_RATIONALE = 2

        // List of return values
        const val PERMISSION_CODE_UNAVAILABLE = -1
        const val PERMISSION_CODE_OK = 0

        const val SIGNAL_PERMISSION_REQUEST_COMPLETED = "permission_request_completed"
    }

    private val currentActivity: Activity = activity ?: throw IllegalStateException()
    private val mPermissions = mutableMapOf<Int, String>()

    init {
        // creates the list of permissions for easy access
        mPermissions[1] = permission.ACCESS_COARSE_LOCATION
        mPermissions[2] = permission.ACCESS_FINE_LOCATION
        mPermissions[3] = permission.READ_EXTERNAL_STORAGE
        mPermissions[4] = permission.READ_CONTACTS
        mPermissions[5] = permission.RECORD_AUDIO

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mPermissions[10] = permission.ACCESS_MEDIA_LOCATION
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mPermissions[20] = permission.POST_NOTIFICATIONS
            mPermissions[21] = permission.READ_MEDIA_IMAGES
            mPermissions[22] = permission.READ_MEDIA_AUDIO
            mPermissions[23] = permission.READ_MEDIA_VIDEO
        }
    }
    /**
     * Gets the name of the plugin to be used in Godot.
     */
    override fun getPluginName() = PLUGIN_NAME

    /**
     * Registers all the signals which the game may need to listen to
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            SignalInfo(SIGNAL_PERMISSION_REQUEST_COMPLETED, Any::class.java, String::class.java, Any::class.java)
        )
    }


    /**
     * Checks if a permission has been provided for the current activity or not.
     * @param permissionCode : Integer code for the permissions
     */
    @UsedByGodot
    fun checkPermission(permissionCode: Int) : Int {
        val permissionString = mPermissions[permissionCode] ?: return PERMISSION_CODE_UNAVAILABLE
        return checkPermissionString(permissionString)
    }

    /**
     * Checks if a permission has been provided for the current activity or not.
     * @param permission : String name of the permission. See [Manifest.permission]
     */
    @UsedByGodot
    fun checkPermissionString(permission: String) : Int {
        return when (currentActivity.checkSelfPermission(permission)) {
            PackageManager.PERMISSION_GRANTED -> {

                PERMISSION_RESULT_GRANTED
            }
            else -> {
                val showRationale = currentActivity.shouldShowRequestPermissionRationale(permission)
                if (showRationale)
                    PERMISSION_RESULT_DENIED_SHOW_RATIONALE
                else
                    PERMISSION_RESULT_DENIED
            }
        }
    }

    /**
     * Launches the permission request launcher for the given permission code
     * @param permissionCode one of the defined code for permission
     */
    @UsedByGodot
    fun requestPermission(permissionCode: Int):Int {
        val permissionString = mPermissions[permissionCode] ?: return PERMISSION_CODE_UNAVAILABLE
        requestPermissionString(permissionString)
        return PERMISSION_CODE_OK
    }

    /**
     * Launches the permission request launcher for the given permission string
     * @param permission one of string value as specified in [Manifest.permission]
     */
    @UsedByGodot
    fun requestPermissionString(permission: String) {
        currentActivity.requestPermissions(arrayOf(permission), RC_CODE)
    }


    /**
     * Callback for the Permission request launcher
     */
    override fun onMainRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>?,
        grantResults: IntArray?
    ) {
        super.onMainRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_CODE && permissions != null && permissions.isNotEmpty()){

            val requestedPermission = permissions.first()
            val permissionCode = if (mPermissions.containsValue(requestedPermission)){
                mPermissions.keys.first { requestedPermission == mPermissions[it]}
            }else 0

            if (grantResults?.first() == PackageManager.PERMISSION_GRANTED)

                emitSignal(SIGNAL_PERMISSION_REQUEST_COMPLETED,
                    permissionCode, requestedPermission, PERMISSION_RESULT_GRANTED)
            else

                emitSignal(SIGNAL_PERMISSION_REQUEST_COMPLETED,
                    permissionCode, requestedPermission, PERMISSION_RESULT_DENIED)

        }
    }
}