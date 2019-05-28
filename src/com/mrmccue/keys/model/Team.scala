package com.mrmccue.keys.model

sealed trait Team {
  /**
    * The team opposing the given team (gold for silver, silver for gold)
    * @return The team opposing another
    */
  def opposing: Team
}

object Team {
  case object Gold extends Team {
    override def opposing: Team = Silver
  }

  case object Silver extends Team {
    override def opposing: Team = Gold
  }
}
