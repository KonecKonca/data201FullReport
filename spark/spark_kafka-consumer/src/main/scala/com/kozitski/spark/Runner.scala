package com.kozitski.spark

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json

object Runner extends App {

  val spark = SparkSession.builder()
    .master("local")
    .appName("kafka reader")
    .getOrCreate()

  val df = spark.read
    .format("kafka")
    .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
    .option("subscribe", "twitter_2")
    .option("startingOffsets", "earliest")
    .option("endingOffsets", "latest")
    .load()


  import org.apache.spark.sql.functions._
  import spark.implicits._

//    df
//      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
//      .as[(String, String)]
//
//  println("..... Start reading elements")

//  df.collect().foreach(row => println(":::   " + row.toString()))

  val messageRDD: RDD[Message] =
    df.select(
      col("key").cast("string"),
      col("value").cast("string"),
      col("offset").cast("long"),
      col("timestamp").cast("long")
    )
      .as[Message]
      .rdd

  messageRDD.foreach(message =>  println(":::   " + message))

}

case class Message(key: String,
                   value: String,
                   offset: Long,
                   timestamp: Long)



