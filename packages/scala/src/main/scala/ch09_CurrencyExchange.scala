import cats.effect.IO
import cats.implicits._

import scala.jdk.CollectionConverters.MapHasAsScala

import ch09_CurrencyExchange.model._

object ch0913 {
  def extractSingleCurrencyRate(currentToExtract: Currency)(
      table: Map[Currency, BigDecimal]
  ): Option[BigDecimal] = {
    table.get(currentToExtract)
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
}
