package com.kozitski.streaming

import com.kozitski.streaming.args.{ArgHandler, RunningArgument}
import com.kozitski.streaming.domain.Twit
import com.kozitski.streaming.service.{KafkaReader, KafkaToJsonMapper, KafkaWriter, TwitsStreamingGrouper}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream

object AppRunner extends App{
  private val runningArgument: RunningArgument = (new ArgHandler).handleArgs(args)

  val spark = SparkSession.builder()
    .master(runningArgument.sparkMaster)
    .appName(runningArgument.appName)
    .getOrCreate()

  val kafkaReader = new KafkaReader
  val kafkaDStream: DStream[(String, String)] = kafkaReader.readAllFromKafka(spark, runningArgument)

  logicPerforming(kafkaDStream)

  kafkaReader.start()

  def logicPerforming(dsTream: DStream[(String, String)]): Unit= {

    val twitsDStream: DStream[Twit] = dsTream.map(elem => (new KafkaToJsonMapper).twittMapFunction(elem._2))

    val twitsStreamingGrouper = new TwitsStreamingGrouper
    val hashTagDStream: DStream[(String, Long)] = twitsStreamingGrouper.groupTwitsByWindow(twitsDStream, runningArgument)

    hashTagDStream.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val kafkaWriter = new KafkaWriter
        kafkaWriter.init(runningArgument)

        kafkaWriter.writeToKafkaTopic(runningArgument.kafkaWriteTopic, System.currentTimeMillis().toString)
        partition.foreach(elem => {
          kafkaWriter.writeToKafkaTopic(runningArgument.kafkaWriteTopic, "#" + elem._1 + " :  " + elem._2)
        })
      }
      )
    })

  }

}
