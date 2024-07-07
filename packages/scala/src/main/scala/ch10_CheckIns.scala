import cats.effect.{IO, Ref}
import cats.implicits._
import cats.effect.unsafe.implicits.global
import fs2.Stream

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

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

  case class CityStats(
      city: City,
      checkIns: Int
  )

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

object ch10_CheckInsApp extends App {
  // コーヒーブレイク: 逐次的に考える
  import ch10_04._
  processCheckIns(
    Stream(
      City("Sydney"),
      City("Sydney"),
      City("Cape Town"),
      City("Singapore"),
      City("Cape Town"),
      City("Sydney")
    )
  ).unsafeRunSync()

  // 実習: 同時IO

}
