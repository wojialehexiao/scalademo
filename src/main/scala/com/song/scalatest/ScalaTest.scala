package com.song.scalatest

import scala.actors.Actor


class ScalaTest {


}

class ActorTest() extends Actor{
  override def act(): Unit = {
    loop{
      react{
        case HelloMsg(id,name) => {
          println(s"$id,hello $name")
          Thread.sleep(3000)
          sender ! ReplyMsg(100,"master")
        }

        case "exit" => {
          println("exit...")
          Thread.sleep(1000)
          exit()
        }
        case _ => {
          println("error")
        }
      }
    }
  }
}

object ScalaTest{
  def main(args: Array[String]): Unit = {
    val actor = new ActorTest
    actor.start()
    val reply = actor !! HelloMsg(1,"song")
    val reply2 = actor !? HelloMsg(2,"wang")

    println(reply.isSet)
//    println(reply2.isSet)
//    println(reply.apply())
    println(reply)
    Thread.sleep(50)
    println("----------------------------")
//    println(reply.isSet)
    println(reply2)
    actor ! "exit"
    //println(reply.apply())
  }
}

case class HelloMsg(id:Int, name:String)
case class ReplyMsg(id:Int, name:String)