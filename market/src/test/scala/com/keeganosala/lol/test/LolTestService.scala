package com.keeganosala.lol.test

import akka.actor.ActorSystem

import akka.testkit.{ ImplicitSender, TestKit, TestProbe }

import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import com.keeganosala._

abstract class LolTestService extends TestKit(ActorSystem("MyTestSystem"))
    with ImplicitSender
    with WordSpecLike
    with Matchers
    with BeforeAndAfterAll
{

  override def beforeAll {
    Thread.sleep(1000)
  }

  override def afterAll {
    Thread.sleep(1000)
    TestKit.shutdownActorSystem(system)
  }
}
