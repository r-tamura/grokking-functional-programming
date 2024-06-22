enum MusicGenre {
  case HeavyMetal
  case Pop
  case HardRock
}

object model {
  opaque type Location = String
  object Location {
    def apply(value: String): Location = value
    extension (l: Location) def value: String = l
  }
}
import model._

enum YearsActive {
  case ActiveBetween(start: Int, end: Int)
  case StillActive(since: Int)
}
import YearsActive._

case class Artist(
    name: String,
    genre: MusicGenre,
    location: Location,
    yearsActive: YearsActive
)

def activeSearch(artist: Artist, currentYear: Int): Int = {
  artist.yearsActive match {
    case StillActive(since)        => currentYear - since
    case ActiveBetween(start, end) => end - start
  }
}

object ch07_Artist extends App {
  // 7.29 実習: パターンマッチング
  assert(
    activeSearch(
      Artist(
        "Metallica",
        MusicGenre.HeavyMetal,
        Location("U.S."),
        StillActive(1981)
      ),
      2022
    ) == 41
  )

  assert(
    activeSearch(
      Artist(
        "The Beatles",
        MusicGenre.Pop,
        Location("U.K."),
        ActiveBetween(1960, 1970)
      ),
      2022
    ) == 10
  )
}
