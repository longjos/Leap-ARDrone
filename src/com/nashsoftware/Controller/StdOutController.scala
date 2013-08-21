package com.nashsoftware.Controller

/**
 * Created with IntelliJ IDEA.
 * User: jlong
 * Date: 8/18/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
class StdOutController extends BaseController {

  var controlStatus: ControlStatus = new ControlStatus(0,0,0,0)


  def land() = {
    if (state != DroneState.LANDED) {
      println("Landing")
      state = DroneState.LANDED
    }
  }

  def takeOff() = {
    if (state != DroneState.HOVERING) {
      println("Launching")
      state = DroneState.HOVERING
    }
  }

  def move(pitch: Float, roll: Float, gaz: Float, yaw: Float) = {
    if (state == DroneState.HOVERING) {
      state =DroneState.FLYING
      println("Moving")
    }
    if (state == DroneState.FLYING) {
      controlStatus = new ControlStatus(pitch, roll, yaw, gaz)
    }
  }

  def emergencyLand() = {
    println("Crashing")
  }

  def hover() = {
    if (state != DroneState.HOVERING) {
      println("HOVERING")
      state = DroneState.HOVERING
    }
  }

  def trim() = {
    println("Reset Trim")

  }

}

