package com.kozitski.streaming.service

import com.kozitski.streaming.args.RunningArgument
import com.kozitski.streaming.domain.{KafkaMessage, Twit}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Reviewer of loosed messages
  */
class LoosedMessagesReviewer extends Serializable{

  /**
    * Review specifying batch
    *
    * @param spark is [[SparkSession]]
    * @param runningArgument are command line args [[RunningArgument]]
    * @return [[RDD[Twit]]]
    */
  def reviewBatch(spark: SparkSession, runningArgument: RunningArgument): RDD[Twit] = {
    val offsetRdd = readBatchFromKafka(spark, runningArgument, runningArgument.kafkaStartOffsets, runningArgument.kafkaEndOffsets)

    val fullRdd = readBatchFromKafka(spark, runningArgument, "earliest", "latest")
    val withTimeRdd = reduceByTime(fullRdd, runningArgument)

    offsetRdd.union(withTimeRdd).distinct()
  }

  /**
    * Read batch form kafka topic
    *
    * @param spark is [[SparkSession]]
    * @param runningArgument are command line args [[RunningArgument]]
    * @param startOffset is [[Int]] start offset
    * @param endOffset is [[Int]] end offset
    * @return [[RDD[Twit]]]
    */
  private def readBatchFromKafka(spark: SparkSession, runningArgument: RunningArgument, startOffset: String, endOffset: String): RDD[Twit]= {
    val df = spark.read
      .format("kafka")
      .option("kafka.bootstrap.servers", runningArgument.hostName)
      .option("subscribe", runningArgument.kafkaReadTopic)
      .option("startingOffsets", startOffset)
      .option("endingOffsets", endOffset)
      .load()

    import org.apache.spark.sql.functions._
    import spark.implicits._

    val kafkaMessageRdd = df.select(
      col("key").cast("string"),
      col("value").cast("string"),
      col("offset").cast("long"),
      col("timestamp").cast("long")
    )
      .as[KafkaMessage]
      .rdd

    (new KafkaToJsonMapper).kafkaMessageMap(kafkaMessageRdd)
  }

  /**
    * Filter [[RDD]] by specifying time
    *
    * @param rdd is [[RDD]] of [[Twit]]
    * @param runningArgument are command line args [[RunningArgument]]
    * @return [[RDD[Twit]]]
    */
  def reduceByTime(rdd: RDD[Twit], runningArgument: RunningArgument): RDD[Twit]=
    rdd.filter(twit => twit.createdAt > runningArgument.revisionStartTime && twit.createdAt < runningArgument.revisionEndTime)

}
