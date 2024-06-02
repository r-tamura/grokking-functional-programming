let abbreviate = (fullName: string) => {
  let initial = Js.String2.substring(~from=0, ~to_=1, fullName)
  let separatorIndex = Js.String2.indexOf(fullName, " ")
  let lastName = Js.String2.sliceToEnd(fullName, ~from=separatorIndex + 1)
  `${initial}. ${lastName}`
}

assert(abbreviate("Alonzo Church") === "A. Church")
assert(abbreviate("Haskell Curry") === "H. Curry")
assert(abbreviate("A Church") === "A. Church")
assert(abbreviate("A. Church") === "A. Church")
