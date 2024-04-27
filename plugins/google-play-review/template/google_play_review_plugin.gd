@tool
extends EditorPlugin

var export_plugin : %1$sPlugin

func _enter_tree():
	export_plugin = %1$sPlugin.new()
	add_export_plugin(export_plugin)


func _exit_tree():
	remove_export_plugin(export_plugin)
	export_plugin = null


class %1$sPlugin extends EditorExportPlugin:
	const PLUGIN_NAME = "%1$s"
	const PLUGIN_FILE_NAME = "%2$s"

	func _supports_platform(platform):
		if platform is EditorExportPlatformAndroid:
			return true
		return false

	func _get_android_libraries(platform, debug):
		return PackedStringArray([PLUGIN_NAME.path_join(PLUGIN_FILE_NAME)])


	func _get_android_dependencies(platform, debug):
		return PackedStringArray(["com.google.android.play:review:2.0.1"])

	func _get_name():
		return PLUGIN_NAME
