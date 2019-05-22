package com.mrmccue.keys.view

import com.mrmccue.keys.model.Position

trait ViewActionListener {
  def pressLocation(position: Position): Unit
  def releaseLocation(position: Position): Unit
}
