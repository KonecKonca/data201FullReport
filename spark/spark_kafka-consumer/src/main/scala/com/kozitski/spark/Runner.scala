package com.kozitski.spark

import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.service.JsonMapper
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Runner extends App {

  val spark = SparkSession.builder()
    .master("local")
    .appName("kafka reader")
    .getOrCreate()

  val df = spark.read
    .format("kafka")
    .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
    .option("subscribe", "twitter_3")
    .option("startingOffsets", "earliest")
    .option("endingOffsets", "latest")
    .load()

  import org.apache.spark.sql.functions._
  import spark.implicits._

  val twitRDD: RDD[KafkaMessage] =
    df.select(
      col("key").cast("string"),
      col("value").cast("string"),
      col("offset").cast("long"),
      col("timestamp").cast("long")
    )
      .as[KafkaMessage]
      .rdd

  val twits: Array[Twit] = (new JsonMapper).mapToArray(twitRDD)


}

