extends Node

const PLUGIN_NAME = "GooglePlayGameServices"

var play_game_plugin = null

func _ready() -> void:
	# Check if the plugin is available
	if Engine.has_singleton(PLUGIN_NAME):

		# Get a reference to the singleton
		play_game_plugin = Engine.get_singleton(PLUGIN_NAME)

		play_game_plugin.connect("user_authenticated", _on_user_authenticated)
		play_game_plugin.connect("player_fetched", _on_player_fetched)

		# Call to init the SDK and user automatically sign In if configured to
		play_game_plugin.init()


	else:
		print("Review service not available")


## When called directly it will signIn
func sign_in():
	if play_game_plugin:
		play_game_plugin.signIn()


## Launches the In store review
func fetch_current_player():
	if play_game_plugin:
		play_game_plugin.fetchCurrentPlayer()


func _on_user_authenticated(success: bool):
	if success:
		print("User has been authenticated.")
	else:
		print("User not signed in.")


func _on_player_fetched(player_info: Dictionary):
	if player_info.is_empty():
		print("Unable to find player")
	else:
		print("Player:", player_info )
