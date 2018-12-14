package com.keeganosala.lol.market
package computations

import java.util.concurrent.TimeUnit

import scala.concurrent.duration._ 
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Promise }
import scala.collection.mutable.ListBuffer
import scala.math._
import scala.util.{ Failure, Success }

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem }
import akka.pattern.ask
import akka.util.Timeout

import com.keeganosala._

import lol.core.config.LolConfig

import lol.market.gateway._

import DataAccessService._

case class AlphavantageData2(
  timedata : Map[String,Map[String,String]]
)

case class AlphavantageData(
  `Time Series (1min)`: Map[String,Map[String,String]]
)

trait Computations {

  implicit def system: ActorSystem

  val dataAccessService = system.actorOf(Props[DataAccessService], "dataAccessService")

  implicit val timedelta = Timeout(5.seconds)

  	var high = ListBuffer[Double]()
  	var low = ListBuffer[Double]()
  	var open = ListBuffer[Double]()
  	var close = ListBuffer[Double]()
  	var volume = ListBuffer[Double]()
  	var graph = Map[String,Map[String,String]]()

	def setValues(data:AlphavantageData2){

		for ((timestap,data) <- data.timedata) {
			
			high += data("2. high").toFloat
			low += data("3. low").toFloat
			volume += data("5. volume").toFloat
			open += data("1. open").toFloat
			close += data("4. close").toFloat
		}

	}

	def obtainValues(data:AlphavantageData2){
			for ((timestap,data) <- data.timedata) graph += (timestap -> data)
	}

	def obtainGraphData = {
		val latestData = (dataAccessService ? GetData).mapTo[AlphavantageData]
		latestData.onComplete{
	  		case Success(data) => obtainValues(
	  			new AlphavantageData2(timedata = data `Time Series (1min)`))
	  		case Failure(e) => 
	  			println(e)
		}
		graph
	}

  	def obtainMarketVolatility:Map[String,Double] = {
		val latestData = (dataAccessService ? GetData).mapTo[AlphavantageData]
		latestData.onComplete{
	  		case Success(data) => setValues(
	  			new AlphavantageData2(timedata = data `Time Series (1min)`))
	  		case Failure(e) => 
	  			println(e)
		}
		coMputeAndMerge
  	}

	////Calculate the variance///

	def computeData(data:List[Double]):Double = {
		val mean = data.sum/data.length
		val squares = data map (x => pow((mean-x),2))
		pow(squares.sum/data.length,0.5)
	}

	def coMputeAndMerge:Map[String,Double] = {
		Map[String,Double](
		"high" -> computeData(high.toList),
		"low" -> computeData(low.toList),
		"open" -> computeData(open.toList),
		"close" -> computeData(close.toList),
		"volume" -> computeData(volume.toList)
		)
	}
}