package com.mrmccue.keys.model

import scala.collection.mutable

/**
  * A game of keys represented by some mutable board.
  */
trait KeysGame {
  /**
    * Moves the piece at the given location to a new location, locking any piece that it lands on.
    * @param asTeam The team that is making the move.
    * @param from The location where the piece that will be moved is on the board.
    * @param to The location where the player wants the piece to move.
    * @return The result of the move.
    */
  def move(asTeam: Team, from: Position, to: Position): MoveResult

  /**
    * Rotates the piece at the given location to face a new direction.
    * @param asTeam The team that is making the rotation.
    * @param at The location where the piece is on the board.
    * @param toFace The direction to face the key.
    * @return The result of the rotation.
    */
  def rotate(asTeam: Team, at: Position, toFace: Direction): RotateResult

  /**
    * Respawns at the given location.
    * @param asTeam The team that is respawning.
    * @param at The location at which to respawn.
    * @return The result of the respawn.
    */
  def respawn(asTeam: Team, at: Position): RespawnResult

  /**
    * The current phase of the game.
    * @return The phase which the game is currently in.
    */
  def phase: Phase

  /**
    * Resets the game to it's initial conditions.
    */
  def reset(): Unit

  /**
    * All the locations that are on the board for this game of keys.
    * @return A set of all the locations on the board.
    */
  def allLocations: Set[Position]


  /**
    * Gets whatever unlocked key is at the given position.
    * @param position The position to check.
    * @return An unlocked key.
    */
  def unlockedKey(position: Position): Option[UnlockedKey]

  /**
    * Get whatever locked key is at the given position.
    * @param position The position to check.
    * @return A locked key.
    */
  def lockedKey(position: Position): Option[LockedKey]

  /**
    * An immutable view of the state of the board.
    * @return The state of the board.
    */
  def board: Board

  /**
    * Gets the places that a key at the given position can move to. If there is no unlocked
    * key at the given location or it isn't the right phase for that key to move
    * then the set of positions will be empty.
    *
    * @param at The location to look for a key.
    * @return A set of positions representing the places that it is valid to move to.
    */
  def validMovesOfKey(at: Position): Set[Position]
}

object KeysGame {
  def default: KeysGame = new KeysGameImpl()
}
