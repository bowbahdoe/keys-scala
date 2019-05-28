package com.mrmccue.keys.view

import com.mrmccue.keys.model
import com.mrmccue.keys.model._
import com.mrmccue.keys.view.SwingKeysView.Canvas
import javax.swing.{JFrame, WindowConstants}
import java.awt._
import java.awt.event.{MouseEvent, MouseListener}
import java.awt.geom.{AffineTransform, Point2D}

import com.mrmccue.keys.model.Direction._
import com.mrmccue.keys.model.Team.{Gold, Silver}
import javax.swing.JPanel

import scala.collection.mutable

private[view] final class SwingKeysView extends JFrame("Keys") with KeysView {
  private val canvas: Canvas = new Canvas(SwingKeysView.CANVAS_WIDTH, SwingKeysView.CANVAS_HEIGHT)

  override def start(): Unit = {
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    this.layoutUI()
    this.setVisible(true)
    this.setFocusable(true)
    this.setResizable(false)
    this.pack()
  }

  private def layoutUI(): Unit = {
    this.add(this.canvas)
  }


  override def addListener(listener: ViewActionListener): Unit =
    this.canvas.addListener(listener)

  override def renderBoard(board: Board): Unit =
    this.canvas.renderBoard(board)

  override def renderRespawnPoints(positions: Seq[model.Position]): Unit =
    this.canvas.renderRespawnPoints(positions)

  override def redraw(): Unit =
    this.canvas.redraw()

  override def renderPossibleMoves(positions: Seq[Position]): Unit =
    this.canvas.renderPossibleMoves(positions)

  override def clearPossibleMoves(): Unit =
    this.canvas.clearPossibleMoves()

  override def renderPossibleRotations(rotations: Seq[(Position, Direction)]): Unit =
    this.canvas.renderPossibleRotations(rotations)

  override def clearPossibleRotations(): Unit =
    this.canvas.clearPossibleRotations()
}

object SwingKeysView {
  private val CANVAS_WIDTH = 600
  private val CANVAS_HEIGHT = 600
  private val CELL_COLORS: (Color, Color) = (Color.DARK_GRAY, Color.MAGENTA.darker())
  private val UNLOCKED_COLORS: (Color, Color) = (Color.YELLOW.darker(), Color.LIGHT_GRAY)
  private val LOCKED_COLORS: (Color, Color) = (Color.YELLOW.darker().darker(), Color.LIGHT_GRAY.darker())
  private val MOVE_COLOR = Color.BLUE.darker()

  private final class Canvas(width: Int, height: Int) extends JPanel with KeysView {
    private var listeners: mutable.ArrayBuffer[ViewActionListener] = mutable.ArrayBuffer()
    private var board: Option[Board] = None
    private var respawnPoints: Option[Seq[model.Position]] = None
    private var possibleMoves: Option[Seq[model.Position]] = None
    private var possibleRotations: Option[Seq[(model.Position, model.Direction)]] = None

    this.addMouseListener(new MouseListener {
      override def mouseClicked(e: MouseEvent): Unit = {}

      override def mousePressed(e: MouseEvent): Unit = {
        listeners.foreach(_.pressLocation(Position(e.getX / (width / 8), e.getY / (height / 8))))
      }

      override def mouseReleased(e: MouseEvent): Unit = {
        listeners.foreach(_.releaseLocation(Position(e.getX / (width / 8), e.getY / (height / 8))))
      }

      override def mouseEntered(e: MouseEvent): Unit = {/* NoOp */}

      override def mouseExited(e: MouseEvent): Unit = {/* NoOp */}
    })

    override def getPreferredSize: Dimension = {
      new Dimension(width, height)
    }

    override def addListener(listener: ViewActionListener): Unit = listeners += listener

