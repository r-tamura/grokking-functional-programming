case class Event(name: String, start: Int, end: Int)

def validateName(name: String): Option[String] = {
  if (name.size > 0) Some(name) else None
}

def validateEnd(end: Int): Option[Int] = {
  if (end < 3000) Some(end) else None
}

def validateStart(start: Int, end: Int): Option[Int] = {
  if (start < end) Some(start) else None
}

def validateLength(start: Int, end: Int, minLength: Int): Option[Int] = ???

def parseLongEvent(
    name: String,
    start: Int,
    end: Int,
    minLength: Int
): Option[Event] = {
  if (end - start >= 10) Some(Event(name, start, end)) else None
}

object ch05_Events extends App {
  // 5.34 コーヒーブレイク: Optionによる解析
  assert(
    parseLongEvent("Apollo Prgrams", 1961, 1972, 10) == Some(
      Event("Apollo Prgrams", 1961, 1972)
    )
  )
  assert(parseLongEvent("WorldWar II", 1939, 1945, 10) == None)
  assert(parseLongEvent("", 1939, 1945, 3) == None)
  assert(parseLongEvent("Apollo Program", 1972, 1961, 10) == None)
}
