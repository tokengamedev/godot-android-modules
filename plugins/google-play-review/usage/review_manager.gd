extends Node

signal review_completed()
signal review_launch_failed(error_string: String)

const PLUGIN_NAME = "GooglePlayReview"

enum ERROR {
	INTERNAL_ERROR = -100,
	INVALID_REQUEST = -2,
	PLAY_STORE_NOT_FOUND = -1,
	NO_ERROR = 0
}

var review_plugin = null

func _ready() -> void:
	# Check if the plugin is available
	if Engine.has_singleton(PLUGIN_NAME):

		# Get a reference to the singleton
		review_plugin = Engine.get_singleton(PLUGIN_NAME)

		review_plugin.connect("flow_completed", _on_review_flow_completed)
		review_plugin.connect("flow_launch_error", _on_review_flow_error)

	else:
		print("Review service not available")


## Launches the In app review
func launch_in_app_review():
	if review_plugin:
		review_plugin.launchInAppReview()


## Launches the In store review
func launch_in_store_review():
	if review_plugin:
		review_plugin.launchInStoreReview()


func _on_review_flow_completed():
	review_completed.emit()


func _on_review_flow_error(error_code: int):
	review_launch_failed.emit(_error_string(error_code))


func _error_string(code: int) -> String:
	match(code):
		ERROR.INTERNAL_ERROR: return "Internal Error"
		ERROR.INVALID_REQUEST: return "Invalid Request"
		ERROR.PLAY_STORE_NOT_FOUND: return "Play Store not found"
		ERROR.NO_ERROR: return "No Error"
		_: return ""
