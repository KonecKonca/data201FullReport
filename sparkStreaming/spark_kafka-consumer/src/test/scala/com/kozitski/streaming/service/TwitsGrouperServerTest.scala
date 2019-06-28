//package com.kozitski.streaming.service
//
//import com.kozitski.streaming.domain.Twit
//import org.apache.spark.rdd.RDD
//import org.apache.spark.sql.SparkSession
//import org.scalatest.{BeforeAndAfterAll, FunSuite}
//
//class TwitsGrouperServerTest extends FunSuite with BeforeAndAfterAll{
//
//  var spark: SparkSession = _
//
//  override def beforeAll(){
//    spark = SparkSession.builder()
//      .master("local")
//      .appName("test")
//      .getOrCreate()
//  }
//
//  override def afterAll(){
//    spark.stop()
//  }
//
//  test("twits group"){
//    val rdd = spark.sparkContext.textFile("src/test/resources/twitsTest.txt")
//    val twitsRdd: RDD[Twit] = (new KafkaToJsonMapper).sringRddMap(rdd)
//
//    val hashTagOnCount = (new TwitsGrouper).groupByHashTag(twitsRdd)
//
//    val firstHash = hashTagOnCount.first
//    assert(firstHash.equals(("", 10)))
//  }
//
//}
