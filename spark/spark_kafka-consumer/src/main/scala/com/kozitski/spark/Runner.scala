package com.kozitski.spark

import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.service._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object Runner extends App{

  val spark = SparkSession.builder()
    .master("local")
    .appName("kafka twitter reader")
    .getOrCreate()

  val kafkaReader = new KafkaReader
  val kafkaMessageRdd: RDD[KafkaMessage] = kafkaReader.readAllFromKafka(spark)

  val jsonMapper = new KafkaToJsonMapper
  val twits: RDD[Twit] = jsonMapper.kafkaToArrayMap(kafkaMessageRdd)

  val grouper = new TwitsGrouper()
  val groupedTwits: RDD[(String, Int)] = grouper.groupByHashTag(twits)
  println(grouper.hashTagWithCountReport(groupedTwits))

  val hdfsSaver = new HdfsSaver
  hdfsSaver.saveTwitsToHdfsWithPartitioning(twits, spark, "/user/maria_dev/spark_advanced/14")

  val hdfsReader = new HdfsReader
  val extractedRdd: RDD[Twit] = hdfsReader.readTwitsFromHdfs(spark, "/user/maria_dev/spark_advanced/14")

  val hdfsWithKafkaMerger = new HdfsWithKafkaMerger
  val mergedTwitsRdd: RDD[Twit] = hdfsWithKafkaMerger.merge(spark, "/user/maria_dev/spark_advanced/14/_2=2019_176/_3=12", 2019, 176, 12)

  hdfsSaver.saveTwitsToHdfs(mergedTwitsRdd, spark, "/user/maria_dev/spark_advanced/14/_2=2019_176/_3=12/recalculate")

}

