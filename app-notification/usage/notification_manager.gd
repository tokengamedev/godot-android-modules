# Notification Manager is a singleton to be added as part of autoload
extends Node

# enum to hold the Importance values corresponding to NotificationManager.IMPORTANCE_* 
# in android api
enum {IMPORTANCE_DEFAULT = 3, IMPORTANCE_HIGH = 4, IMPORTANCE_LOW = 2 }


# ids of the channel
const DEFAULT_CHANNEL = ""
const AFTER_CHANNEL ="after_channel"
const REPEAT_CHANNEL = "repeat_channel"

# Holds the name, importance and 
const CHANNELS = {
	AFTER_CHANNEL: ["After Channel", IMPORTANCE_HIGH,"Channel for Important Communications"],
	REPEAT_CHANNEL: ["Repeat Channel", IMPORTANCE_DEFAULT,"Channel for Reminders"]
}

# Holds the notification ids forcorresponding channels, 
# It can be different for each notification 
const notificationIds = {
	DEFAULT_CHANNEL: 1,
	AFTER_CHANNEL: 2,
	REPEAT_CHANNEL: 3
}
const REPEAT_CHANNEL_CUSTOMIZATION = [true, "", "my_large_icon", "my_color"]

var notifier


# Called when the node enters the scene tree for the first time.
func _ready():
	
	# Check if the Plugin is available
	if Engine.has_singleton("AppNotification"):

		# Get a reference to the singleton
		notifier = Engine.get_singleton("AppNotification")

		# call to setup the channels if not created.
		setup_channels()
		

# Creates all the channels as required. 
# Will work on androiid devices with api version 26 or higher
# on android dveices lower than api version 26 it will be ignored, even if it is called
func setup_channels():
	if notifier != null:
		# creating the channels
		for channel_key in CHANNELS.keys():
			notifier.setupNotificationChannel (
				channel_key, 
				CHANNELS[channel_key][0], 
				CHANNELS[channel_key][1], 
				CHANNELS[channel_key][2] 
			)
		# channel customisations
		notifier.setChannelCustomOptions(
			REPEAT_CHANNEL,
			REPEAT_CHANNEL_CUSTOMIZATION[0],
			REPEAT_CHANNEL_CUSTOMIZATION[1],
			REPEAT_CHANNEL_CUSTOMIZATION[2],
			REPEAT_CHANNEL_CUSTOMIZATION[3]
		)


# Shows an immediate notification when no channel has been provided
func show_notification(title, message):
	print("GODOT: Notification {%s, %s}" % [title, message])
	if notifier != null:
		notifier.showNotification(DEFAULT_CHANNEL, notificationIds[DEFAULT_CHANNEL], title, message)


# Shows a notification after a delay on a custom channel but with default customizations
func show_notification_after(title, message, delay):
	print("GODOT: Notification {%s, %s} after 10 secs" % [title, message])
	if notifier != null:
		notifier.showNotificationAfter(
			AFTER_CHANNEL, 
			notificationIds[AFTER_CHANNEL],
			delay,
			title, 
			message)

# shows notification after regular intervals with separate customizations
# delay = time in seconds after which repeations start
# interval = time in seconds after which the notification will repeat
# e.g., If a 24 hrs repeating notification at 1300 HRS is required and current time is 1100 HRS
# then delay = 7200 and interval = 24 * 3600   
func setup_repeating_notification(title, message, delay, interval):
	if notifier != null:
		notifier.setupRepeatingNotification(
			REPEAT_CHANNEL,
			notificationIds[REPEAT_CHANNEL],
			delay,
			interval,
			title,
			message
		)

# Cancels the after notifictaion if there is any pending
func cancel_after_notification():
	if notifier != null:
		notifier.cancelNotification(notificationIds[AFTER_CHANNEL])
		

# cancels the Repeating notification, if there is any
func cancel_repeating_notification():
	if notifier != null:
		notifier.cancelNotification(notificationIds[REPEAT_CHANNEL])
