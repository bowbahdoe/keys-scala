package com.mrmccue.keys

import com.mrmccue.keys.controller.KeysController
import com.mrmccue.keys.model._
import com.mrmccue.keys.view.KeysView

object Main {
  def main(args: Array[String]): Unit = {
    val game = KeysGame.default
    val view = KeysView.default
    val controller = new KeysController(game, view)

    controller.start()
    /*println(game.phase)
    println(game.move(Gold, Position(1, 0), Position(1, 6)))
    println(game.phase)
    println(game)


    println(game.move(Silver, Position(4, 7), Position(4, 4)))
    println(game.phase)
    println(game)

    println(game.rotate(Gold, Position(1, 6), SouthEast))
    println(game.phase)
    println(game)

    println(game.move(Silver, Position(4, 4), Position(4, 3)))
    println(game.phase)
    println(game)

    println(game.move(Gold, Position(1, 6), Position(2, 7)))
    println(game.phase)
    println(game)

    println(game.rotate(Silver, Position(6, 7), West))
    println(game.phase)
    println(game)


    println(game.rotate(Gold, Position(2, 7), East))
    println(game.phase)
    println(game)


    println(game.move(Silver, Position(6, 7), Position(2, 7)))
    println(game.phase)
    println(game)

    println(game.respawn(Silver, Position(6, 7)))
    println(game.phase)
    println(game)*/


  }
}
