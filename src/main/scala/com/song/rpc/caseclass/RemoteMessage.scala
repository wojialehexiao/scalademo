package com.song.rpc.caseclass

trait RemoteMessage extends Serializable

case class RegisterWorker(id:String,memery:Int,cores:Int) extends RemoteMessage
case class SuccessInfo() extends RemoteMessage

case object SendHeartBeat
case class HeartBeatInfo(id:String) extends RemoteMessage

case object CheckHeartBeat