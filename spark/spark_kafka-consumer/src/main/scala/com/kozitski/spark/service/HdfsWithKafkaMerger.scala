package com.kozitski.spark.service

import com.kozitski.spark.args.RunningArgument
import com.kozitski.spark.domain.{KafkaMessage, Twit}
import com.kozitski.spark.util.DateUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

case class HdfsWithKafkaMerger(runningArguments: RunningArgument) {

  def merge(spark: SparkSession, path: String, year: Int, day: Int, hour: Int): RDD[Twit] = {
    val kafkaMessages: RDD[KafkaMessage] = (new KafkaReader).readAllFromKafka(spark, runningArguments)
    val kafkatwitts: RDD[Twit] = (new KafkaToJsonMapper).kafkaToArrayMap(kafkaMessages)
    val reducedKafkaTwitts = readFromKafkaInTimeLine(kafkatwitts, year, day, hour)

    val hdfsTwitts: RDD[Twit] = (new HdfsReader).readTwitsFromHdfs(spark, path)

    reducedKafkaTwitts.union(hdfsTwitts).distinct()
  }

  private def readFromKafkaInTimeLine(rdd: RDD[Twit], year: Int, day: Int, hour: Int): RDD[Twit] = {
    rdd.filter(elem => {
      val createdAt = elem.createdAt

      val receivedYear: Int = DateUtils.extractYear(createdAt)
      val receivedDay: Int = DateUtils.extractDay(createdAt)
      val receivedHour: Int = DateUtils.extractHour(createdAt)

      year == receivedYear && day == receivedDay && hour == receivedHour
    })
  }

}
