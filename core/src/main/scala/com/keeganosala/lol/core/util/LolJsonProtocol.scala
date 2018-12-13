package com.keeganosala.lol.core
package util

import de.heikoseeberger.akkahttpjson4s._
import org.json4s.{DefaultFormats, jackson }

trait LolJsonProtocol extends Json4sSupport {
	implicit val serialization = jackson.Serialization
	implicit val formats = DefaultFormats
}