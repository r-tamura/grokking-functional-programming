case class Book(title: String, authors: List[String])

def recommendedBooks(friend: String): List[Book] = {
  val scala = List(
    Book("FP in Scala", List("Chiusano", "Bjarnason")),
    Book("Get Programming with Scala", List("Sfregola"))
  )
  val fiction = List(
    Book("Harry Potter", List("Rowling")),
    Book("The Lord ot the Rings", List("Tolkien"))
  )

  friend match {
    case "Alice" => scala
    case "Bob"   => fiction
    case _       => List.empty
  }
}

object ch05_BookAdaptations extends App {
  // 5.7 コーヒーブレイク: リストのリストを処理する
  val friends = List("Alice", "Bob", "Charlie")
  val recommendations = friends.flatMap(recommendedBooks)
  assert(
    recommendations ==
      List(
        Book("FP in Scala", List("Chiusano", "Bjarnason")),
        Book("Get Programming with Scala", List("Sfregola")),
        Book("Harry Potter", List("Rowling")),
        Book("The Lord ot the Rings", List("Tolkien"))
      ),
    recommendations
  )

  val authors = friends.flatMap(recommendedBooks).flatMap(_.authors)
  assert(
    authors == List("Chiusano", "Bjarnason", "Sfregola", "Rowling", "Tolkien"),
    authors
  )
}
