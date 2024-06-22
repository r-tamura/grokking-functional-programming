object model72 {
  type User = String
  object User {
    def apply(name: String): User = name
  }
  type Artist = String
  object Artist {
    def apply(name: String): Artist = name
  }
}
import model72._

enum PlaylistMusicGenre {
  case HeavyMetal
  case Pop
  case HardRock
}

enum PlayListKind {
  case CuratedByUser(user: User)
  case ByArtist(artist: Artist)
  case ByGenres(genres: Set[MusicGenre])
}

case class Song(
    artist: String,
    name: String
)

case class PlayList(
    name: String,
    kind: PlayListKind,
    songs: List[Song]
)

def gatherSongs(
    playlists: List[PlayList],
    artist: Artist,
    genre: MusicGenre
): List[Song] = {
  playlists.foldLeft(List.empty[Song]) { (songs, playlist) =>
    val matchingSongs = playlist.kind match {
      case PlayListKind.ByArtist(playlistArtist) =>
        if playlistArtist == artist then playlist.songs
        else List.empty[Song]
      case PlayListKind.ByGenres(playlistGenres) =>
        if playlistGenres.contains(genre) then playlist.songs
        else List.empty[Song]
      case PlayListKind.CuratedByUser(_) =>
        playlist.songs.filter(_.artist == artist)
    }
    songs.appendedAll(matchingSongs)
  }
}

object ch07_Playlist extends App {
  // 7.32 コーヒーブレイク: 関数型データ設計
  val fooFighters = Artist("Foo Fighters")
  val playlists = List(
    PlayList(
      "This is Foo Fighters",
      PlayListKind.ByArtist(fooFighters),
      List(
        Song("Foo Fighters", "Breakout"),
        Song("Foo Fighters", "Learn to Fly")
      )
    ),
    PlayList(
      "Deep Focus",
      PlayListKind.ByGenres(Set(MusicGenre.HardRock)),
      List(
        Song("Draft Punk", "One More Time"),
        Song("Chemical Brothers", "Hey Boy Hey Girl")
      )
    )
  )
  assert(
    gatherSongs(
      playlists,
      fooFighters,
      MusicGenre.Pop
    ) == List(
      Song("Foo Fighters", "Breakout"),
      Song("Foo Fighters", "Learn to Fly")
    ),
    gatherSongs(
      playlists,
      fooFighters,
      MusicGenre.Pop
    )
  )
  val noSuchArtist = Artist("No Such Artist")
  assert(
    gatherSongs(
      playlists,
      noSuchArtist,
      MusicGenre.HardRock
    ) == List(
      Song("Draft Punk", "One More Time"),
      Song("Chemical Brothers", "Hey Boy Hey Girl")
    )
  )
}
