package com.keeganosala.lol.market
package gateway.marshalling

private[gateway] object AlphavantageMarshalling extends LolJsonProtocol {

  case class AlphavantageData(
    `Time Series (1min)`: Map[String,Map[String,String]]
  )
}
