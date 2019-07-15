package com.mrmccue.keys.model

sealed trait Phase

object Phase {
  case class Playing(team: Team) extends Phase with PrettyProduct

  case class Respawning(team: Team) extends Phase with PrettyProduct

  case class Win(team: Team) extends Phase with PrettyProduct
}