package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.collection.immutable.HashSet

class KafkaToJsonMapperTest extends FunSuite with BeforeAndAfterAll with Serializable {
  val actualId: HashSet[String] = HashSet("1144180575516995584", "1144180575470792704", "1144180575860875265", "1144180576112562176",
  "1144180576309542912", "1144180576829747200", "1144180577492525056", "1144180577584766976", "1144180577484062720", "1144180577869975557")

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

  test("json map"){
    val rdd = spark.sparkContext.textFile("src/test/resources/twitsTest.txt")
    val twitsRdd: RDD[Twit] = (new KafkaToJsonMapper).sringRddMap(rdd)

    val expectedId: HashSet[String] = HashSet()
    twitsRdd.foreach(elem => {
      expectedId + elem.id
    })

    expectedId.foreach(id => assert(actualId.contains(id)))
  }

}
