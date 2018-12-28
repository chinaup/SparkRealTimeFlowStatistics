package com.zhangbin.project

import com.zhangbin.dao.{CategoryClickCountDAO, CategorySearchClickCountDAO}
import com.zhangbin.domain.{CategoryClickCount, CategorySearchClickCount, ClickLog}
import com.zhangbin.project.util.DataUtils
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer


/**
  * 使用 sparkStreaming 处理 Kafka 过来数据
  */

object StatStreamingApp {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)


    val conf = new SparkConf().setMaster("local[4]").setAppName("StatStreamingApp")//至少两个线程
    val ssc = new StreamingContext(conf, Seconds(5))

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "master:9092,slave1:9092,slave2:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "test",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = List("flumeTopic").toSet
    val logs = KafkaUtils.createDirectStream[String, String](
      ssc,
      PreferConsistent,
      Subscribe[String, String](topics, kafkaParams)
    ).map(_.value())


    //124.187.143.156    2018-12-25 10:19:14    "GET www/2 HTTP/1.0"    -    200
    //把数据进行清洗，并过滤掉无效数据
    val cleanlog = logs.map(line=>{
        val infos = line.split("\t")
        val url = infos(2).split(" ")(1)
        var categoryId = 0
        if(url.startsWith("www")){
          categoryId = url.split("/")(1).toInt
        }
        ClickLog(infos(0),DataUtils.parseToMin(infos(1)),categoryId,infos(3),infos(4).toInt)
    }).filter(log => log.categoryId != 0)

    cleanlog.print()

    //每个类别每天的点击量day_categoryId
    cleanlog.map(log=>{
       (log.time.substring(0,8)+"_"+log.categoryId,1)//把categoryId转为day_categoryId
    }).reduceByKey(_+_).foreachRDD(rdd=>{
       rdd.foreachPartition(partitions=>{
         val list = new ListBuffer[CategoryClickCount]()
         partitions.foreach(pair=>{
           list.append(CategoryClickCount(pair._1,pair._2))
         })
         CategoryClickCountDAO.save(list)//把数据保存到HBase上
       })
    })

    //每个栏目各个搜索引擎渠道过来的流量20171122_1(渠道)_2(类别)
    //156.29.132.124    2018-12-25 10:19:14    "GET www/4 HTTP/1.0"    https://www.sogou.com/web?qu=快乐人生    302
    cleanlog.map(log=>{
        val url = log.refer.replace("//","/")
        val splits = url.split("/")
        var host = ""
        if(splits.length > 2)
          host = splits(1)
        (host,log.time,log.categoryId)
    }).filter(x => x._1 != "").map(x => {
        (x._2.substring(0,8)+"_"+x._1+"_"+x._3,1)
    }).reduceByKey(_+_).foreachRDD(rdd=>{
      rdd.foreachPartition(partitions=>{
        val list = new ListBuffer[CategorySearchClickCount]()
        partitions.foreach(pair=>{
          list.append(CategorySearchClickCount(pair._1,pair._2))
        })
        CategorySearchClickCountDAO.save(list)//把数据保存到HBase上
      })
    })



    ssc.start();
    ssc.awaitTermination();
  }
}
