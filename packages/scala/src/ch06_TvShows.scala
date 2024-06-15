case class TvShow(title: String, start: Int, end: Int);

def parseShow(rawShow: String): Option[TvShow] = {
  for {
    name <- extractName(rawShow)
    yearStart <- extractYearStart(rawShow)
    yearEnd <- extractYearEnd(rawShow)
  } yield TvShow(name, yearStart, yearEnd)
}

def extractYearStart(rawShow: String): Option[Int] = {
  val bracketIndex = rawShow.indexOf("(")
  val dash = rawShow.indexOf("-")

  val yearStr = if (bracketIndex != -1 && dash > bracketIndex + 1) {
    Some(
      rawShow.substring(bracketIndex + 1, dash)
    )
  } else None

  yearStr.flatMap(_.toIntOption)
}

def extractName(rawShow: String): Option[String] = {
  val bracketIndex = rawShow.indexOf("(")

  if (bracketIndex != -1) {
    Some(rawShow.substring(0, bracketIndex).trim())
  } else {
    None
  }
}

def extractYearEnd(rawShow: String): Option[Int] = {
  val dash = rawShow.indexOf("-")
  val bracketIndex = rawShow.indexOf(")")

  for {
    yearStr <-
      if (dash != -1 && bracketIndex > dash + 1) {
        Some(rawShow.substring(dash + 1, bracketIndex))
      } else None
    year <- yearStr.toIntOption
  } yield year
}

object ch06_TvShows extends App {
  // 6.19 実習: Optionを返す安全な関数
  assert(
    parseShow("Breaking Bad (2008-2013)") == Some(
      TvShow("Breaking Bad", 2008, 2013)
    ),
    parseShow("Breaking Bad (2008-2013)")
  )

  assert(
    parseShow("Breaking Bad) (2008-2013") == None,
    parseShow("Breaking Bad) (2008-2013")
  )
}
