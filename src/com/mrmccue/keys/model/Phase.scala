package com.mrmccue.keys.model

sealed trait Phase

object Phase {
  case class Playing(team: Team) extends Phase {
    override def toString: String = s"Playing { team: $team }"
  }

  case class Respawning(team: Team) extends Phase {
    override def toString: String = s"Respawning { team: $team }"
  }

  case class Win(team: Team) extends Phase {
    override def toString: String = s"Win { team: $team }"
  }
}