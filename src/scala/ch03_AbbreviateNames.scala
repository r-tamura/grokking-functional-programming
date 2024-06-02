def abbreviate(fullName: String): String = {
  assert(fullName.indexOf(" ") != -1)
  val names = fullName.split(" ", 2)
  val firstName = names(0)
  val lastName = names(1)
  s"${firstName.charAt(0)}. $lastName"
}

def main(args: Array[String]): Unit = {
  assert(
    abbreviate("Alonzo Church") == "A. Church",
    abbreviate("Alonzo Church")
  )
  assert(
    abbreviate("A Church") == "A. Church",
    abbreviate("A Church")
  )
  assert(
    abbreviate("A. Church") == "A. Church",
    abbreviate("A. Church")
  )
}
