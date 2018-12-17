package com.keeganosala.lol.market
package computations

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.mutable.ListBuffer
import scala.math._

case class AlphavantageData(
  timedata : Map[String,Map[String,String]]
)

trait Computations {

	def calculateVolatility(data:AlphavantageData): Map[String,Double] = {

	  	var high = ListBuffer[Double]()
	  	var low = ListBuffer[Double]()
	  	var open = ListBuffer[Double]()
	  	var close = ListBuffer[Double]()
	  	var volume = ListBuffer[Double]()

		for ((timestap,data) <- data.timedata) {
			high += data("2. high").toFloat
			low += data("3. low").toFloat
			volume += data("5. volume").toFloat
			open += data("1. open").toFloat
			close += data("4. close").toFloat
		}

		Map[String,Double](
		"high" -> computeVolatilityAlgo(high.toList),
		"low" -> computeVolatilityAlgo(low.toList),
		"open" -> computeVolatilityAlgo(open.toList),
		"close" -> computeVolatilityAlgo(close.toList),
		"volume" -> computeVolatilityAlgo(volume.toList)
		)
	}

	def computeVolatilityAlgo(data:List[Double]):Double = {
		val mean = data.sum/data.length
		val squares = data map (x => pow((mean-x),2))
		pow(squares.sum/data.length,0.5)
	}

}