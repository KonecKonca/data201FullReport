package com.kozitski.spark.service

import com.kozitski.spark.args.RunningArgument
import com.kozitski.spark.domain.KafkaMessage
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class KafkaReader {

  def readAllFromKafka(spark: SparkSession, runningArguments: RunningArgument): RDD[KafkaMessage] ={

    val df = spark.read
      .format("kafka")
      .option("kafka.bootstrap.servers", runningArguments.hostName)
      .option("subscribe", runningArguments.kafkaTopic)
      .option("startingOffsets", "earliest")
      .option("endingOffsets", "latest")
      .load()

    import org.apache.spark.sql.functions._
    import spark.implicits._

    df.select(
      col("key").cast("string"),
      col("value").cast("string"),
      col("offset").cast("long"),
      col("timestamp").cast("long")
    )
      .as[KafkaMessage]
      .rdd
  }

}
