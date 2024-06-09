type point = {
  x: int,
  y: int,
}

// 5.12 実習: 入れ子のflatMap
assert(Belt.Array.flatMap([1], x => Belt.Array.map([-2, 7], y => {x, y})) == [
    {x: 1, y: -2},
    {x: 1, y: 7},
  ])
