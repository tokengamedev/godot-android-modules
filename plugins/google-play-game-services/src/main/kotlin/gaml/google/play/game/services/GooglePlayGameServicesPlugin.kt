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
import android.util.Log
import android.view.View
import com.google.android.gms.games.AuthenticationResult
import com.google.android.gms.games.GamesSignInClient
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
        const val PLUGIN_NAME = "GooglePlayGameServices"
        const val TAG = PLUGIN_NAME
    }

    private val currentActivity: Activity = activity ?: throw IllegalStateException()
    private val context: Context = currentActivity.applicationContext

    // All the helper modules
    private val signInHelper: SignInHelper = SignInHelper(godot,
        PlayGames.getGamesSignInClient(currentActivity))
    private val playerHelper: PlayerHelper = PlayerHelper(godot,
        PlayGames.getPlayersClient(currentActivity))


    override fun onMainCreate(activity: Activity?): View? {

        return super.onMainCreate(activity)
    }
    /**
     * Gets the name of the plugin to be used in Godot.
     */
    override fun getPluginName() = PLUGIN_NAME

    /**
     * Registers all the signals which the game may need to listen to
     */
    override fun getPluginSignals(): MutableSet<SignalInfo> {
        val signals = mutableSetOf<SignalInfo>()

        signals.addAll(signInHelper.getPluginSignals())
        signals.addAll(playerHelper.getPluginSignals())

        return signals
    }

    /**
     * To be called immediately after Plugin creation in Godot.
     */
    @UsedByGodot
    fun init(){
        Log.d(TAG, "Initializing Play Games Sdk...")
        PlayGamesSdk.initialize(context)
        signInHelper.checkUserIsAuthenticated()
    }

    /**
     * Signs In the Player. It has to be called manually if the player is not automatically signed
     * in by the game
     */
    @UsedByGodot
    fun signIn(){
        Log.d(TAG, "Signing In the player ...")
        signInHelper.signIn()
    }

    /**
     * Fetches the current_player
     * @param forced: true if the cache has to be ignored
     */
    @UsedByGodot
    fun fetchCurrentPlayer(forced: Boolean) {
        playerHelper.fetchCurrentPlayer(forced)

    }
}

