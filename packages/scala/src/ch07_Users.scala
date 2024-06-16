case class User(
    name: String,
    city: Option[String],
    favoriteArtists: List[String]
)

object ch07_Users extends App {
  // 7.18 コーヒーブレイク: forall, exists, contains
  val users = List(
    User("Alice", Some("Melbourne"), List("Bee Gees")),
    User("Bob", Some("Lagos"), List("Bee Gees")),
    User("Eve", Some("Tokyo"), List.empty),
    User("Mallory", None, List("Metallica", "Bee Gees")),
    User("Trent", Some("Buenos Aires"), List("Led Zeppelin"))
  )

  // f1
  assert(
    users.filter(_.city.forall(_ == "Melbourne")) == List(
      User("Alice", Some("Melbourne"), List("Bee Gees")),
      User("Mallory", None, List("Metallica", "Bee Gees"))
    )
  )

  // f2
  assert(
    users.filter(_.city.exists(_ == "Lagos")) == List(
      User("Bob", Some("Lagos"), List("Bee Gees"))
    )
  )

  // f3
  assert(
    users.filter(_.favoriteArtists.contains("Bee Gees")) == List(
      User("Alice", Some("Melbourne"), List("Bee Gees")),
      User("Bob", Some("Lagos"), List("Bee Gees")),
      User("Mallory", None, List("Metallica", "Bee Gees"))
    )
  )

  // f4
  assert(
    users.filter(_.city.exists(_.startsWith("T"))) == List(
      User("Eve", Some("Tokyo"), List.empty)
    )
  )

  // f5
  assert(
    users.filter(
      _.favoriteArtists.map(_.length).forall(_ > 8)
    ) == List(
      User("Eve", Some("Tokyo"), List.empty),
      User("Trent", Some("Buenos Aires"), List("Led Zeppelin"))
    )
  )

  // f6
  assert(
    users.filter(_.favoriteArtists.exists(_.startsWith("M"))) == List(
      User("Mallory", None, List("Metallica", "Bee Gees"))
    )
  )

}
