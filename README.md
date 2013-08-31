Leap-ARDrone
============

Control a Parrot AR.Drone2 using a Leap Motion Controller

This is an early version. It's probably buggy, fly at your own risk.

### Controls

#### Take off
* Place your right hand open and flat with palm down in the field of view of the Leap Motion
* You should see the overlay on the video image start updating with position information
* Bring your left hand into the field of view of the Leap Motion
* Raise your left hand rapidly upward
* You can now remove your left hand from the field of view

#### Land
* With your right hand in the field of view (Make a fist to hover)
* Bring your left hand into the field of view
* Quickly lower your left hand

#### Hover
* Make a fist with your right hand.
* The AR.Drone should hover in place.
* This will also reset the hand tracking hysteresis.
* Open your hand again, wait a moment while your hand position is normalized, then regain movement control

#### Move
* Move Forward - Tilt open hand toward screen
* Move Back - Tilt open hand away from screen
* Roll Left - Tilt open hand left
* Roll Right - Tile open hand right
* Yaw Left - Rotate your hand counter-clockwise with palm down
* Yaw Right - Rotate your hand clockwise with palm down


## Requirements

* https://github.com/longjos/ARDrone2  An Extension to the JavaDrone library
* Leap Motion SDK
* log4j
