package com.mrmccue.keys.model

final case class Board(
  unlocked: Map[Position, UnlockedKey],
  locked: Map[Position, LockedKey]
) extends PrettyProduct
