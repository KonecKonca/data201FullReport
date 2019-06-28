package com.kozitski.streaming.args

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class ArgsHandlerTest extends FunSuite with BeforeAndAfterAll{
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
    val args: Array[String] = spark.sparkContext.textFile("src/test/resources/runningArgs.txt").flatMap(string => string.split(" ")).collect()

    val runningArgument = (new ArgHandler).handleArgs(args)

    assertResult(runningArgument.revisionEndTime)(0)
    assertResult(runningArgument.revisionStartTime)(0)
    assertResult(runningArgument.kafkaStartOffsets)(null)
    assertResult(runningArgument.kafkaEndOffsets)(null)
    assertResult(runningArgument.revisionKafkaTopic)(null)
  }

}
