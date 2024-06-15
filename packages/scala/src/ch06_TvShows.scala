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
    val name = rawShow.substring(0, bracketIndex).trim()
    if (name == "") {
      None
    } else {
      return Some(name)
    }
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

def extractSingleYear(rawShow: String): Option[Int] = {
  val bracketIndex = rawShow.indexOf("(")
  val bracketEndIndex = rawShow.indexOf(")")
  val dash = rawShow.indexOf("-")

  val yearStr =
    if (
      dash == -1 && bracketIndex != -1 && bracketEndIndex > bracketIndex + 1
    ) {
      Some(
        rawShow.substring(bracketIndex + 1, bracketEndIndex)
      )
    } else None

  yearStr.flatMap(_.toIntOption)
}

def extractSingleYearOrYearEnd(rawShow: String): Option[Int] = {
  extractSingleYear(rawShow).orElse(extractYearEnd(rawShow))
}

def extractAnyYear(rawShow: String): Option[Int] = {
  extractYearStart(rawShow)
    .orElse(extractYearEnd(rawShow))
    .orElse(extractSingleYear(rawShow))
}

def extractSingleYearIfNameExists(rawShow: String): Option[Int] = {
  extractName(rawShow).flatMap(_ => extractSingleYear(rawShow))
}

def addOrResign(
    parsedShows: Option[List[TvShow]],
    newParsedShow: Option[TvShow]
): Option[List[TvShow]] = {
  for {
    shows <- parsedShows
    newShow <- newParsedShow
  } yield shows :+ newShow
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

  // 6.29 実習:  関数型のエラー処理
  assert(
    extractSingleYearOrYearEnd("Breaking Bad (2008)") == Some(2008),
    extractSingleYearOrYearEnd("Breaking Bad (2008)")
  )
  assert(
    extractSingleYearOrYearEnd("Breaking Bad (2008-2013)") == Some(2013),
    extractSingleYearOrYearEnd("Breaking Bad (2008-2013)")
  )

  assert(
    extractAnyYear("A (2008)") == Some(2008)
  )
  assert(
    extractAnyYear("A (2008-2013)") == Some(2008)
  )
  assert(
    extractAnyYear("A (-2020)") == Some(2020)
  )

  // 6.36 コーヒーブレイク: エラー処理の戦略
  assert(
    addOrResign(
      Some(List.empty),
      Some(TvShow("Chernobyl", 2019, 2019))
    ) == Some(List(TvShow("Chernobyl", 2019, 2019)))
  )
  assert(
    addOrResign(
      Some(List(TvShow("Chernobyl", 2019, 2019))),
      Some(TvShow("The Wire", 2002, 2008))
    ) == Some(
      List(
        TvShow("Chernobyl", 2019, 2019),
        TvShow("The Wire", 2002, 2008)
      )
    )
  )
  assert(
    addOrResign(
      Some(List(TvShow("Chernobyl", 2019, 2019))),
      None
    ) == None
  )
  assert(
    addOrResign(
      None,
      Some(TvShow("Chernobyl", 2019, 2019))
    ) == None
  )
  assert(addOrResign(None, None) == None)
}
