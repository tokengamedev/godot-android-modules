package gaml.google.play.game.services

import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.GamesSignInClient
import com.google.android.gms.tasks.Task
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.GodotPlugin

class SignInHelper(private val godot: Godot, private val signInClient: GamesSignInClient) {

    // List of Signals Generated
    private val userAuthenticatedSignal = SignalInfo(
        "user_authenticated", Boolean::class.javaObjectType)

    /**
     * Returns the list of signals generated by SignInHelper
     */
    fun getPluginSignals(): MutableSet<SignalInfo>{
        return mutableSetOf (userAuthenticatedSignal)
    }

    /**
     * Checks if the user is Authenticated or not. The result will be returned by emitting the
     * [userAuthenticatedSignal] Signal
     */
    fun checkUserIsAuthenticated(){
        signInClient.isAuthenticated().addOnCompleteListener { isAuthTask ->
            handleAuthTask(isAuthTask)
        }
    }
    /**
     * Signs In the user. The result will be returned by emitting the  [userAuthenticatedSignal]
     * Signal
     */
    fun signIn(){
        signInClient.signIn().addOnCompleteListener { isAuthTask ->
            handleAuthTask(isAuthTask)
        }
    }

    private fun handleAuthTask( isAuthenticatedTask: Task<AuthenticationResult>) {

        val isAuthenticated = isAuthenticatedTask.isSuccessful &&
                isAuthenticatedTask.result.isAuthenticated

        // Emits the User Authenticated Signal
        GodotPlugin.emitSignal (
            godot,
            GooglePlayGameServicesPlugin.PLUGIN_NAME,
            userAuthenticatedSignal,
            isAuthenticated
        )
    }
}