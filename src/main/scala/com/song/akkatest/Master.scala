package com.song.akkatest

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

class Master extends Actor{


  override def preStart(): Unit = {
    println("prestart invoked")
  }

  override def receive = {
    case "connect" => {
      println("a client connected")
      sender ! "reply"
    }

    case "hello" => {
      println("hello client")
    }

  }
}

object Master{

  def main(args: Array[String]): Unit = {

    val host = args(0)
    val port = args(1).toInt
    val configStr =
      s"""
        |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname = "$host"
        |akka.remote.netty.tcp.port = "$port"
      """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    val actorSystem = ActorSystem("MasterSystem",config)

    val master = actorSystem.actorOf(Props(new Master),"Master")

    master ! "hello"
    actorSystem.awaitTermination()
  }
}

