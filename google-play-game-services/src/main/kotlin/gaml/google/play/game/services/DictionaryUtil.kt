package gaml.google.play.game.services

import com.google.android.gms.games.Player
import org.godotengine.godot.Dictionary

class DictionaryUtil {

    companion object{
        fun getPlayerDictionary(player: Player): Dictionary{
            val dictionary = Dictionary()

            dictionary["player_id"] = player.playerId
            dictionary["title"] = player.title
            dictionary["display_name"] = player.displayName

            return dictionary
        }
    }

}