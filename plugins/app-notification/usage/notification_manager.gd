extends Node

# importance of Notification
enum IMPORTANCE { NONE= 0, MIN = 1, LOW = 2, DEFAULT = 3, HIGH = 4,  MAX= 5 }

# Notification IDs
const NOTIFY_IMMEDIATE_ID: int = 100
const NOTIFY_AFTER_ID: int = 101
const NOTIFY_RECURRING_ID: int = 102
const NOTIFY_WITH_TAG: int = 103

const NOTIFY_CUSTOM: int = 200
const NOTIFY_CUSTOM_BIG_TEXT: int = 201
const NOTIFY_CUSTOM_BIG_PICTURE: int = 202
const NOTIFY_CUSTOM_GROUP: int = 203

const NOTIFY_GROUPED_MIN : int = 1000

# Channel Ids
const DEFAULT_CHANNEL_ID := ""
const AFTER_CHANNEL_ID := "after_channel"
const SCHEDULED_CHANNEL_ID := "scheduled_channel"

const NOTIFICATION_CATEGORY_EVENT := "event"

# Channels
const CHANNELS := {
	AFTER_CHANNEL_ID: {
		"name": "After Channel",
		"importance": IMPORTANCE.HIGH,
		"description": "After channel description"
	},

	SCHEDULED_CHANNEL_ID: {
		"name": "Scheduled Channel",
		"importance": IMPORTANCE.DEFAULT,
		"description": "Recurring notifications comes through here"
	},
}
const CHANNEL_OPTIONS := {
	AFTER_CHANNEL_ID :{
		"show_badge" : false,
		"lock_screen_visibility" : 0,
	}
}
const NOTIFICATION_OPTIONS := {
	NOTIFY_CUSTOM: {
			"show_when": true, "small_icon_id": "app_icon",
			"large_icon_id": "large_icon", "color_id": "forest_green",
		},
	NOTIFY_CUSTOM_BIG_TEXT: {
			"show_when": true, "small_icon_id": "app_icon",
			"large_icon_id": "large_icon", "color_id": "royal_blue",
			"sub_text": "Urgent",
			"category": NOTIFICATION_CATEGORY_EVENT,
			"expandable": 1,
		},
	NOTIFY_CUSTOM_BIG_PICTURE: {
			"show_when": true, "small_icon_id": "app_icon",
			"large_icon_id": "large_icon", "color_id": "royal_blue",
			"expandable_image": "demo_picture",
			"sub_text": "Daily",
			"category": NOTIFICATION_CATEGORY_EVENT,
			"expandable": 2
		}
}

const GROUPED_PROPERTIES := {
	"group_key": "com.gaml.demo",
	"group_summary_id": 2000,
	"group_summary_text": "Summary",
}
var notifier

func _ready():
	# Check if the plugin is available
	if Engine.has_singleton("AppNotification"):
		print("Notification Service is available.")
		# Get a reference to the singleton
		notifier = Engine.get_singleton("AppNotification")

		# setup the channels
		_setup_channels()

		# customise the channel
		_customise_channels()

		# customize the notifications
		_customise_notifications()

		# prints all the channels that got created
		print_channels_info()

	else:
		print("Notification Service not available.")

func is_service_available(): return notifier != null

func _setup_channels():
	for id in CHANNELS:
		notifier.setupNotificationChannel(
			id,
			CHANNELS[id].name,
			CHANNELS[id].importance,
			CHANNELS[id].description
		)


func _customise_channels():
	for id in CHANNEL_OPTIONS:
		notifier.setupChannelOptions(id, CHANNEL_OPTIONS[id])


func _customise_notifications():
	for id in NOTIFICATION_OPTIONS:
		notifier.setNotificationCustomOptions(id, NOTIFICATION_OPTIONS[id])


func print_channels_info():
	print("Available Channels:")
	for id in CHANNELS:
		print (notifier.getNotificationChannel(id))


func set_grouped(is_grouped : bool):
	for id in NOTIFICATION_OPTIONS:
		if is_grouped:
			var c = NOTIFICATION_OPTIONS[id].duplicate()
			c.merge(GROUPED_PROPERTIES)
			notifier.setNotificationCustomOptions(id, c)
		else:
			notifier.setNotificationCustomOptions(id, NOTIFICATION_OPTIONS[id])


# Send an immediate notification of particular notification type
func send_notification(title: String, message: String):
	if notifier:
		notifier.showNotification(DEFAULT_CHANNEL_ID, NOTIFY_IMMEDIATE_ID, title, message)


# send the Notification after a delay
func send_notification_after(title: String, message: String, delay: int):
	if notifier:
		if notifier.canPostNotifications(AFTER_CHANNEL_ID):
			notifier.showNotificationAfter(AFTER_CHANNEL_ID, NOTIFY_AFTER_ID, title, message, delay)
		else:
			print("Notification is blocked by user")


# Send the notification at regular intervals
func setup_notification_recurring(title: String, message: String, delay: int, interval: int):
	if notifier:
		if notifier.canPostNotifications(SCHEDULED_CHANNEL_ID):
			notifier.setupRepeatingNotification(SCHEDULED_CHANNEL_ID, NOTIFY_RECURRING_ID, title, message, delay, interval)


func cancel_recurring_notification():
	if notifier:
		notifier.cancelNotification(NOTIFY_RECURRING_ID)


func cancel_delayed_notification():
	if notifier:
		notifier.cancelNotification(NOTIFY_AFTER_ID)


func send_tagged_notification(tag: String, title: String, message: String):
	if notifier:
		notifier.showTaggedNotification(AFTER_CHANNEL_ID, NOTIFY_WITH_TAG,
			tag, title, message, 0, 0)


func send_custom_notification(notification_id: int, title: String, message: String):
	if notifier:
		notifier.showNotification(AFTER_CHANNEL_ID, notification_id, title, message)

