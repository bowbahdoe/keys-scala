package com.mrmccue.keys.model

final case class LockedKey(team: Team) {
  override def toString: String = s"LockedKey { team: $team }"
}
