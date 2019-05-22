package com.mrmccue.keys.model

case class Board(unlocked: Map[Position, UnlockedKey], locked: Map[Position, LockedKey]) {
  override def toString: String = {
    s"Board { unlocked: $unlocked, locked: $locked }"
  }
}
