case class TvShow(title: String, start: Int, end: Int);

def parseShow(rawShow: String): Either[String, TvShow] = {
  for {
    name <- extractName(rawShow)
    yearStart <- extractYearStart(rawShow)
    yearEnd <- extractYearEnd(rawShow)
  } yield TvShow(name, yearStart, yearEnd)
}

def extractYearStart(rawShow: String): Either[String, Int] = {
  val bracketIndex = rawShow.indexOf("(")
  val dash = rawShow.indexOf("-")

  val yearStr = if (bracketIndex != -1 && dash > bracketIndex + 1) {
    Right(
      rawShow.substring(bracketIndex + 1, dash)
    )
  } else Left("")

  yearStr
    .flatMap(_.toIntOption.toRight(s"Can't parse year '$yearStr' as int"))
}

def extractName(rawShow: String): Either[String, String] = {
  val bracketIndex = rawShow.indexOf("(")

  if (bracketIndex != -1) {
    val name = rawShow.substring(0, bracketIndex).trim()
    if (name == "") {
      Left(s"Name is empty $rawShow")
    } else {
      return Right(name)
    }
  } else {
    Left(s"Can't extract name from $rawShow")
  }
}

def extractYearEnd(rawShow: String): Either[String, Int] = {
  val dash = rawShow.indexOf("-")
  val bracketIndex = rawShow.indexOf(")")

  for {
    yearStr <-
      if (dash != -1 && bracketIndex > dash + 1) {
        Right(rawShow.substring(dash + 1, bracketIndex))
      } else Left("")
    year <- yearStr.toIntOption.toRight((s"Can't parse year '$yearStr' as int"))
  } yield year
}

def extractSingleYear(rawShow: String): Either[String, Int] = {
  val bracketIndex = rawShow.indexOf("(")
  val bracketEndIndex = rawShow.indexOf(")")
  val dash = rawShow.indexOf("-")

  val yearStr =
    if (
      dash == -1 && bracketIndex != -1 && bracketEndIndex > bracketIndex + 1
    ) {
      Right(
        rawShow.substring(bracketIndex + 1, bracketEndIndex)
      )
    } else Left("Can't extract single year from $rawShow")

  yearStr.flatMap(_.toIntOption.toRight(s"Can't parse $yearStr as int"))
}

def extractSingleYearOrYearEnd(rawShow: String): Either[String, Int] = {
  extractSingleYear(rawShow).orElse(extractYearEnd(rawShow))
}

def extractAnyYear(rawShow: String): Either[String, Int] = {
  extractYearStart(rawShow)
    .orElse(extractYearEnd(rawShow))
    .orElse(extractSingleYear(rawShow))
}

def extractSingleYearIfNameExists(rawShow: String): Either[String, Int] = {
  extractName(rawShow).flatMap(_ => extractSingleYear(rawShow))
}

def addOrResign(
    parsedShows: Either[String, List[TvShow]],
    newParsedShow: Either[String, TvShow]
): Either[String, List[TvShow]] = {
  for {
    shows <- parsedShows
    newShow <- newParsedShow
  } yield shows :+ newShow
}

object ch06_TvShows extends App {
  // 6.19 実習: Optionを返す安全な関数
  assert(
    parseShow("Breaking Bad (2008-2013)") == Right(
      TvShow("Breaking Bad", 2008, 2013)
    ),
    parseShow("Breaking Bad (2008-2013)")
  )

  assert(
    parseShow("Breaking Bad) (2008-2013") == Left(""),
    parseShow("Breaking Bad) (2008-2013")
  )

  // 6.29 実習:  関数型のエラー処理
  assert(
    extractSingleYearOrYearEnd("Breaking Bad (2008)") == Right(2008),
    extractSingleYearOrYearEnd("Breaking Bad (2008)")
  )
  assert(
    extractSingleYearOrYearEnd("Breaking Bad (2008-2013)") == Right(2013),
    extractSingleYearOrYearEnd("Breaking Bad (2008-2013)")
  )

  assert(
    extractAnyYear("A (2008)") == Right(2008)
  )
  assert(
    extractAnyYear("A (2008-2013)") == Right(2008)
  )
  assert(
    extractAnyYear("A (-2020)") == Right(2020)
  )

  // 6.36 コーヒーブレイク: エラー処理の戦略
  assert(
    addOrResign(
      Right(List.empty),
      Right(TvShow("Chernobyl", 2019, 2019))
    ) == Right(List(TvShow("Chernobyl", 2019, 2019)))
  )
  assert(
    addOrResign(
      Right(List(TvShow("Chernobyl", 2019, 2019))),
      Right(TvShow("The Wire", 2002, 2008))
    ) == Right(
      List(
        TvShow("Chernobyl", 2019, 2019),
        TvShow("The Wire", 2002, 2008)
      )
    )
  )
  assert(
    addOrResign(
      Right(List(TvShow("Chernobyl", 2019, 2019))),
      Left("")
    ) == Left("")
  )
  assert(
    addOrResign(
      Left(""),
      Right(TvShow("Chernobyl", 2019, 2019))
    ) == Left("")
  )
  assert(addOrResign(Left(""), Left("")) == Left(""))
}
