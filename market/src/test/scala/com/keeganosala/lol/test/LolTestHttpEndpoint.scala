package com.keeganosala.lol.test

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal

import akka.stream.ActorMaterializer

trait LolTestHttpService extends LolTestService {

  final implicit val materializer = ActorMaterializer()

  def getStringHttpResponse(req: HttpRequest) = {
    getStringHttpResponseImpl(
      uri  = req.uri,
      data = Await.result(
        Unmarshal(req.entity).to[String],
        1.second
      ))
  }

  def getStringHttpResponseImpl(
    data: String,
    uri: Uri
  ): HttpResponse

}

