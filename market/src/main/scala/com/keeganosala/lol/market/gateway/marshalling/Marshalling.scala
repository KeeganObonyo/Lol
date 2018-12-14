package com.keeganosala.lol.market
package gateway.marshalling

import com.keeganosala.lol.core.util.LolJsonProtocol

private[gateway] trait AlphavantageMarshalling extends LolJsonProtocol {

  case class AlphavantageData(
    `Time Series (1min)`: Map[String,Map[String,String]]
  )
}
