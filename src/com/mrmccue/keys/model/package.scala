package com.mrmccue.keys

package object model {

  sealed trait Phase {
    def teamPlaying: Option[Team]
  }


  sealed trait PlayPhase extends Phase
  sealed trait RespawnPhase extends Phase
  sealed trait WinPhase extends Phase

  case object GoldPlaying extends PlayPhase {
    override def teamPlaying: Option[Team] = Some(Team.Gold)
  }

  case object SilverPlaying extends PlayPhase {
    override def teamPlaying: Option[Team] = Some(Team.Silver)
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
        case Team.Gold => UnlockedKey(team=Team.Gold, facing=Direction.South)
        case Team.Silver => UnlockedKey(team=Team.Silver, facing=Direction.North)
      }
    }
  }


}
