package com.kozitski.streaming.service

import com.kozitski.streaming.args.ArgHandler
import com.kozitski.streaming.domain.Twit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class LoosedMessagesReviewerTest  extends FunSuite with BeforeAndAfterAll with Serializable {
  var spark: SparkSession = _

  override def beforeAll(){
    spark = SparkSession.builder()
      .master("local")
      .appName("test")
      .getOrCreate()
  }

  override def afterAll(){
    spark.stop()
  }

  test("arg handling"){
    val rdd = spark.sparkContext.textFile("src/test/resources/twitsTest.txt")
    val twitsRdd: RDD[Twit] = (new KafkaToJsonMapper).sringRddMap(rdd)

    val args: Array[String] = spark.sparkContext.textFile("src/test/resources/runningArgs.txt").flatMap(string => string.split(" ")).collect()
    val runningArgument = (new ArgHandler).handleArgs(args)
    val arguments = runningArgument.copy(revisionStartTime = 1461628881000L, revisionEndTime = 1561628881007L)

    val expected = (new LoosedMessagesReviewer).reduceByTime(twitsRdd, arguments).count()

    assertResult(expected)(5)
  }

}
