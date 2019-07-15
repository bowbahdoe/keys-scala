package com.mrmccue.keys.model

private[model] trait PrettyProduct extends Product {
  override def toString: String = {
    this
      .productElementNames
      .zip(this.productIterator)
      .map { case (name, value) => s"$name=$value" }
      .mkString(this.productPrefix + "(", ", ", ")")
  }
}
