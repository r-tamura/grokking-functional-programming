import cats.effect.IO
import cats.implicits._

import scala.jdk.CollectionConverters.MapHasAsScala

object ch09_CurrencyExchange {
  object model {
    opaque type Currency = String
    object Currency {
      def apply(name: String): Currency = name
      extension (currency: Currency) def name: String = currency
    }
  }
  import model._
  object ch09_CurrencyExchange {

    /** PREREQUISITE: model
      */

    def retry[A](ioa: IO[A], retries: Int): IO[A] =
      List.range(0, retries).map(_ => ioa).foldLeft(ioa) {
        (program, retryAction) =>
          program.orElse(retryAction)
      }

    /** PREREQUISITE: Impure, unsafe and side-effectful API call
      *
      * See [[ch09_CurrencyExchangeImpure.exchangeRatesTableApiCall]]
      *
      * We wrap them here to be able to use Scala immutable collections and
      * BigDecimal.
      */
    def exchangeRatesTableApiCall(currency: String): Map[String, BigDecimal] = {
      ch09_CurrencyExchangeImpure
        .exchangeRatesTableApiCall(currency)
        .asScala
        .view
        .mapValues(BigDecimal(_))
        .toMap
    }
  }
}
import ch09_CurrencyExchange._
