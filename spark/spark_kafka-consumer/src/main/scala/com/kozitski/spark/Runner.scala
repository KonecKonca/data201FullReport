package com.kozitski.spark

import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.service.{HdfsSaver, JsonMapper, TwitsGrouper}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

object Runner extends App{

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

  val twits: RDD[Twit] = (new JsonMapper).kafkaToArrayMap(twitRDD)

  val grouper = new TwitsGrouper()
  val groupedTwits: RDD[(String, Int)] = grouper.groupByHashTag(twits)
  println(grouper.hashTagWithCountReport(groupedTwits))

//  val savedRdd: RDD[(String, String, Int)] = (new HdfsSaver).transformToSave(twits)
//  savedRdd
//    .toDS()
//    .write
//    .mode(SaveMode.Overwrite)
//    .partitionBy("_2", "_3")
////    .format("json")
//    .saveAsTable("/user/maria_dev/spark_advanced/6")
////    .json("/user/maria_dev/spark_advanced/5")  // /result.csv
//
//  val value: String = spark.read.json("/user/maria_dev/spark_advanced/6").rdd.first().get(0).toString
////  println("---------------------------------------------------")
////  println(value.substring(0, 50))
////  println("---------------------------------------------------")

}

