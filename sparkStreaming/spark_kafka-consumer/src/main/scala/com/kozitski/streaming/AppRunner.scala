package com.kozitski.streaming

import com.kozitski.streaming.args.{ArgHandler, RunningArgument}
import com.kozitski.streaming.domain.Twit
import com.kozitski.streaming.service._
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.DStream

/**
  * Application runner
  */
object AppRunner extends App{
  private val runningArgument: RunningArgument = (new ArgHandler).handleArgs(args)

  val spark = SparkSession.builder()
    .master(runningArgument.sparkMaster)
    .appName(runningArgument.appName)
    .getOrCreate()

  if(runningArgument.isCheckingLoosedMode) revisionStart() else streamingStart()

  /**
    * Running process of revision data with defined
    * kafka offsets and time ranges according to arguments
    * from command line
    */
  def revisionStart(): Unit ={

    val messagesReviewer = new LoosedMessagesReviewer
    val updatedTwits = messagesReviewer.reviewBatch(spark, runningArgument)

    val updatedHashes = updatedTwits.groupBy(twit => twit.hashtag)
    updatedHashes.foreachPartition(partition => {
      val kafkaWriter = new KafkaWriter

      partition.foreach(hash => kafkaWriter.writeToKafkaTopic("#####", "#" + hash._1 + " :  " + hash._2))
    })

  }

  /**
    * Running of streaming chain, which include reading from kafka,
    * grouping and writing to kafka.
    * foreachPartition was used for reducing creating new object [[KafkaWriter]]
    * for every element of stream
    */
  def streamingStart(): Unit= {
    val kafkaReader = new KafkaStreamingReader
    val kafkaDStream: DStream[(String, String)] = kafkaReader.readStreamFromKafka(spark, runningArgument)

    val twitsDStream: DStream[Twit] = kafkaDStream.map(elem => (new KafkaToJsonMapper).twittMapFunction(elem._2))

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

    kafkaReader.start()
  }

}
