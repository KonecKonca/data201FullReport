package com.kozitski.spark

import com.kozitski.spark.domain.Twit
import com.kozitski.spark.service.KafkaToJsonMapper
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import streamming.TwitsStreamingGrouper

object AppRunner extends App{
  val outputPath = "/user/maria_dev/streaming_advanced/2"

  val spark = SparkSession.builder()
    .master("local")
    .appName("kafkaTwitterReader")
    .getOrCreate()

  val ssc = new StreamingContext(spark.sparkContext, Seconds(100))
  ssc.checkpoint( "/user/maria_dev/streaming_advanced/3/checkpoints")

  val kafkaStream = createKafkaStream(ssc, "twitter_3", "sandbox-hdp.hortonworks.com:6667")
  val twitsDStream: DStream[Twit] = kafkaStream.map(elem => (new KafkaToJsonMapper).twittMapFunction(elem._2))

  (new TwitsStreamingGrouper).groupTwitsByWindow(twitsDStream)

  ssc.start()
  ssc.awaitTermination()

  def createKafkaStream(ssc: StreamingContext, kafkaTopics: String, brokers: String): DStream[(String, String)] = {
    val topicsSet = kafkaTopics.split(",").toSet
    val props = Map(
      "bootstrap.servers" -> "sandbox-hdp.hortonworks.com:6667",
      "metadata.broker.list" -> "sandbox-hdp.hortonworks.com:6667",
      "serializer.class" -> "kafka.serializer.StringEncoder",
      "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )

    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, props, topicsSet)
  }

}
