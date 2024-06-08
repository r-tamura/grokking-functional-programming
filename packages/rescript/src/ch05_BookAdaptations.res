type book = {
  title: string,
  authors: array<string>,
}

let recommendationBooks = (friend: string): array<book> => {
  switch friend {
  | "Alice" => [
      {title: "FP in Scala", authors: ["Chiusano", "Bjarnason"]},
      {title: "Get Programming with Scala", authors: ["Sfregola"]},
    ]
  | "Bob" => [
      {
        title: "Harry Potter",
        authors: ["Rowling"],
      },
      {
        title: "The Lord of the Rings",
        authors: ["Tolkien"],
      },
    ]
  | _ => []
  }
}

let friends = ["Alice", "Bob", "Charlie"]
let recommendations = Belt.Array.flatMap(friends, recommendationBooks)
assert(recommendations == [
    {title: "FP in Scala", authors: ["Chiusano", "Bjarnason"]},
    {title: "Get Programming with Scala", authors: ["Sfregola"]},
    {title: "Harry Potter", authors: ["Rowling"]},
    {title: "The Lord of the Rings", authors: ["Tolkien"]},
  ])

let authors = friends->Belt.Array.flatMap(recommendationBooks)->Belt.Array.flatMap(r => r.authors)
assert(authors == ["Chiusano", "Bjarnason", "Sfregola", "Rowling", "Tolkien"])
