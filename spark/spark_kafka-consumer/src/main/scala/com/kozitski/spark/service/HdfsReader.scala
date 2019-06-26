package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

class HdfsReader {

  def readTwitsFromHdfs(spark: SparkSession, path: String): RDD[Twit]= {
    extractFromHdfsToTwit(readFromHdfs(spark, path))
  }

  def readFromHdfs(spark: SparkSession, path: String): RDD[String]= {

    import org.apache.spark.sql.functions._
    import spark.implicits._

    spark
      .read
      .text(path)
      .select(col("value").cast("String"))
      .as[String]
      .rdd

  }

  def extractFromHdfsToTwit(rdd: RDD[String]): RDD[Twit]= {
    rdd.map(elem => {
      val fileds = elem.split(",,")
      Twit(fileds(0).trim().substring(1), fileds(1).trim().toLong,fileds(2).trim().toLong,fileds(3).trim(),
        fileds(4).trim(),fileds(5).trim(),fileds(6).trim().toLong,
        fileds(7).trim().toLong,fileds(8).trim().toLong,fileds(9).trim(),fileds(10).trim(),
        fileds(11).trim(),fileds(12).trim().toLong,fileds(13).trim(),fileds(14).trim(),fileds(15).trim(),
        fileds(16).trim(),fileds(17).trim(),fileds(18).trim(),fileds(19).trim(),fileds(20).trim().toLong,fileds(21).trim(),fileds(22).trim(),
        fileds(23).trim(),fileds(24).trim().toBoolean,fileds(25).trim().toBoolean,fileds(26).trim().toBoolean,
        fileds(27).trim(),fileds(28).trim().toBoolean,fileds(29).trim(),fileds(30).trim().substring(0, fileds(30).trim().length - 1).toBoolean)
    })
  }

}
