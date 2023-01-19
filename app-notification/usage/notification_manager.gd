extends Node

# Notification IDs
enum { NOTIFY_IMMEDIATE = 100, NOTIFY_DELAYED = 101, NOTIFY_SCHEDULED= 102}

# Notification Importance
enum IMPORTANCE { NONE= 0, MIN = 1, LOW = 2, DEFAULT = 3, HIGH =4,  MAX=5  }

# Channel IDs
const DEFAULT_CHANNEL_ID := "" 
const AFTER_CHANNEL_ID := "after_channel"
const SCHEDULED_CHANNEL_ID := "scheduled_channel"

# Notification Categories
const NOTIFICATION_CATEGORY_STATUS := "status"
const NOTIFICATION_CATEGORY_EVENT := "event"

# Channels 
const CUSTOM_CHANNELS := {
	AFTER_CHANNEL_ID: {"name": "After Channel", "importance":IMPORTANCE.HIGH, "description": "After channel description" },
	SCHEDULED_CHANNEL_ID: {"name": "Scheduled Channel", "importance":IMPORTANCE.HIGH, "description": "Recurring notifications comes through here" }
}

# Notification Customizations
const NOTIFICATION_CUSTOMIZATIONS := {
	NOTIFY_IMMEDIATE: {"large_icon_id":"custom_large_icon", "sub_text": "Urgent", "expandable": 1},
	NOTIFY_DELAYED: {"small_icon_id": "custom_icon", "color_id": "green", "expandable": 2, "large_icon_id":"demo_picture2", "sub_text": "Urgent", "category": NOTIFICATION_CATEGORY_EVENT }
}

var notifier

func _ready():	
	# Check if the plugin is available
	if Engine.has_singleton("AppNotification"):

		# Get a reference to the singleton
		notifier = Engine.get_singleton("AppNotification")
		
		# setup the channels
		setup_channels()
		
		# setup the customization
		setup_customisation()
		print("Notification Service is available.")
	else:
		print("Notification Service not available.")

# creates the channels
func setup_channels():
	for id in CUSTOM_CHANNELS:
		notifier.setupNotificationChannel(
			id,
			CUSTOM_CHANNELS[id].name,
			CUSTOM_CHANNELS[id].importance,
			CUSTOM_CHANNELS[id].description
		)

# initialises the customisation
func setup_customisation():
	for id in NOTIFICATION_CUSTOMIZATIONS:
		notifier.setNotificationCustomOptions(id,NOTIFICATION_CUSTOMIZATIONS[id])

# Send an immediate notification of particular notification type
func send_notification(type: int, title: String, message: String):
	if notifier:
		notifier.showNotification(DEFAULT_CHANNEL_ID, type, title, message)


# send the Notification after a delay
func send_notifications_after( title: String, message: String, delay: int):
	if notifier:
		if notifier.canPostNotifications(AFTER_CHANNEL_ID):
			notifier.showNotificationAfter(AFTER_CHANNEL_ID, NOTIFY_DELAYED, title, message, delay)
		else:
			print("Notification is blocked by user")

# send the notification att regular intervals
func send_notification_recurring(title: String, message: String, delay: int, interval: int):
	if notifier:
		notifier.setupRepeatingNotification(SCHEDULED_CHANNEL_ID, NOTIFY_SCHEDULED,title, message, delay, interval)


# Lists all the active notification
func get_active_notifications():
	if notifier:
		return notifier.getActiveNotifications().notifications

# cancels the pending repeating notification
func cancel_repeating_notification():
	if notifier:
		notifier.cancelNotification(NOTIFY_SCHEDULED)


