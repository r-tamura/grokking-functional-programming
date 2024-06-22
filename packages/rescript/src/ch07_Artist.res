type musicGenre = HeavyMetal | Pop | Hardrock
type location = Location(string)
type yearsActive =
  | StillActive(int)
  | ActiveBetween(int, int)

type artist = {
  name: string,
  genre: musicGenre,
  origin: location,
  yearsActive: yearsActive,
}

let activeSearch = (artist: artist, currentYear: int): int => {
  switch artist.yearsActive {
  | StillActive(year) => currentYear - year
  | ActiveBetween(start, end) => end - start
  }
}
