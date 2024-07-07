import cats.effect.{IO, Ref}
import cats.implicits._
import cats.effect.unsafe.implicits.global
import fs2.Stream

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._
import ch10_04.topCities

object ch10_CheckIns {

  /** PREREQUISITE: model
    */
  object model {
    opaque type City = String

    object City {
      def apply(name: String): City = name
      extension (city: City) def name: String = city
    }

    case class CityStats(city: City, checkIns: Int)
  }
  import model._

  /** PREREQUISITE: a stream of user check-ins
    */
  val checkIns: Stream[IO, City] =
    Stream(
      City("Sydney"),
      City("Dublin"),
      City("Cape Town"),
      City("Lima"),
      City("Singapore")
    )
      .repeatN(100_000)
      .append(Stream.range(0, 100_000).map(i => City(s"City $i")))
      .append(Stream(City("Sydney"), City("Sydney"), City("Lima")))
      .covary[IO]

  private def showCheckIns = {
    assert {
      val allCheckIns = checkIns.map(_.name).compile.toList.unsafeRunSync()
      allCheckIns.size == 600_003 && allCheckIns.count(
        _ == "Sydney"
      ) == 100_002 && allCheckIns
        .count(_ == "Lima") == 100_001 && allCheckIns.count(
        _ == "Cape Town"
      ) == 100_000 && allCheckIns
        .count(_ == "City 27") == 1
    }
  }
}
import ch10_CheckIns._
import ch10_CheckIns.model.City

object ch10_04 {

  import ch10_CheckIns.model._

  def topCities(cityCheckIns: Map[City, Int]): List[CityStats] = {
    cityCheckIns.toList
      .map(_ match {
        case (city, checkIns) => CityStats(city, checkIns)
      })
      .sortBy(_.checkIns)
      .reverse
      .take(3)
  }

  def processCheckIns(checkIns: Stream[IO, City]): IO[Unit] = {
    checkIns
      .scan(Map[City, Int]())((map, city) => {
        map.updatedWith(city)(v =>
          v match {
            case None    => Some(1)
            case Some(v) => Some(v + 1)
          }
        )
      })
      .map(topCities)
      .foreach(IO.println)
      .compile
      .drain
  }
}

object ch10_21 {

  import ch10_CheckIns.model._

  def storeCheckIn(storedCheckIns: Ref[IO, Map[City, Int]])(
      city: City
  ): IO[Unit] = {
    storedCheckIns.update(
      _.updatedWith(city)(optionCount =>
        optionCount.map(count => count + 1).orElse(Some(1))
      )
    )
  }

  def updateRanking(
      storedCheckIns: Ref[IO, Map[City, Int]],
      storedRanking: Ref[IO, List[CityStats]]
  ): IO[Nothing] = {
    (for {
      newRanking <- storedCheckIns.get.map(topCities)
      _ <- storedRanking.set(newRanking)
    } yield ()).foreverM
  }

  def output(
      storedRanking: Ref[IO, List[CityStats]]
  )(v: Unit): IO[Unit] = {
    storedRanking.get.flatMap(IO.println)
  }

  def processCheckIns(checkIns: Stream[IO, City]): IO[Unit] = {
    for {
      storedCheckIns <- Ref.of[IO, Map[City, Int]](Map())
      storedRanking <- Ref.of[IO, List[CityStats]](List.empty)
      rankingProgram = updateRanking(storedCheckIns, storedRanking)
      checkInsProgram = checkIns
        .evalMap(storeCheckIn(storedCheckIns))
        .compile
        .drain
      outputProgram = IO
        .sleep(1.second)
        .flatMap(_ => storedCheckIns.get)
        .flatMap(IO.println)
        .foreverM
      _ <- List(rankingProgram, checkInsProgram, outputProgram).parSequence
    } yield IO.pure(())
  }
}

object ch10_CheckInsApp extends App {
  val sixCheckIns = Stream(
    City("Sydney"),
    City("Sydney"),
    City("Cape Town"),
    City("Singapore"),
    City("Cape Town"),
    City("Sydney")
  )

  // コーヒーブレイク: 逐次的に考える
  import ch10_04._
  processCheckIns(sixCheckIns).unsafeRunSync()

  // 10.21 コーヒーブレイク: 並行的に考える
  ch10_21
    .processCheckIns(
      Stream(
        City("Sydney"),
        City("Sydney"),
        City("Cape Town"),
        City("Singapore"),
        City("Cape Town"),
        City("Sydney")
      )
    )
    .unsafeRunSync()

}
