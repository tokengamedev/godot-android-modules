# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [4] - 2023-12-31

#### Google Play Review (1.0.2)
- Updated the kotlin version tp 1.12.0


## [3] - 2023-07-08


### Added

#### ApplovinMax (1.0.0)

- Module to provide ApplovinMax mediation so that users can benefit from ads from multiple networks

#### Ogury Consent Manager (1.0.0)

- Module to manage consent management from user for various geographic restricted usage of data.

### Changed

#### Core
- Updated Kotlin version to 1.10.1
- Updated Android build tools to 7.4.2

#### AppNotification (1.1.1)

- Minor bug fix related to sub text
- Updated Kotlin version to 1.10.1
  
#### Google Play Billing (1.0.1)

- Updated library version to 6.0.1. It is backward compatible.
- Updated Kotlin version to 1.10.1


#### Google Play Review(1.0.1)

- Updated Kotlin version to 1.10.1


### Removed/Hold

#### Google Play Game Services

- Not recommended to use at this moment as there is a serious looping issue while logging in. Needs to be fixed.


## [1.1.0] - 2023-01-19


### Changed

#### Core
- Migrated build system from 7.4.0 to 7.6
- Updated Kotlin to 1.8.0


#### AppNotification

- Updated Notification APIs to handle more customizations (Breaks Compatibility)
- Apis to Query Notifications
- Apis to check if User blocked notification


## [1.0.0] - 2023-01-09


### Added

#### AppNotification

- Module to send basic app notification from Godot Game

#### Google Play Review

- Module to launch in-app review or launch store for review of the game

#### Google Play Billing

- Module to integrate billing service with the Google play store

#### Google Play games Service

- Module to integrate google play games services with play store

