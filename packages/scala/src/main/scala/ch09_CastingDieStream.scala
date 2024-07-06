import cats.effect.IO
import cats.implicits._
import cats.effect.unsafe.implicits.global

import fs2.{Pure, Stream}

object ch0940 {
  import ch08_CastingDieImpure.NoFailures.castTheDieImpure

  def castTheDie(): IO[Int] = IO.delay(castTheDieImpure())

  val dieCast: Stream[IO, Int] = Stream.eval(castTheDie())
  val oneDieCastProgram = dieCast.compile.toList

  val infiniteDieCasts: Stream[IO, Int] = dieCast.repeat
}

object ch09_CastingDieStream extends App {
  // 9.40 実習: ストリーム処理
  import ch0940._
  assert(
    infiniteDieCasts
      .filter(_ % 2 != 0)
      .take(3)
      .compile
      .toList
      .unsafeRunSync()
      .size == 3
  )
  assert {

    val actual = infiniteDieCasts
      .take(5)
      .map(v =>
        v match {
          case 6 => v * 2
          case _ => v
        }
      )
      .compile
      .toList
      .unsafeRunSync()

    !actual.contains(6) && actual.size == 5
  }

  assert {
    val actual =
      infiniteDieCasts.take(3).compile.toList.map(_.sum).unsafeRunSync()
    actual >= 3 && actual <= 18
  }

  assert {
    val actual = infiniteDieCasts
      .filter(_ == 5)
      .take(1)
      .append(infiniteDieCasts.take(2))
      .compile
      .toList
      .unsafeRunSync()
    actual.size == 3 && actual.head == 5
  }

  assert {
    val actual = infiniteDieCasts.take(100).compile.drain.unsafeRunSync()
    actual == ()
  }

  assert {
    val actual = infiniteDieCasts
      .take(3)
      .append(infiniteDieCasts.take(3).map(_ * 3))
      .compile
      .toList
      .unsafeRunSync()
    actual.size == 6
  }

  assert {
    val actual = infiniteDieCasts
      .scan(0)((sixesInRow, current) => {
        if (current == 6) sixesInRow + 1 else 0
      })
      .filter(_ == 2)
      .take(1)
      .compile
      .toList
      .unsafeRunSync()
    actual.size == 1 && actual.head == 2
  }
}
