package com.kozitski.streaming.service

import com.kozitski.streaming.args.RunningArgument
import com.kozitski.streaming.domain.{KafkaMessage, Twit}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class LoosedMessagesReviewer extends Serializable{

  def reviewBatch(spark: SparkSession, runningArgument: RunningArgument): RDD[Twit] = {
    val offsetRdd = readBatchFromkafka(spark, runningArgument, runningArgument.kafkaStartOffsets, runningArgument.kafkaEndOffsets)

    val fullRdd = readBatchFromkafka(spark, runningArgument, "earliest", "latest")
    val withTimeRdd = reduceByTime(fullRdd, runningArgument)

    fullRdd.union(withTimeRdd).distinct()
  }

  private def readBatchFromkafka(spark: SparkSession, runningArgument: RunningArgument, startOffset: String, endOffset: String): RDD[Twit]= {
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

  def reduceByTime(rdd: RDD[Twit], runningArgument: RunningArgument): RDD[Twit]=
    rdd.filter(twit => twit.createdAt < runningArgument.revisionStartTime || twit.createdAt > runningArgument.revisionEndTime)

}
