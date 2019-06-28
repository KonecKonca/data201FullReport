package com.kozitski.streaming

import com.kozitski.streaming.args.{ArgHandler, RunningArgument}
import com.kozitski.streaming.domain.Twit
import com.kozitski.streaming.service.{KafkaStreamingReader, KafkaToJsonMapper, KafkaWriter, LoosedMessagesReviewer, TwitsStreamingGrouper}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream

object AppRunner extends App{
  private val runningArgument: RunningArgument = (new ArgHandler).handleArgs(args)

  val spark = SparkSession.builder()
    .master(runningArgument.sparkMaster)
    .appName(runningArgument.appName)
    .getOrCreate()

  if(runningArgument.isCheckingLoosedMode){
    val messagesReviewer = new LoosedMessagesReviewer
    val updatedTwitts = messagesReviewer.reviewBatch(spark, runningArgument)

    val updatedHashes = updatedTwitts.groupBy(twit => twit.hashtag)
    updatedHashes.foreachPartition(partition => {
      val kafkaWriter = new KafkaWriter

      partition.foreach(hash => kafkaWriter.writeToKafkaTopic("#####", "#" + hash._1 + " :  " + hash._2))
    })

  }
  else {
    val kafkaReader = new KafkaStreamingReader
    val kafkaDStream: DStream[(String, String)] = kafkaReader.readStreamFromKafka(spark, runningArgument)

    logicPerforming(kafkaDStream)

    kafkaReader.start()
  }

  def logicPerforming(dsTream: DStream[(String, String)]): Unit= {

    val twitsDStream: DStream[Twit] = dsTream.map(elem => (new KafkaToJsonMapper).twittMapFunction(elem._2))

    val twitsStreamingGrouper = new TwitsStreamingGrouper
    val hashTagDStream: DStream[(String, Long)] = twitsStreamingGrouper.groupTwitsByWindow(twitsDStream, runningArgument)

    hashTagDStream.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val kafkaWriter = new KafkaWriter
        kafkaWriter.init(runningArgument)

        kafkaWriter.writeToKafkaTopic(runningArgument.kafkaWriteTopic, System.currentTimeMillis().toString)
        partition.foreach(hash => {
          kafkaWriter.writeToKafkaTopic(runningArgument.kafkaWriteTopic, "#" + hash._1 + " :  " + hash._2)
        })
      }
      )
    })

  }

}
