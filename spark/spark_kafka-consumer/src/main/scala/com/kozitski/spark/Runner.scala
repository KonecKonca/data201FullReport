package com.kozitski.spark

import play.api.libs.json.Json
import com.kozitski.spark.domain.{KafkaMessage, Twit}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Runner extends App {

//  val str = "{\"created_at\": \"pedrorijo91\", \"id\": 1999050}"
  val str = "{created_at: 123, id: 1999050}"
  val jsonObject = Json.parse(str)

  val created = jsonObject \ "created_at"
  println("   : " + created.get)

  val twit = jsonObject.asInstanceOf[Twit]
  println(twit)

//  val spark = SparkSession.builder()
//    .master("local")
//    .appName("kafka reader")
//    .getOrCreate()
//
//  val df = spark.read
//    .format("kafka")
//    .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
//    .option("subscribe", "twitter_2")
//    .option("startingOffsets", "earliest")
//    .option("endingOffsets", "latest")
//    .load()
//
//
//  import org.apache.spark.sql.functions._
//  import spark.implicits._
//
//  val messageRDD: RDD[KafkaMessage] =
//    df.select(
//      col("key").cast("string"),
//      col("value").cast("string"),
//      col("offset").cast("long"),
//      col("timestamp").cast("long")
//    )
//      .as[KafkaMessage]
//      .rdd




//  messageRDD
//    .foreach(message =>  {
//      val jsonObject = Json.parse(message.value)
//
//      val text = jsonObject \ "text"
//
////      val twit = jsonObject.asInstanceOf[Twit]
//
//      println(":::   " + message.value.substring(10) + " ____ "  + text)
//    })

}

