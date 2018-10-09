package com.keeganosala.Lol
package data

import scala.util.{Failure,Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable.ListBuffer
import scala.math._

import java.util.concurrent.TimeUnit

import akka.actor.{ ActorRef, Actor, ActorLogging, Props, ActorSystem}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import akka.pattern.ask

import data.DataAccessActor._


trait Computations extends AutoMarshalling {

  implicit def system: ActorSystem

  implicit def materializer: ActorMaterializer

  def dataAccessActor: ActorRef

  implicit lazy val timedelta = Timeout(5.seconds)

  	var high = ListBuffer[Double]()
  	var low = ListBuffer[Double]()
  	var open = ListBuffer[Double]()
  	var close = ListBuffer[Double]()
  	var volume = ListBuffer[Double]()
  	var graph = Map[String,Map[String,String]]()
	// graph += ("graph" -> Map[String,String]())

	def setValues(data:AlphavantageData){

		val timeSeries = data `Time Series (1min)`

		for ((timestap,data) <- timeSeries) {
			
			high += data("2. high").toFloat
			low += data("3. low").toFloat
			volume += data("5. volume").toFloat
			open += data("1. open").toFloat
			close += data("4. close").toFloat
		}

	}

	def obtainValues(data:AlphavantageData){

		val maPP = data `Time Series (1min)`

		for ((timestap,data) <- maPP) graph += (timestap -> data)
	}

	def obtainGraphData = {
		val latestData = (dataAccessActor ? GetData).mapTo[AlphavantageData]
		latestData.onComplete{
	  		case Success(data) => obtainValues(data)
	  		case Failure(e) => 
	  			println(e)
		}
		graph
	}

  	def obtainMarketVolatility:Map[String,Double] = {
		val latestData = (dataAccessActor ? GetData).mapTo[AlphavantageData]
		latestData.onComplete{
	  		case Success(data) => setValues(data)
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



