package com.mrmccue.keys.model

/**
  * A direction that an unlocked key might face.
  */
sealed trait Direction {
  /**
    * @return All of the directions that are not this direction.
    */
  final def allOthers: Set[Direction] = {
    val dirs: Set[Direction] = Set(
      Direction.North,
      Direction.South,
      Direction.East,
      Direction.West,
      Direction.NorthEast,
      Direction.NorthWest,
      Direction.SouthEast,
      Direction.SouthWest
    )
    dirs - this
  }

  /**
    * Moves a position in the direction by 1 "square"
    * @param position The position to move from.
    * @return A new position.
    */
  def move(position: Position): Position
}

object Direction {
  case object North extends Direction {
    override def toString: String = "↑"

    override def move(position: Position): Position =
      position.copy(y = position.y - 1)
  }

  case object South extends Direction {
    override def toString: String = "↓"

    override def move(position: Position): Position =
      position.copy(y = position.y + 1)
  }

  case object East extends Direction {
    override def toString: String = "→"

    override def move(position: Position): Position =
      position.copy(x = position.x + 1)
  }

  case object West extends Direction {
    override def toString: String = "←"

    override def move(position: Position): Position =
      position.copy(x = position.x - 1)
  }

  case object NorthEast extends Direction {
    override def toString: String = "↗"

    override def move(position: Position): Position =
      North.move(East.move(position))
  }

  case object NorthWest extends Direction {
    override def toString: String = "↖"

    override def move(position: Position): Position =
      North.move(West.move(position))
  }

  case object SouthEast extends Direction {
    override def toString: String = "↘"

    override def move(position: Position): Position =
      South.move(East.move(position))
  }

  case object SouthWest extends Direction {
    override def toString: String = "↙"

    override def move(position: Position): Position =
      South.move(West.move(position))
  }
}