    private def drawPossibleMoves(g: Graphics): Unit = {
      this.possibleMoves match {
        case Some(moves) =>
          moves.foreach(position => {
            g.setColor(MOVE_COLOR)
            val g2d = g.asInstanceOf[Graphics2D]
            val oldStroke = g2d.getStroke
            g2d.setStroke(new BasicStroke(4))
            g.drawRect(position.x * width / 8, position.y * height / 8, width / 8, height / 8)
            g2d.setStroke(oldStroke)
          })
        case None =>
      }
    }

    private def drawUnlockedKeys(g: Graphics): Unit = {
      this.board.map(_.unlocked) match {
        case Some(unlocked) =>
          unlocked.foreach(entry => {
            val pos = entry._1
            val key = entry._2

            key.team match {
              case Gold => g.setColor(UNLOCKED_COLORS._1)
              case Silver => g.setColor(UNLOCKED_COLORS._2)
            }

            val centerX = width / 8 * pos.x + width / 16
            val centerY = height / 8 * pos.y + height / 16

            val dw = width / 32
            val dh = height / 32


            val rotateBy: (Array[Point2D], Int) => Array[Point2D] = (arr, by) => {
              val storeTo: Array[Point2D] = Array.ofDim(arr.length)
              AffineTransform.getRotateInstance(Math.toRadians(by), centerX, centerY)
                .transform(arr, 0, storeTo, 0, 3)
              storeTo
            }

            val downRightTriangle: Array[Point2D] = Array(
              new Point(centerX + dw, centerY + dh),
              new Point(centerX + dw, centerY - dh),
              new Point(centerX - dw, centerY + dh)
            )

            val triangle: Array[Point2D] = rotateBy(downRightTriangle, key.facing match {
              case North => -135
              case NorthEast => -90
              case East => -45
              case SouthEast => 0
              case South => 45
              case SouthWest => 90
              case West => 135
              case NorthWest => 180
            })


            g.fillPolygon(new Polygon(
              triangle.map(_.getX.toInt),
              triangle.map(_.getY.toInt),
              3
            ))
          })
        case None =>
      }
    }

    private def drawLockedKeys(g: Graphics): Unit = {
      this.board.map(_.locked) match {
        case Some(locked) =>
          locked.foreach(entry => {
            val pos = entry._1
            val key = entry._2

            key.team match {
              case Gold => g.setColor(LOCKED_COLORS._1)
              case Silver => g.setColor(LOCKED_COLORS._2)
            }

            val centerX = width / 8 * pos.x + width / 72
            val centerY = height / 8 * pos.y + height / 72

            g.fillOval(centerX, centerY, width / 10 , height / 10)
          })
        case None =>
      }
    }
    private def drawChessBoard(g: Graphics): Unit = {
      for (x <- 0 until 8; y <- 0 until 8) {
        if (x % 2 == 0 && y % 2 == 1 || x % 2 == 1 && y % 2 == 0) {
          g.setColor(CELL_COLORS._1)
        }
        else {
          g.setColor(CELL_COLORS._2)
        }
        g.fillRect(width / 8 * x, height / 8 * y, width / 8, height / 8)
      }
    }

    override def paintComponent(g: Graphics): Unit = {
      super.paintComponent(g)
      drawChessBoard(g)
      drawPossibleMoves(g)
      drawLockedKeys(g)
      drawUnlockedKeys(g)
    }

    override def renderBoard(board: Board): Unit = {
      this.board = Some(board)
    }

    override def renderRespawnPoints(positions: Seq[Position]): Unit = {
      this.respawnPoints = Some(positions)
    }

    override def renderPossibleMoves(positions: Seq[Position]): Unit = {
      this.possibleMoves = Some(positions)
    }

    override def clearPossibleMoves(): Unit = {
      println(this.possibleMoves)
      this.possibleMoves = None
    }

    override def redraw(): Unit = this.repaint()

    override def start(): Unit = {}

    override def renderPossibleRotations(rotations: Seq[(Position, Direction)]): Unit =
      this.possibleRotations = Some(rotations)

    override def clearPossibleRotations(): Unit = ???
  }

}