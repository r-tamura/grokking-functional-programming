import cats.effect.unsafe.{IORuntime, IORuntimeConfig, Scheduler}
import cats.effect.{IO, Ref}
import cats.implicits._

import java.util.concurrent.{Executors, ScheduledThreadPoolExecutor}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object ch10_CastingDieConcurrently extends App {
  import ch08_CastingDieImpure.NoFailures.castTheDieImpure

  def castDie(): IO[Int] = IO.delay(castTheDieImpure())

  private def unsafeRunTimedIO[A](
      io: IO[A]
  )(runtime: IORuntime = IORuntime.global): A = {
    val start = System.currentTimeMillis()
    val result = io.unsafeRunSync()(runtime)
    val end = System.currentTimeMillis()
    println(s"$result (took ${end - start}ms)")
    result
  }

  // 10.17 実習: 同時IO
  unsafeRunTimedIO(for {
    _ <- IO.sleep(1.second)
    result <- List(castDie(), castDie()).sequence
  } yield result.sum)

  unsafeRunTimedIO(for {
    l <- Ref.of[IO, List[Int]](List.empty)
    program1 = castDie().flatMap(v => l.update(_.appended(v)))
    program2 = castDie().flatMap(v => l.update(_.appended(v)))
    result <- List(program1, program2).parSequence
  } yield result)

  unsafeRunTimedIO(for {
    l <- Ref.of[IO, List[Int]](List.empty)
    result <- List
      .range(0, 3)
      .map(_ => castDie().flatMap(v => l.update(_.appended(v))))
      .parSequence
  } yield result)

  unsafeRunTimedIO(for {
    storedTotal <- Ref.of[IO, Int](0)
    singleCast = castDie().flatMap(v =>
      storedTotal.update(total =>
        if (v == 6) { total + v }
        else { total }
      )
    )
    result <- List
      .range(0, 100)
      .map(_ => singleCast)
      .parSequence
  } yield result)

  val singleCast = IO.sleep(1.second).flatMap(_ => castDie())
  unsafeRunTimedIO(for {
    result <- List
      .range(0, 100)
      .map(_ => singleCast)
      .parSequence
  } yield result.sum)
}
