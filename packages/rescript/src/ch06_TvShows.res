type tvShow = {
  title: string,
  start: int,
  end: int,
}

let extractStartYear = (rawShow: string): option<int> => {
  let bracketIndex = String.indexOf(rawShow, "(")
  let dashIndex = String.indexOf(rawShow, "-")

  let yearStr = if bracketIndex != -1 && dashIndex > bracketIndex + 1 {
    Some(String.substring(rawShow, ~start=bracketIndex + 1, ~end=dashIndex))
  } else {
    None
  }
  yearStr->Belt.Option.flatMap(Belt.Int.fromString)
}

let extractTitle = (rawShow: string): option<string> => {
  let bracketIndex = String.indexOf(rawShow, "(")

  if bracketIndex != -1 {
    rawShow
    ->String.substring(~start=0, ~end=bracketIndex)
    ->String.trim
    ->(
      s =>
        if s == "" {
          None
        } else {
          Some(s)
        }
    )
  } else {
    None
  }
}

let extractEndYear = (rawShow: string): option<int> => {
  let bracketIndex = String.indexOf(rawShow, ")")
  let dashIndex = String.indexOf(rawShow, "-")
  let yearStr = if dashIndex != -1 && bracketIndex > dashIndex + 1 {
    Some(String.substring(rawShow, ~start=dashIndex + 1, ~end=bracketIndex))
  } else {
    None
  }
  yearStr->Belt.Option.flatMap(Belt.Int.fromString)
}

let extractSingleYear = (rawShow: string): option<int> => {
  let dashIndex = String.indexOf(rawShow, "-")
  let bracketOpenIndex = String.indexOf(rawShow, "(")
  let bracketCloseIndex = String.indexOf(rawShow, ")")
  let yearStr = if (
    dashIndex == -1 && bracketOpenIndex != -1 && bracketCloseIndex > bracketOpenIndex + 1
  ) {
    rawShow
    ->String.substring(~start=bracketOpenIndex + 1, ~end=bracketCloseIndex)
    ->Some
  } else {
    None
  }
  yearStr->Belt.Option.flatMap(Belt.Int.fromString)
}

let extractSingleYearOrYearEnd = (rawShow: string): option<int> => {
  extractSingleYear(rawShow)->Belt.Option.orElse(extractEndYear(rawShow))
}

let extractAnyYear = (rawShow: string): option<int> => {
  extractStartYear(rawShow)
  ->Belt.Option.orElse(extractEndYear(rawShow))
  ->Belt.Option.orElse(extractSingleYear(rawShow))
}

let extractSingleYearIfNameExists = (rawShow: string): option<int> => {
  extractTitle(rawShow)->Belt.Option.flatMap(_ => extractSingleYear(rawShow))
}

let extractAnyYearIfNameExists = (rawShow: string): option<int> => {
  extractTitle(rawShow)->Belt.Option.flatMap(_ => extractAnyYear(rawShow))
}

let addOrResign = (parsedShows: option<array<tvShow>>, newParsedShow: option<tvShow>): option<
  array<tvShow>,
> => {
  parsedShows->Belt.Option.flatMap(shows =>
    newParsedShow->Belt.Option.map(newShow => shows->Belt.Array.concat([newShow]))
  )
}

let parseShow = (rawShow: string): option<tvShow> => {
  let title = extractTitle(rawShow)
  let start = extractStartYear(rawShow)->Belt.Option.orElse(extractSingleYear(rawShow))
  let end = extractEndYear(rawShow)->Belt.Option.orElse(extractSingleYear(rawShow))
  title->Belt.Option.flatMap(title =>
    start->Belt.Option.flatMap(start => end->Belt.Option.map(end => {title, start, end}))
  )
}

// 6.19 実習: Optionを返す安全な関数
assert(extractStartYear("Title (2020-)") == Some(2020))
assert(extractStartYear("Title -(2020)") == None)

assert(parseShow("Breaking Bad (2008-2013)") ==
  Some({title: "Breaking Bad", start: 2008, end: 2013}))
