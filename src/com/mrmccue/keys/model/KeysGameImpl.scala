package com.mrmccue.keys.model

import com.mrmccue.keys.model.Team.{Gold, Silver}

import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}

private[model] final class KeysGameImpl extends KeysGame {
  private var unlockedKeys: mutable.Map[Position, UnlockedKey] = initialUnlockedKeys
  private var lockedKeys: mutable.Map[Position, LockedKey] = mutable.Map()
  private var _phase: Phase = GoldPlaying

  /**
    * The starting configuration for the locked and unlocked keys.
    *
    * @return A mutable map of positions to keys representing all of the unlocked keys at the start of a game.
    */
  private def initialUnlockedKeys: mutable.Map[Position, UnlockedKey] = {
    val keys: mutable.Map[Position, UnlockedKey] = mutable.Map()
    for (position <- this.respawnPoints(Gold)) {
      keys.put(position, UnlockedKey.default(Gold))
    }
    for (position <- this.respawnPoints(Silver)) {
      keys.put(position, UnlockedKey.default(Silver))
    }
    keys
  }

  /**
    * Resets the game to it's initial conditions.
    */
  override def reset(): Unit = {
    this.unlockedKeys = initialUnlockedKeys
    this.lockedKeys = mutable.Map()
    this._phase = GoldPlaying
  }

  /**
    * Returns whether the given position is out of bounds for the board.
    *
    * @param position The position to check.
    * @return Whether the position is out of bounds or not.
    */
  private def isOutOfBounds(position: Position): Boolean = {
    position.x < 0 || position.x > 7 || position.y < 0 || position.y > 7
  }

  /**
    * Gets the places that a key at the given position can move to. If there is no unlocked
    * key at the given location then the set of positions will be empty
    *
    * @param at The location to look for a key.
    * @return A set of positions representing the places that it is valid to move to.
    */
  override def validMovesOfKey(at: Position): Set[Position] = {
    val moves: mutable.Set[Position] = mutable.Set()
    unlockedKeys.get(at) match {
      case Some(key) =>
        if (this.phase == GoldPlaying && key.team == Gold
          || this.phase == SilverPlaying && key.team == Silver) {
          var nextLoc = key.facing.move(at)

          breakable {
            while (true) {
              if (isOutOfBounds(nextLoc)) {
                break
              }
              else if (this.unlockedKeys.get(nextLoc).isEmpty) {
                moves += nextLoc
                nextLoc = key.facing.move(nextLoc)
              }
              else if (this.unlockedKeys.get(nextLoc).map(_.team).contains(key.team.opposing)) {
                moves += nextLoc
                break
              }
              else {
                break
              }
            }
          }
        }
      case None =>
    }


    moves.toSet
  }

  /**
    * If the game is over it means that some team has won, in which case this method
    * will return that team.
    * @return The winning team, if any.
    */
  private def winningTeam: Option[Team] = {
    val teamsLeftOnBoard: Set[Team] = this.unlockedKeys.map(_._2.team).toSet
    if (teamsLeftOnBoard.size == 1) {
      Some(teamsLeftOnBoard.toList.head)
    }
    else {
      None
    }
  }

  private def respawnPoints(team: Team): Set[Position] = {
    team match {
      case Gold =>
        Set(Position(1, 0), Position(3, 0), Position(5, 0))
      case Silver =>
        Set(Position(2, 7), Position(4, 7), Position(6, 7))
    }
  }

  override def move(asTeam: Team, from: Position, to: Position): MoveResult = {
    (asTeam, this.phase) match {
      case (Gold, GoldPlaying) | (Silver, SilverPlaying) =>
        val unlockedKeySrc = this.unlockedKeys.get(from)
        val lockedKeyDest = this.lockedKeys.get(to)
        val unlockedKeyDest = this.unlockedKeys.get(to)
        val teamPlaying = this.phase match {
          case GoldPlaying => Gold
          case SilverPlaying => Silver
          case _ => throw new AssertionError("Unreachable")
        }

        var shouldEnterRespawn = false

        if (unlockedKeySrc.isEmpty) {
          NoPieceAtSelection
        }
        else if (!unlockedKeySrc.map(_.team).contains(teamPlaying)) {
          TeamDoesNotMatch
        }
        else if (this.validMovesOfKey(from).contains(to)) {
          lockedKeyDest match {
            case Some(locked) =>
              if (teamPlaying == locked.team) {
                this.lockedKeys.remove(to) // need to enter respawn
                shouldEnterRespawn = true
              }
            case None =>
          }

          unlockedKeyDest match {
            case Some(unlockedDst) =>
              this.lockedKeys.put(to, unlockedDst.locked)
            case None =>
          }

          this.unlockedKeys.put(to, this.unlockedKeys(from))
          this.unlockedKeys.remove(from)

          this._phase = {
            if (shouldEnterRespawn) {
              teamPlaying match {
                case Gold => GoldRespawning
                case Silver => SilverRespawning
              }
            }
            else {
              this.winningTeam match {
                case Some(Gold) => GoldWin
                case Some(Silver) => SilverWin
                case None =>
                  teamPlaying match {
                    case Gold => SilverPlaying
                    case Silver => GoldPlaying
                  }
              }
            }
          }

          Success
        }
        else {
          InvalidMovement
        }
      case _ =>
        WrongGamePhase
    }
  }

  override def rotate(asTeam: Team, at: Position, toFace: Direction): RotateResult = {
    (asTeam, this.phase) match {
      case (Gold, GoldPlaying) | (Silver, SilverPlaying) =>
        this.unlockedKeys.get(at) match {
          case Some(unlockedKey) =>
            if (unlockedKey.team != asTeam) {
              TeamDoesNotMatch
            }
            else if (unlockedKey.facing == toFace) {
              AlreadyFacing
            }
            else {
              this.unlockedKeys.put(at, unlockedKey.copy(facing = toFace))
              this._phase = this.phase match {
                case GoldPlaying => SilverPlaying
                case SilverPlaying => GoldPlaying
                case _ => throw new AssertionError("Unreachable")
              }
              Success
            }
          case None =>
            NoPieceAtSelection
        }
      case _ =>
        WrongGamePhase
    }
  }

  override def respawn(asTeam: Team, at: Position): RespawnResult = {
    (asTeam, this.phase) match {
      case (Gold, GoldRespawning) | (Silver, SilverRespawning)  =>
        this.unlockedKeys.get(at) match {
          case Some(_) =>
            SpaceAlreadyOccupied
          case None =>
            if (this.respawnPoints(asTeam).contains(at)) {
              this.unlockedKeys.put(at, UnlockedKey.default(asTeam))
              this._phase = this.phase match {
                case GoldRespawning => SilverPlaying
                case SilverRespawning => GoldPlaying
                case _ => throw new AssertionError("Unreachable")
              }
              Success
            }
            else {
              InvalidRespawn
            }
        }
      case _ =>
        WrongGamePhase
    }
  }

  override def phase: Phase =
    this._phase

  override def allLocations: Set[Position] = {
    (for (x <- 0 to 8; y <- 0 to 8) yield {
      Position(x, y)
    }).toSet
  }

  override def toString: String = {
    val unlockedArr: Array[Array[String]] = Array(
      Array.ofDim(8),
      Array.ofDim(8),
      Array.ofDim(8),
      Array.ofDim(8),
      Array.ofDim(8),
      Array.ofDim(8),
      Array.ofDim(8),
      Array.ofDim(8)
    )


    val lockedArr = unlockedArr.map(_.clone())

    for (x <- 0 until 8; y <- 0 until 8) {
      unlockedArr(y)(x) = "__"
    }

    for (x <- 0 until 8; y <- 0 until 8) {
      lockedArr(y)(x) = "_"
    }

    for ((Position(x, y), key) <- this.unlockedKeys) {
      unlockedArr(y)(x) = (key.team match {
        case Gold => "G"
        case Silver => "S"
      }) + key.facing.toString
    }

    for ((Position(x, y), key) <- this.lockedKeys) {
      lockedArr(y)(x) = key.team match {
        case Gold => "G"
        case Silver => "S"
      }
    }

    val unlockedStr = unlockedArr.map(_.mkString(" ")).mkString("\n")
    val lockedStr = lockedArr.map(_.mkString("  ")).mkString("\n")
    s"Unlocked:\n$unlockedStr\n\nLocked:\n$lockedStr"
  }

  /**
    * Gets whatever unlocked key is at the given position.
    *
    * @param position The position to check.
    * @return An unlocked key.
    */
  override def unlockedKey(position: Position): Option[UnlockedKey] =
    this.unlockedKeys.get(position)

  /**
    * Get whatever locked key is at the given position.
    *
    * @param position The position to check.
    * @return A locked key.
    */
  override def lockedKey(position: Position): Option[LockedKey] =
    this.lockedKeys.get(position)

  /**
    * An immutable view of the state of the board.
    *
    * @return The state of the board.
    */
  override def board: Board = Board(unlocked =
    this.unlockedKeys.toMap, locked = this.lockedKeys.toMap)
}

