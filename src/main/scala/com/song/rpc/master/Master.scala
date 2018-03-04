package com.song.rpc.master

import akka.actor.{Actor, ActorSystem, Props}
import com.song.akkatest.Worker
import com.song.rpc.caseclass._
import com.song.rpc.worker.Worker
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class Master extends Actor{

  var workers:Map[String,WorkerInfo] = _

  val CHECK_INTERVAL = 3000

  override def preStart(): Unit = {
    workers = Map();

    context.system.scheduler.schedule(0 millis,CHECK_INTERVAL millis,self,CheckHeartBeat)
  }

  override def receive = {
    case RegisterWorker(id,memery,cores)=>{
      workers += (id -> new WorkerInfo(id,memery,cores,System.currentTimeMillis()));
      println(workers)
      sender ! SuccessInfo()
    }
    case HeartBeatInfo(workId) => {
      var workerInfo = workers.getOrElse(workId,null)
      workerInfo.lastHearBitTime = System.currentTimeMillis();
      println(s"${workId}发送了一次心跳")
    }
    case CheckHeartBeat => {
      for((id,workerInfo) <- workers){
        val time = System.currentTimeMillis() - workerInfo.lastHearBitTime
        if(time > CHECK_INTERVAL * 3){
          workers -= (id)
        }
      }
      println("进行了一次检查，当前Worker有：" + workers)
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

    val worker = actorSystem.actorOf(Props(new Master),"Master")
    actorSystem.awaitTermination()
  }
}
