package com.mrmccue.keys

import com.mrmccue.keys.model._

package object model {
  /**
    * A direction that an unlocked key might face.
    */
  sealed trait Direction {
    /**
      * @return All of the directions that are not this direction.
      */
    def allOthers: Set[Direction] = {
      val dirs: Set[Direction] = Set(
        North,
        South,
        East,
        West,
        NorthEast,
        NorthWest,
        SouthEast,
        SouthWest
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

  /**
    * A Position.
    * @param x The x-coord of the position.
    * @param y The y-coord of the position.
    */
  final case class Position(x: Int, y: Int) {
    override def toString: String = s"Position { x: $x, y: $y }"
  }

  sealed trait Team {
    /**
      * The team opposing the given team (gold for silver, silver for gold)
      * @return The team opposing another
      */
    def opposing: Team
  }

  case object Gold extends Team {
    override def opposing: Team = Silver
  }

  case object Silver extends Team {
    override def opposing: Team = Gold
  }

  sealed trait Phase {
    def teamPlaying: Option[Team]
  }


  sealed trait PlayPhase extends Phase
  sealed trait RespawnPhase extends Phase
  sealed trait WinPhase extends Phase

  case object GoldPlaying extends PlayPhase {
    override def teamPlaying: Option[Team] = Some(Gold)
  }

  case object SilverPlaying extends PlayPhase {
    override def teamPlaying: Option[Team] = Some(Silver)
  }

  case object GoldRespawning extends RespawnPhase {
    override def teamPlaying: Option[Team] = None
  }

  case object SilverRespawning extends RespawnPhase {
    override def teamPlaying: Option[Team] = None
  }

  case object GoldWin extends WinPhase {
    override def teamPlaying: Option[Team] = None
  }

  case object SilverWin extends WinPhase {
    override def teamPlaying: Option[Team] = None
  }

  /**
    * The result of trying to move a key from one position to another.
    */
  sealed trait MoveResult

  /**
    * The result of trying to rotate a key to face a new direction.
    */
  sealed trait RotateResult

  /**
    * The result of trying to respawn in a given location.
    */
  sealed trait RespawnResult

  /**
    * Signifies that the action went through correctly and whatever needed to change in the game state has been changed.
    */
  case object Success extends MoveResult with RotateResult with RespawnResult

  /**
    * The location that the user was trying to move to was not a valid location to move to.
    */
  case object InvalidMovement extends MoveResult

  /**
    * The key was already facing the direction that it was being asked to face.
    */
  case object AlreadyFacing extends RotateResult

  /**
    * There was no piece at the place the user was trying to manipulate.
    */
  case object NoPieceAtSelection extends MoveResult with RotateResult

  /**
    * The team that the user is trying to manipulate does not match the user's team.
    */
  case object TeamDoesNotMatch extends MoveResult with RotateResult

  /**
    * It is the wrong game phase to conduct the requested action.
    */
  case object WrongGamePhase extends MoveResult with RotateResult with RespawnResult

  /**
    * The selected position is not valid to use for respawns.
    */
  case object InvalidRespawn extends RespawnResult

  /**
    * The selected position already has a piece in it.
    */
  case object SpaceAlreadyOccupied extends RespawnResult

  final case class LockedKey(team: Team) {
    override def toString: String = s"LockedKey { team: $team }"
  }

  final case class UnlockedKey(team: Team, facing: Direction) {
    override def toString: String = s"UnlockedKey { team: $team, facing: $facing }"

    def locked: LockedKey = LockedKey(team)
  }

  object UnlockedKey {
    private[model] def default(team: Team): UnlockedKey = {
      team match {
        case Gold => UnlockedKey(team=Gold, facing=South)
        case Silver => UnlockedKey(team=Silver, facing=North)
      }
    }
  }


}
