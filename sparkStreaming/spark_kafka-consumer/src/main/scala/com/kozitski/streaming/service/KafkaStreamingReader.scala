package com.kozitski.streaming.service

import com.kozitski.streaming.args.RunningArgument
import kafka.serializer.StringDecoder
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

class KafkaStreamingReader {
  var ssc: StreamingContext = _

  def readStreamFromKafka(spark: SparkSession, runningArguments: RunningArgument): DStream[(String, String)] ={
    ssc = new StreamingContext(spark.sparkContext, Seconds(runningArguments.windowDuration))
    ssc.checkpoint(runningArguments.checkpointLocation)

    createKafkaStream(ssc, runningArguments.kafkaReadTopic, runningArguments.hostName)
  }

  def createKafkaStream(ssc: StreamingContext, kafkaReadTopics: String, brokers: String): DStream[(String, String)] = {
    val topicsSet = kafkaReadTopics.split(",").toSet
    val props = Map(
      "bootstrap.servers" -> brokers,
      "metadata.broker.list" -> brokers,
      "serializer.class" -> "kafka.serializer.StringEncoder",
      "value.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "key.serializer" -> "org.apache.kafka.common.serialization.StringSerializer",
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )

    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, props, topicsSet)
  }

  def start(): Unit= {
    ssc.start()
    ssc.awaitTermination()
  }


}
