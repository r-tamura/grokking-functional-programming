object TipCalculator {
  def getTipPercentage(names: List[String]) = {
    if (names.size > 5) {
      20
    } else if (names.size > 0) {
      10
    } else 0
  }
}

def main(args: Array[String]): Unit = {
  val names = List.empty[String]
  println(TipCalculator.getTipPercentage(names))

  val names2 = names.appendedAll(List("Alice", "Bob", "Charlie"));
  println(TipCalculator.getTipPercentage(names2))

  val names3 = names2.appendedAll(List("David", "Emily", "Frank"));
  println(TipCalculator.getTipPercentage(names3))
}
