package com.mrmccue.keys.model

sealed trait Phase {
  def teamPlaying: Option[Team]
}

object Phase {
  case object GoldPlaying extends Phase {
    override def teamPlaying: Option[Team] = Some(Team.Gold)
  }

  case object SilverPlaying extends Phase {
    override def teamPlaying: Option[Team] = Some(Team.Silver)
  }

  case object GoldRespawning extends Phase {
    override def teamPlaying: Option[Team] = None
  }

  case object SilverRespawning extends Phase {
    override def teamPlaying: Option[Team] = None
  }

  case object GoldWin extends Phase {
    override def teamPlaying: Option[Team] = None
  }

  case object SilverWin extends Phase {
    override def teamPlaying: Option[Team] = None
  }
}