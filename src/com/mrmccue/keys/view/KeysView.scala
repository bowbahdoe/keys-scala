package com.mrmccue.keys.view

import com.mrmccue.keys.model.{Board, Direction, Position}

trait KeysView {
  def addListener(listener: ViewActionListener): Unit

  def renderBoard(board: Board): Unit

  def renderRespawnPoints(positions: Seq[Position]): Unit

  def renderPossibleMoves(positions: Seq[Position]): Unit

  def clearPossibleMoves(): Unit

  def renderPossibleRotations(rotations: Seq[(Position, Direction)]): Unit

  def clearPossibleRotations(): Unit

  def redraw(): Unit

  def start(): Unit
}

object KeysView {
  def default: KeysView = new SwingKeysView
}
