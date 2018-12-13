package com.keeganosala.lol.core
package util

import scala.concurrent.duration.{ Duration, FiniteDuration, MILLISECONDS }

object LolUtil {
 
  def parseFiniteDuration(str: String) : Option[FiniteDuration] = {
    try {
      Some(Duration(str)).collect { case d: FiniteDuration => d }
    } catch {
      case ex: NumberFormatException => None
    }
  }
}
