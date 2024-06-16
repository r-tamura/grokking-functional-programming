type tvShow = {
  title: string,
  start: int,
  end: int,
}

let toRight = (o: option<'a>, ~error: 'b): result<'a, 'b> =>
  switch o {
  | Some(v) => Ok(v)
  | None => Error(error)
  }

let orElse = (r: result<'a, 'b>, fallback: result<'a, 'b>): result<'a, 'b> =>
  switch r {
  | Ok(_) => r
  | Error(_) => fallback
  }

let parseInt = (s: string): result<int, string> =>
  Belt.Int.fromString(s)->toRight(~error="Can't parse ${s} as int")

let extractStartYear = (rawShow: string): result<int, string> => {
  let bracketIndex = String.indexOf(rawShow, "(")
  let dashIndex = String.indexOf(rawShow, "-")

  let yearStr = if bracketIndex != -1 && dashIndex > bracketIndex + 1 {
    Ok(String.substring(rawShow, ~start=bracketIndex + 1, ~end=dashIndex))
  } else {
    Error(`Can't extract start year from ${rawShow}`)
  }
  yearStr->Belt.Result.flatMap(y => Belt.Int.fromString(y)->toRight(~error="Can't parse year"))
}

let extractTitle = (rawShow: string): result<string, string> => {
  let bracketIndex = String.indexOf(rawShow, "(")

  if bracketIndex != -1 {
    rawShow
    ->String.substring(~start=0, ~end=bracketIndex)
    ->String.trim
    ->(
      s =>
        if s == "" {
          Error(`Title is empty in ${rawShow}`)
        } else {
          Ok(s)
        }
    )
  } else {
    Error(`Can't extract title from ${rawShow}`)
  }
}

let extractEndYear = (rawShow: string): result<int, string> => {
  let bracketIndex = String.indexOf(rawShow, ")")
  let dashIndex = String.indexOf(rawShow, "-")
  let yearStr = if dashIndex != -1 && bracketIndex > dashIndex + 1 {
    Ok(String.substring(rawShow, ~start=dashIndex + 1, ~end=bracketIndex))
  } else {
    Error(`Can't extract end year from ${rawShow}`)
  }
  yearStr->Belt.Result.flatMap(v => Belt.Int.fromString(v)->toRight(~error="Can't parse year"))
}

let extractSingleYear = (rawShow: string): result<int, string> => {
  let dashIndex = String.indexOf(rawShow, "-")
  let bracketOpenIndex = String.indexOf(rawShow, "(")
  let bracketCloseIndex = String.indexOf(rawShow, ")")
  let yearStr = if (
    dashIndex == -1 && bracketOpenIndex != -1 && bracketCloseIndex > bracketOpenIndex + 1
  ) {
    rawShow
    ->String.substring(~start=bracketOpenIndex + 1, ~end=bracketCloseIndex)
    ->Ok
  } else {
    Error(`Can't extract single year from ${rawShow}`)
  }
  yearStr->Belt.Result.flatMap(parseInt)
}

let extractSingleYearOrYearEnd = (rawShow: string): result<int, string> => {
  extractSingleYear(rawShow)->orElse(extractEndYear(rawShow))
}

let extractAnyYear = (rawShow: string): result<int, string> => {
  extractStartYear(rawShow)
  ->orElse(extractEndYear(rawShow))
  ->orElse(extractSingleYear(rawShow))
}

let extractSingleYearIfNameExists = (rawShow: string): result<int, string> => {
  extractTitle(rawShow)->Belt.Result.flatMap(_ => extractSingleYear(rawShow))
}

let extractAnyYearIfNameExists = (rawShow: string): result<int, string> => {
  extractTitle(rawShow)->Belt.Result.flatMap(_ => extractAnyYear(rawShow))
}

let addOrResign = (
  parsedShows: result<array<tvShow>, string>,
  newParsedShow: result<tvShow, string>,
): result<array<tvShow>, string> => {
  parsedShows->Belt.Result.flatMap(shows =>
    newParsedShow->Belt.Result.map(newShow => shows->Belt.Array.concat([newShow]))
  )
}

let parseShow = (rawShow: string): result<tvShow, string> => {
  let title = extractTitle(rawShow)
  let start = extractStartYear(rawShow)->orElse(extractSingleYear(rawShow))
  let end = extractEndYear(rawShow)->orElse(extractSingleYear(rawShow))
  title->Belt.Result.flatMap(title =>
    start->Belt.Result.flatMap(start => end->Belt.Result.map(end => {title, start, end}))
  )
}

// 6.19 実習: Optionを返す安全な関数
assert(extractStartYear("Title (2020-)") == Ok(2020))
assert(extractStartYear("Title -(2020)") == Error("Can't extract start year from Title -(2020)"))

assert(parseShow("Breaking Bad (2008-2013)") == Ok({title: "Breaking Bad", start: 2008, end: 2013}))
