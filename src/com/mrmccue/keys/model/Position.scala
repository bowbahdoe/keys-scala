package com.mrmccue.keys.model

/**
  * A Position.
  * @param x The x-coord of the position.
  * @param y The y-coord of the position.
  */
final case class Position(x: Int, y: Int) {
  override def toString: String = s"Position { x: $x, y: $y }"
}