package com.kozitski.spark.service

import com.kozitski.spark.domain.KafkaMessage
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class KafkaReader {

  def readAllFromKafka(spark: SparkSession): RDD[KafkaMessage] ={

    val df = spark.read
      .format("kafka")
      .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
      .option("subscribe", "twitter_3")
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
