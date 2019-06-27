package com.kozitski.spark

import com.kozitski.spark.args.{ArgHandler, RunningArgument}
import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.service.{HdfsReader, HdfsSaver, HdfsWithKafkaMerger, KafkaReader, KafkaToJsonMapper, TwitsGrouper}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * ApplicationRunner starts application
  *
  */
object AppRunner extends App{

  /**
    * Delegate handling of command line arguments to [[ArgHandler]]
    */
  val runningArguments: RunningArgument = (new ArgHandler).handleArgs(args)


  /**
    * Creating [[SparkSession]]
    */
  val spark = SparkSession.builder()
    .master(runningArguments.sparkMaster)
    .appName(runningArguments.appName)
    .getOrCreate()

  /**
    * Read data from kafka by [[KafkaReader]]
    */
  val kafkaReader = new KafkaReader
  val kafkaMessageRdd: RDD[KafkaMessage] = kafkaReader.readAllFromKafka(spark, runningArguments)

  /**
    * Map kafka response RDD to RDD of twits by [[KafkaToJsonMapper]]
    */
  val jsonMapper = new KafkaToJsonMapper
  val twits: RDD[Twit] = jsonMapper.kafkaMessageMap(kafkaMessageRdd)

  /**
    * Group responsed RDD by [[TwitsGrouper]] and received as result
    * number of [[Twit]] by every hashTag
    */
  val grouper = new TwitsGrouper()
  val groupedTwits: RDD[(String, Int)] = grouper.groupByHashTag(twits)
  println(grouper.hashTagWithCountReport(groupedTwits))

  /**
    * Save received from kafka twits to hdfs
    */
  val hdfsSaver = new HdfsSaver
  hdfsSaver.saveTwitsToHdfsWithPartitioning(twits, spark, runningArguments.path)

  /**
    * Read twits saved into hdfs
    */
  val hdfsReader = new HdfsReader
  val extractedRdd: RDD[Twit] = hdfsReader.readTwitsFromHdfs(spark, runningArguments.path)

  /**
    * Merge twits from hdfs with new twits from kafka
    */
  val hdfsWithKafkaMerger = HdfsWithKafkaMerger(runningArguments)
  val path = runningArguments.path + "/_2=" + runningArguments.year + "_" + runningArguments.day + "/_3=" + runningArguments.hour
  val mergedTwitsRdd: RDD[Twit] = hdfsWithKafkaMerger.merge(spark, path,
    runningArguments.year.toInt, runningArguments.day.toInt, runningArguments.hour.toInt)

  /**
    * Save merged result into hdfs
    */
  hdfsSaver.saveTwitsToHdfs(mergedTwitsRdd, spark, path + "/recalculate")

}
