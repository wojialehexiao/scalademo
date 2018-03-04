package com.song.rpc.worker

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.song.akkatest.Worker
import com.song.rpc.caseclass.{HeartBeatInfo, RegisterWorker, SendHeartBeat, SuccessInfo}
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.duration._

class Worker(memery:Int,cores:Int) extends Actor{

  val id:String = UUID.randomUUID().toString;
  var master : ActorSelection = _

  override def preStart(): Unit = {
    master = context.actorSelection(s"akka.tcp://MasterSystem@127.0.0.1:8888/user/Master")
    master ! RegisterWorker(id,memery,cores)
  }

  override def receive = {
    case SuccessInfo() => {
      println(s"${id}成功链接Master")
      context.system.scheduler.schedule(0 millis,3000 millis,self,SendHeartBeat)
    }
    case SendHeartBeat =>{
      master ! HeartBeatInfo(id)
    }
  }
}


object Worker{
  def main(args: Array[String]): Unit = {
    val host = args(0)
    val port = args(1).toInt
    val memery = args(2).toInt
    val cores = args(3).toInt

    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
      """.stripMargin
    val config = ConfigFactory.parseString(configStr)
    val actorSystem = ActorSystem("WorkerSystem",config)

    val worker = actorSystem.actorOf(Props(new Worker(memery,cores)),"Worker")
    actorSystem.awaitTermination()
  }
}