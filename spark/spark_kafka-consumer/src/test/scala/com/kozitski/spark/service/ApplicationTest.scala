package com.kozitski.spark.service

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class ApplicationTest extends FunSuite with BeforeAndAfterAll{

  var spark: SparkSession = _

  override def afterAll(): Unit = {
    spark.stop()
  }

}
