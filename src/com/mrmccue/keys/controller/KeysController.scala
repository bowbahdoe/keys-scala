package com.mrmccue.keys.controller

import com.mrmccue.keys.model
import com.mrmccue.keys.model.Team.{Gold, Silver}
import com.mrmccue.keys.model._
import com.mrmccue.keys.view.{KeysView, ViewActionListener}

final class KeysController(private val game: KeysGame, private val view: KeysView) extends ViewActionListener {

  private var pressed: Option[model.Position] = None
  private var rotations: Option[Set[(model.Position, Direction)]] = None

  view.addListener(this)
  view.renderBoard(game.board)


  def start(): Unit = {
    view.start()
  }

  override def pressLocation(position: model.Position): Unit = {
    this.pressed = Some(position)
    view.renderPossibleMoves(game.validMovesOfKey(at=position).toSeq)
    game.unlockedKey(position) match {
      case Some(key) =>
        (game.phase, key.team) match {
          case (GoldPlaying, Gold) | (SilverPlaying, Silver) =>
            val directionMarkers = key.facing.allOthers
              .map(dir => (dir.move(position), dir))
              .filter(pair => game.allLocations.contains(pair._1))
            this.rotations = Some(directionMarkers)
            view.renderPossibleRotations(directionMarkers.toSeq)
          case _ =>
        }
      case None =>
    }
    view.redraw()
  }

  override def releaseLocation(position: model.Position): Unit = {
    this.pressed match {
      case Some(p) =>
        if (game.validMovesOfKey(p).contains(position)) {
          game.phase match  {
            case GoldPlaying => println(game.move(Gold, p, position))
            case SilverPlaying => println(game.move(Silver, p, position))
            case _ =>
          }
        }
        else {
          this.rotations match {
            case Some(pairs) =>
              pairs.foreach(pair => {
                val pos = pair._1
                val dir = pair._2
                if (position == pos) {
                  game.phase match  {
                    case GoldPlaying => println(game.rotate(Gold, p, dir))
                    case SilverPlaying => println(game.rotate(Silver, p, dir))
                    case _ =>
                  }
                }
              })
            case None =>
          }
        }
      case None => /* Unreachable */
    }
    this.pressed = None
    this.rotations = None
    view.clearPossibleMoves()
    view.renderBoard(game.board)
    view.redraw()
  }
}
