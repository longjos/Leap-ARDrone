package com.nashsoftware.Controller

/**
 * Created with IntelliJ IDEA.
 * User: jlong
 * Date: 8/18/13
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class BaseController {

  var state = DroneState.LANDED

  def move(pitch: Float, roll: Float, gaz: Float, yaw: Float)

  def takeOff()

  def land()

  def emergencyLand()

  def hover()

  def trim()
}

object DroneState extends Enumeration {
  type DroneState = Value
  val LANDED, LAUNCHING, FLYING, ERROR, EMERGENCY, HOVERING = Value

}

class ControlStatus(val pitch: Float, val roll: Float, val yaw: Float, val gaz: Float)