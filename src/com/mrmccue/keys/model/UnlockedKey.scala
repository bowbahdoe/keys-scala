package com.mrmccue.keys.model

final case class UnlockedKey(team: Team, facing: Direction) extends PrettyProduct {
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
