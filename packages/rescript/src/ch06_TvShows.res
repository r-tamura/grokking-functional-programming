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
    rawShow->String.substring(~start=0, ~end=bracketIndex)->String.trim->Some
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

let parseShow = (rawShow: string): option<tvShow> => {
  let title = extractTitle(rawShow)
  let start = extractStartYear(rawShow)
  let end = extractEndYear(rawShow)
  title->Belt.Option.flatMap(title =>
    start->Belt.Option.flatMap(start => end->Belt.Option.map(end => {title, start, end}))
  )
}

// 6.19 実習: Optionを返す安全な関数
assert(extractStartYear("Title (2020-)") == Some(2020))
assert(extractStartYear("Title -(2020)") == None)

assert(parseShow("Breaking Bad (2008-2013)") ==
  Some({title: "Breaking Bad", start: 2008, end: 2013}))
