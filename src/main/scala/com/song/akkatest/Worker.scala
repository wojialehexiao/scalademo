package com.song.akkatest

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Worker extends Actor{

  var master : ActorSelection = _

  override def preStart(): Unit = {
    master = context.actorSelection("akka.tcp://MasterSystem@127.0.0.1:8888/user/Master")
    master ! "connect"

  }

  override def receive = {
    case "reply" => {
      println("reply message from Master")
    }
  }
}

object Worker{

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
    val actorSystem = ActorSystem("WorkerSystem",config)

    val worker = actorSystem.actorOf(Props(new Worker),"Worker")
    actorSystem.awaitTermination()
  }
}


