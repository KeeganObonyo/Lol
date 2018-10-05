package com.keeganosala.Lol
package data

import de.heikoseeberger.akkahttpjson4s._
import org.json4s.{DefaultFormats, jackson }

trait AutoMarshalling extends Json4sSupport {
	implicit val serialization = jackson.Serialization
	implicit val formats = DefaultFormats
}