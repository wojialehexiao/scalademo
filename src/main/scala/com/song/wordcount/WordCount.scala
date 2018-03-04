package com.song.wordcount

import java.io.File

import scala.actors.{Actor, Future}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

class WordCount extends Actor{
  override def act(): Unit = {
    loop{
      react{
        case SubmitTask(fileName) => {
          val content = Source.fromFile(new File(fileName)).mkString
          val arr = content.split(" ")
          val result = arr.map((_,1)).groupBy(_._1).map(x=>(x._1,x._2.size))
          sender ! ResultTask(result)
        }
        case StopTask => {
          exit()
        }
      }
    }
  }
}

object WordCount {
  def main(args: Array[String]): Unit = {
    val files = Array("/home/song/dream","/home/song/dream2")
    val resultSet = new mutable.HashSet[Future[Any]]
    val resultList = new ListBuffer[ResultTask]

    for(f <- files){
      val actor = new WordCount
      actor.start()
      val reply = actor !! SubmitTask(f)
      resultSet += reply
    }

    while (resultSet.size > 0){
      for(reply <- resultSet.filter(_.isSet)){
          resultList += reply.apply().asInstanceOf[ResultTask]
          resultSet -= reply
      }
      Thread.sleep(500)
    }

    val finalResult = resultList.flatMap(_.result).groupBy(_._1).mapValues(x=>x.foldLeft(0)(_+_._2))
    println(finalResult)
  }

}

case class SubmitTask(fileName:String)
case class ResultTask(result:Map[String,Int])
case object StopTask