package com.nashsoftware.Controller

import com.nashsoftware.ardrone2.ARDrone2
import com.nashsoftware.ardrone2.video.DroneVideoListener

/**
 * Created with IntelliJ IDEA.
 * User: jlong
 * Date: 8/18/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */

class NetworkNavDataListener extends com.codeminders.ardrone.NavDataListener {
  override def navDataReceived(nd: com.codeminders.ardrone.NavData) = {
    println(nd.getFlyingState)
    println(nd.getMode)
  }
}

class NetworkController(drone: ARDrone2) extends BaseController {
  drone.connect()
  drone.clearEmergencySignal()
  drone.trim()
  drone.addNavDataListener(new NetworkNavDataListener)

  var controlStatus: ControlStatus = new ControlStatus(0,0,0,0)

  def addImageListener(imageListener: DroneVideoListener) = {
    drone.addImageListener(imageListener)
  }

  def land() = {
    if (state != DroneState.LANDED) {
      println("Land")
      state = DroneState.LANDED
      drone.land()
    }
  }

  def takeOff() = {
    if (state != DroneState.LAUNCHING) {
      println("Take Off")
      state = DroneState.LAUNCHING
      drone.takeOff()
    }
  }

  def move(pitch: Float, roll: Float, gaz: Float, yaw: Float) = {
    if (state == DroneState.HOVERING) {
      state = DroneState.FLYING
    }
    if (state == DroneState.FLYING) {
      controlStatus = new ControlStatus(pitch, roll, yaw, gaz)
    }
    drone.move(roll, pitch, gaz, yaw)
  }

  def emergencyLand() = {
    drone.sendEmergencySignal()
  }

  def hover() = {
    if (state != DroneState.HOVERING) {
      drone.hover()
      state = DroneState.HOVERING
    }
  }

  def trim() = {
    drone.trim()
  }

}
