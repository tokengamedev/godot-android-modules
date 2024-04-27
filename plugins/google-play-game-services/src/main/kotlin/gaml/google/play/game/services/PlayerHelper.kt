package gaml.google.play.game.services

import com.google.android.gms.games.Player
import com.google.android.gms.games.PlayersClient
import org.godotengine.godot.Dictionary
import org.godotengine.godot.Godot
import org.godotengine.godot.plugin.GodotPlugin
import org.godotengine.godot.plugin.SignalInfo

class PlayerHelper( private val godot: Godot, private val playersClient: PlayersClient) {

    // List of Signals Generated
    private val playerFetchedSignal = SignalInfo("player_fetched", Any::class.java)

    /**
     * Returns the list of signals generated by PlayerHelper
     */
    fun getPluginSignals(): MutableSet<SignalInfo>{
        return mutableSetOf (playerFetchedSignal)
    }

    fun fetchCurrentPlayer(forced: Boolean){
        if (forced)
            playersClient.getCurrentPlayer(true).addOnCompleteListener{ playerTask ->
                val player = if (playerTask.isSuccessful)
                     playerTask.result.get()
                else
                    null

                GodotPlugin.emitSignal(
                    godot,
                    GooglePlayGameServicesPlugin.PLUGIN_NAME,
                    playerFetchedSignal,
                    DictionaryUtil.getPlayerDictionary(player))
            }
        else
            playersClient.currentPlayer.addOnCompleteListener {playerTask ->
                val player = if (playerTask.isSuccessful)
                    playerTask.result
                else
                    null
                GodotPlugin.emitSignal(
                    godot,
                    GooglePlayGameServicesPlugin.PLUGIN_NAME,
                    playerFetchedSignal,
                    DictionaryUtil.getPlayerDictionary(player))
            }
    }
}