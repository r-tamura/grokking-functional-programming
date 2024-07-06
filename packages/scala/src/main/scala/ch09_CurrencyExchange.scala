import cats.effect.IO
import cats.implicits._

import scala.jdk.CollectionConverters.MapHasAsScala

import ch09_CurrencyExchange.model._
import ch09_CurrencyExchange.ch09_CurrencyExchange.exchangeRatesTableApiCall

object ch0913 {
  def extractSingleCurrencyRate(currentToExtract: Currency)(
      table: Map[Currency, BigDecimal]
  ): Option[BigDecimal] = {
    table.get(currentToExtract)
  }
}

object ch0923 {

  def exchangeTable(from: Currency): IO[Map[Currency, BigDecimal]] = {
    IO.delay(exchangeRatesTableApiCall(from.name))
      .map(
        _.map(kv =>
          kv match {
            case (currency, rate) => (Currency(currency), rate)
          }
        )
      )
  }

  def retry[A](action: IO[A], maxRetries: Int): IO[A] = {
    List.range(0, maxRetries).map(_ => action).foldLeft(action) {
      (program, retryAction) =>
        program.orElse(retryAction)
    }
  }

  def currencyRate(from: Currency, to: Currency): IO[BigDecimal] = {
    retry(
      (for {
        table <- exchangeTable(from)
        rate <- ch0913.extractSingleCurrencyRate(to)(table) match {
          case Some(rate) => IO.pure(rate)
          case None       => currencyRate(from, to)
        }
      } yield rate),
      10
    )
  }
}

object ch09_CurrencyExchangeApp extends App {
  // 9.13 コーヒーブレイク: マップとタプルを操作する
  import ch0913._

  val usdExchangeTables = List(
    Map(Currency("EUR") -> BigDecimal(0.89)),
    Map(
      Currency("EUR") -> BigDecimal(0.89),
      Currency("JPY") -> BigDecimal(114.62)
    ),
    Map(Currency("JPY") -> BigDecimal(114))
  )

  assert(
    usdExchangeTables.map(extractSingleCurrencyRate(Currency("EUR"))) == List(
      Some(BigDecimal(0.89)),
      Some(BigDecimal(0.89)),
      None
    )
  )

  // 9.23 コーヒーブレイク: 再帰と無限
  import ch0923._
  val actual = currencyRate(Currency("USD"), Currency("EUR")).unsafeRunSync()
  assert(
    actual > 0.8 && actual < 0.9,
    actual
  )
}
