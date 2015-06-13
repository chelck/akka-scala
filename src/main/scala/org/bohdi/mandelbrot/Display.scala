package org.bohdi.mandelbrot

import java.awt.{Dimension, Graphics}
import javax.swing.{JFrame, JPanel}

object Display {

}

class Display(env: Environment, pallete: Pallete) extends JFrame {
  val panel = new MandelbrotePanel(env, pallete)
  setContentPane(panel)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setSize(new Dimension(env.width, env.height))
  setVisible(true)

  def setPoints(points: Map[(Int, Int), Int]) = panel.setPoints(points)
}

class MandelbrotePanel(env: Environment, pallete: Pallete) extends JPanel {
  private var points: Map[(Int, Int), Int] = Map()

  def setPoints(xpoints: Map[(Int, Int), Int]): Unit = {
    points = xpoints
    repaint()
  }

  override def paintComponent(g: Graphics) {
    println(s"About to draw")


    for (((px,py), iterations) <- points) {
      g.setColor(pallete(iterations))
      g.drawLine(px, py, px, py)
    }

    println(s"done to draw")
  }


}