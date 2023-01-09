/**
 * GooglePlayBillingPlugin.kt - LICENSE Notice
 * *************************************************************************************************
 *
 * Copyright (c) Token Gamedev, Prakash Das.
 * Copyright (c) Contributors (cf. AUTHORS.md).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ********************************************************************************************** */

package gaml.google.play.game.services

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.google.android.gms.games.Player
import com.google.android.gms.tasks.Task
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo
import org.godotengine.godot.plugin.UsedByGodot


/**
 * Android plugin to incorporate Google play game services client in Godot Game
 */
class GooglePlayGameServicesPlugin(godot:Godot): GodotPlugin(godot) {


    companion object {

    }

    private var player: Player? = null
    private var isSignedIn: Boolean = false

    private val currentActivity: Activity = activity ?: throw IllegalStateException()

    private val context: Context =
        activity?.applicationContext ?: throw IllegalStateException()


    override fun onMainCreate(activity: Activity?): View? {
        PlayGamesSdk.initialize(context)
        return super.onMainCreate(activity)
    }


    override fun onMainResume() {
        signIn()
        super.onMainResume()
    }

    /**
     * Gets the name of the plugin to be used in Godot.
     */
    override fun getPluginName() = "GooglePlayGameServices"


    /**
     * Registers all the signals which the game may need to listen to
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        return mutableSetOf(
            // Signal to show when the Services are available
            SignalInfo("sign_in_success"),
            // Signal to show when the Services are not available
            SignalInfo("sign_in_failed"),
            // fetched the player info
            SignalInfo("player_info_fetched", Any::class.java)
        )
    }

    @UsedByGodot
    fun isReady() = isSignedIn

    /**
     * Fetches the player info
     */
    @UsedByGodot
    fun fetchPlayerInfo() {
        //fetch player info if signed in and player is not available
        if (isReady()) {

            if (player == null) {
                PlayGames.getPlayersClient(currentActivity).currentPlayer.addOnCompleteListener { mTask: Task<Player?>? ->
                    if (mTask != null && mTask.result != null) {
                        player = mTask.result
                        emitSignal("player_info_fetched", DictionaryUtil.getPlayerDictionary(player!!))
                    }
                    else
                        emitSignal("player_info_fetched", Dictionary())
                }
            }
            else {
                emitSignal("player_info_fetched", DictionaryUtil.getPlayerDictionary(player!!))
            }
        }
    }

    /**
     * Signs In to the Play games services, if not signedIn already
     */
    @UsedByGodot
    fun signIn() {

        if (!isSignedIn) {
            // Try signIn if it is not signed in
            val gamesSignInClient = PlayGames.getGamesSignInClient(currentActivity)
            gamesSignInClient.isAuthenticated.addOnCompleteListener { isAuthTask: Task<AuthenticationResult> ->

                setSignIn(isAuthTask.isSuccessful && isAuthTask.result.isAuthenticated)

                // If authentication check fails then only try signIn otherwise not
                if (!isSignedIn) {
                    gamesSignInClient.signIn()
                        .addOnCompleteListener() { signInTask: Task<AuthenticationResult> ->

                            setSignIn(signInTask.isSuccessful && signInTask.result.isAuthenticated)

                            if (!isSignedIn)
                                emitSignal("sign_in_failed")
                        }
                }
            }
        }
    }

    /**
     * Set the signIn value and emit signal if sign in is successful
     */
    private fun setSignIn(value: Boolean) {
        isSignedIn = value
        if (value)
            emitSignal("sign_in_success")
    }
}

