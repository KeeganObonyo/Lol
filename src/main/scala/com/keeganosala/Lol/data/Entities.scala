package com.keeganosala.Lol
package data


case class AlphavantageData(
  `Meta Data`: Map[String,String],
  `Time Series (1min)`: Map[String,Map[String,String]]
)

case class MetaData (
  info: String,
  symbol:String,
  lastrefresh:String,
  interval:String,
  outputsize:String,
  timezone:String
)
case class TimeSeries1Min(
	open: String,
	high: String,
	low:String,
	close:String,
	volume:String
)

case class TimeData(
	timedata:List[Map[String,TimeSeries1Min]]
)
