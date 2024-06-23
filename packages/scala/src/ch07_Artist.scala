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
    yearsActiveList: List[YearsActive]
)

def activeYears(artist: Artist, currentYear: Int): Int = {
  artist.yearsActiveList.foldLeft(0)((total, range) =>
    val subtotal = range match {
      case StillActive(since)        => currentYear - since
      case ActiveBetween(start, end) => end - start
    }
    total + subtotal
  )
}

enum SearchCondition {
  case SearchByGenre(genres: List[MusicGenre])
  case SearchByOrigin(locations: List[Location])
  case SearchByActiveYears(start: Int, end: Int)
  case SearchByTotalYears(years: Int, currentYear: Int)
}

case class DateRange(start: Int, end: Int)

def wasArtistActive(artist: Artist, range: DateRange): Boolean = {
  artist.yearsActiveList.exists(artistRange =>
    artistRange match {
      case ActiveBetween(start, end) =>
        start >= range.start && end <= range.end
      case StillActive(since) =>
        since >= range.start
    }
  )
}

def searchArtists(
    artists: List[Artist],
    requiredConditions: List[SearchCondition]
): List[Artist] = {
  artists.filter(artist => {
    requiredConditions.forall(condition => {
      condition match {
        case SearchCondition.SearchByGenre(genres) =>
          genres.contains(artist.genre)
        case SearchCondition.SearchByOrigin(locations) =>
          locations.contains(artist.location)
        case SearchCondition.SearchByActiveYears(start, end) =>
          wasArtistActive(artist, DateRange(start, end))
        case SearchCondition.SearchByTotalYears(years, currentYear) =>
          activeYears(artist, currentYear) >= years
      }
    })
  })
}

object ch07_Artist extends App {
  // 7.29 実習: パターンマッチング
  assert(
    activeYears(
      Artist(
        "Metallica",
        MusicGenre.HeavyMetal,
        Location("U.S."),
        List(StillActive(1981))
      ),
      2022
    ) == 41
  )

  assert(
    activeYears(
      Artist(
        "The Beatles",
        MusicGenre.Pop,
        Location("U.K."),
        List(ActiveBetween(1960, 1970))
      ),
      2022
    ) == 10
  )

  // 7.37 コーヒーブレイク: 設計と保守性
  val metallica = Artist(
    "Metallica",
    MusicGenre.HeavyMetal,
    Location("U.S."),
    List(StillActive(1981))
  )
  val beatles = Artist(
    "The Beatles",
    MusicGenre.Pop,
    Location("U.K."),
    List(ActiveBetween(1960, 1970))
  )
  val beeGees = Artist(
    "Bee Gees",
    MusicGenre.Pop,
    Location("U.K."),
    List(ActiveBetween(1958, 2003), ActiveBetween(2009, 2012))
  )
  val artists = List(
    metallica,
    beatles,
    beeGees
  )

  assert(
    searchArtists(
      artists,
      List(
        SearchCondition.SearchByGenre(List(MusicGenre.Pop)),
        SearchCondition.SearchByOrigin(List(Location("U.K."))),
        SearchCondition.SearchByActiveYears(1960, 1970)
      )
    ) == List(
      beatles
    )
  )

  assert(
    searchArtists(
      artists,
      List(
        SearchCondition.SearchByActiveYears(2008, 2013)
      )
    ) == List(beeGees)
  )

  assert(
    searchArtists(
      artists,
      List(
        SearchCondition.SearchByTotalYears(40, 2022)
      )
    ) == List(metallica, beeGees)
  )
}
