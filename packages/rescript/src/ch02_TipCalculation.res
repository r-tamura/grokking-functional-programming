module TipCalculation = {
  let getTipPercentage = (names: array<string>): int => {
    switch names->Array.length {
    | l if l > 5 => 20
    | l if l > 0 => 10
    | _ => 0
    }
  }
}

Console.log(TipCalculation.getTipPercentage([]))

Console.log(TipCalculation.getTipPercentage(["Alice", "Bob", "Charlie"]))

Console.log(TipCalculation.getTipPercentage(["Alice", "Bob", "Charlie", "David", "Emily", "Frank"]))
