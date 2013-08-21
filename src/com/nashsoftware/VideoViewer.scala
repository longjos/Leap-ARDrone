package com.nashsoftware

import swing._
import java.awt.image.BufferedImage

/**
 * Created with IntelliJ IDEA.
 * User: jlong
 * Date: 8/18/13
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
class VideoViewer {

  val panel = new VideoPanel()

  val frame = new MainFrame {
    title = "Images"
    contents = panel
    centerOnScreen()
  }

  frame.open()

  def drawImage(img: BufferedImage) = {
    this.panel.drawImage(img)
  }
}

class VideoPanel extends Panel {
  preferredSize = new Dimension(800, 500)
  var img: BufferedImage = null

  override def paint(g: Graphics2D) {
    g.clearRect(0, 0, 800, 500)
    g.drawImage(img, 0, 0, null)
  }

  def drawImage(img: BufferedImage) = {
    this.img = img
    this.repaint()
  }


}