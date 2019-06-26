package com.kozitski.spark

import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.service.{HdfsSaver, JsonMapper, TwitsGrouper}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SaveMode, SparkSession}

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

  private val jsonMapper = new JsonMapper
  val twits: RDD[Twit] = jsonMapper.kafkaToArrayMap(twitRDD)

  val grouper = new TwitsGrouper()
  val groupedTwits: RDD[(String, Int)] = grouper.groupByHashTag(twits)
  println(grouper.hashTagWithCountReport(groupedTwits))

  private val hdfsSaver = new HdfsSaver
  val savedRdd: RDD[(String, String, Int)] = hdfsSaver.transformToSave(twits)
  savedRdd
    .toDS()
    .write
    .mode(SaveMode.Overwrite)
    .partitionBy("_2", "_3")
    .format("csv")
    .save("/user/maria_dev/spark_advanced/10")

  val checkRdd: RDD[String] = spark
    .read
    .text("/user/maria_dev/spark_advanced/10")
    .select(col("value").cast("String"))
    .as[String]
    .rdd
  val extractedRdd: RDD[Twit] = hdfsSaver.extractFromHdfsToTwit(checkRdd)


  spark.read
    .format("kafka")
    .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
    .option("subscribe", "twitter_3")
    .option("startingOffsets", "latest")
    .option("endingOffsets", "latest")
    .load()
    .rdd.foreach(println)

}

//val checkRdd: RDD[(String, String, Int)] = spark
//.read
//.text("/user/maria_dev/spark_advanced/8")
//.select(col("value").cast("String"))
//.as[(String, String, Int)]
//.rdd
//val resultRdd: RDD[Twit] = jsonMapper.toArrayMap(checkRdd.map(elem => elem._1))
