package com.nashsoftware
import com.nashsoftware.ardrone2.ARDrone2
import com.leapmotion.leap._
import com.codeminders.ardrone._
import java.awt.image.BufferedImage
import java.awt.Color
import com.nashsoftware.Controller.{NetworkController, StdOutController, BaseController}
import org.apache.log4j.Logger
import org.apache.log4j.BasicConfigurator

/**
 * Created with IntelliJ IDEA.
 * User: jlong
 * Date: 8/18/13
 * Time: 1:22 PM
 * To change this template use File | Settings | File Templates.
 */
object ScalaDrone extends App {
  //BasicConfigurator.configure()
  var controller = new Controller()
  controller.addListener(DroneListener)

  System.in.read()
  controller.removeListener(DroneListener)
  DroneListener.droneController.land()
  println("Disconnected")
}

class VideoListener(hud: HUD) extends com.nashsoftware.ardrone2.video.DroneVideoListener {
  override def frameReceived(img: BufferedImage) = {
    hud.updateVideoFrame(img)
  }
}


object DroneListener extends Listener {

  val hud = new HUD()
  //val droneController = new StdOutController()
  val droneController = new NetworkController(new ARDrone2())
  droneController.addImageListener(new VideoListener(hud))

  val flightHand = new FlightHand(droneController)
  val controlHand = new ControlHand(droneController)


  override def onInit(controller: Controller) = {
    println("Initialized")
  }


  override def onFrame(controller: Controller) = {
    if (controller.frame.hands.count > 1) {
      val leftHand = controller.frame.hands.leftmost()
      if (leftHand.isValid) {
        controlHand.handUpdated(leftHand)
      }
    }
    val rightHand = controller.frame.hands.rightmost()
    if (rightHand.isValid) {
      flightHand.handUpdated(rightHand)
      hud.updateStatus(
        droneController.controlStatus.pitch,
        droneController.controlStatus.roll,
        droneController.controlStatus.yaw,
        droneController.controlStatus.gaz
      )
    }
  }
}

class FlightHand(droneController: BaseController) {

  private val pitchAverage = new MovingAverage(1000)
  private val rollAverage = new MovingAverage(1000)
  private val yawAverage = new MovingAverage(1000)
  private val gazAverage = new MovingAverage(1000)

  def handUpdated(hand: Hand) = {
    if (hand.fingers.count() < 2) {
      pitchAverage.reset
      rollAverage.reset
      yawAverage.reset
      gazAverage.reset
      droneController.hover()
    } else {
      val moveCommand = _normalizeVectors(hand.direction.pitch, hand.palmNormal.roll, hand.direction.yaw, hand.palmPosition.getY)
      droneController.move(moveCommand.pitch, moveCommand.roll, moveCommand.gaz, moveCommand.yaw)

    }
  }

  private def _normalizeVectors(
                                 rawPitch: Double,
                                 rawRoll: Double,
                                 rawYaw: Double,
                                 rawGaz: Double
                                 ): MovementCommand = {

    val RollingAverageSize = 100
    val pitchUnit = toUnitValue(rawPitch)
    val rollUnit = toUnitValue(rawRoll)
    val yawUnit = toUnitValue(rawYaw)
    val gazUnit = rawGaz
    val movementBuilder = new MovementCommandBuilder
      
    if (pitchUnit.abs > (2 * pitchAverage.stdDev).abs && pitchAverage.size > RollingAverageSize) {
      movementBuilder.withPitch(pitchUnit - pitchAverage.avg)
    } else {
      pitchAverage(pitchUnit)
    }

    if (rollUnit.abs > (2 * rollAverage.stdDev).abs && rollAverage.size > RollingAverageSize) {
      movementBuilder.withRoll(rollUnit - rollAverage.avg)
    } else {
      rollAverage(rollUnit)
    }

    if (yawUnit.abs > (2 * yawAverage.stdDev).abs && yawAverage.size > RollingAverageSize) {
      movementBuilder.withYaw(yawUnit - yawAverage.avg)
    } else {
      yawAverage(yawUnit)
    }

    if (gazUnit.abs > (2 * gazAverage.stdDev).abs && gazAverage.size > RollingAverageSize) {
      movementBuilder.withGaz((gazUnit - gazAverage.avg) / 200)
    } else {
      gazAverage(gazUnit)
    }

    movementBuilder.build
  }
  
  private def toUnitValue(radianValue: Double) = {
    Math.toDegrees(2 * (radianValue / Math.PI)) / 45
  }
}

class MovementCommandBuilder {
  var p: Float = 0
  var r: Float = 0
  var y: Float = 0
  var g: Float = 0

  def withPitch(_p: Double) = {
    p = _p.toFloat
  }

  def withRoll(_r: Double) = {
    r = (-1 * _r).toFloat
  }

  def withYaw(_y: Double) = {
    y = _y.toFloat
  }

  def withGaz(_g: Double) = {
    g = _g.toFloat
  }

  def build: MovementCommand = {
    new MovementCommand(p, r, y, g)
  }
}

class MovementCommand(val pitch: Float, val roll: Float, val yaw: Float, val gaz: Float)

class MovingAverage(period: Int) {
  private val queue = new scala.collection.mutable.Queue[Double]()
  private var _avg: Double = 0.0
  private var _stdDev: Double = 0.0

  def apply(n: Double) = {
    queue.enqueue(n)
    if (queue.size > period)
      queue.dequeue()
    _avg = queue.sum / queue.size
    _stdDev = {
      math.sqrt(
        queue.foldLeft(0.0)((total: Double, item: Double) => {
          total + math.pow(item - avg, 2)
        }) / queue.size.toDouble)
    }
  }
  
  def avg: Double = _avg
  def stdDev: Double = _stdDev
  def size = queue.size

  def reset = queue.clear()
}

class ControlHand(droneController: BaseController) {

  def handUpdated(hand: Hand) = {
    if (hand.palmVelocity.getY > 1000) {
      droneController.takeOff()
    } else if (hand.palmVelocity.getY < -1000) {

      droneController.land()
    }
  }
}

class HUD {
  val viewer = new VideoViewer()
  var pitch: Double = 0
  var roll: Double = 0
  var yaw: Double = 0
  var gaz: Double = 0
  var bufferedFrame: BufferedImage = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB)

  def updateVideoFrame(videoFrame: BufferedImage) = {
    bufferedFrame = videoFrame
    drawHUD(copyBuffer(bufferedFrame))
  }

  def drawHUD(videoFrame: BufferedImage) = {
    val g = videoFrame.getGraphics
    g.setColor(new Color(0xffff0000))
    g.drawString(
      "Hand Pitch: %.2f ".format(pitch)
      + " Roll: %.2f".format(roll)
      + " Yaw: %.2f".format(yaw)
      + " Gaz: %.2f".format(gaz),
      25,
      25
    )
    viewer.drawImage(videoFrame)
  }

  def updateStatus(_pitch: Double, _roll: Double, _yaw: Double, _gaz: Double) = {
    pitch = _pitch
    roll = _roll
    yaw = _yaw
    gaz = _gaz
    drawHUD(copyBuffer(bufferedFrame))
  }

  def copyBuffer(frame: BufferedImage): BufferedImage = {
    new BufferedImage(frame.getColorModel, frame.copyData(null), frame.getColorModel.isAlphaPremultiplied, null)
  }

}
