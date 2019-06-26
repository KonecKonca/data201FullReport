package com.kozitski.spark

import com.kozitski.spark.args.{ArgHandler, RunningArgument}
import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.service._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object AppRunner extends App{

  val runningArguments: RunningArgument = (new ArgHandler).handleArgs(args)

  val spark = SparkSession.builder()
    .master(runningArguments.sparkMaster)
    .appName(runningArguments.appName)
    .getOrCreate()

  val kafkaReader = new KafkaReader
  val kafkaMessageRdd: RDD[KafkaMessage] = kafkaReader.readAllFromKafka(spark, runningArguments)

  val jsonMapper = new KafkaToJsonMapper
  val twits: RDD[Twit] = jsonMapper.kafkaToArrayMap(kafkaMessageRdd)

  val grouper = new TwitsGrouper()
  val groupedTwits: RDD[(String, Int)] = grouper.groupByHashTag(twits)
  println(grouper.hashTagWithCountReport(groupedTwits))

  val hdfsSaver = new HdfsSaver
  hdfsSaver.saveTwitsToHdfsWithPartitioning(twits, spark, runningArguments.path)

  val hdfsReader = new HdfsReader
  val extractedRdd: RDD[Twit] = hdfsReader.readTwitsFromHdfs(spark, runningArguments.path)

  val hdfsWithKafkaMerger = HdfsWithKafkaMerger(runningArguments)
  val path = runningArguments.path + "/_2=" + runningArguments.year + "_" + runningArguments.day + "/_3=" + runningArguments.hour
  val mergedTwitsRdd: RDD[Twit] = hdfsWithKafkaMerger.merge(spark, path,
    runningArguments.year.toInt, runningArguments.day.toInt, runningArguments.hour.toInt)

  hdfsSaver.saveTwitsToHdfs(mergedTwitsRdd, spark, path + "/recalculate")

}


