package com.keeganosala.lol.market
package gateway.marshalling

import de.heikoseeberger.akkahttpjson4s._
import org.json4s.{DefaultFormats, jackson }

private[gateway] trait AlphavantageMarshalling extends Json4sSupport {

	implicit val serialization = jackson.Serialization
	implicit val formats = DefaultFormats
}
