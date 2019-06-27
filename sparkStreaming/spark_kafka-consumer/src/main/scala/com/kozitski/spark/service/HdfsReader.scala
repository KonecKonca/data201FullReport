package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * HdfsReader read data from hdfs
  */
class HdfsReader {

  /**
    * It is composite method which delegate to [[extractFromHdfsToTwit]]
    * mapping of [[String]] [[RDD]]
    *
    * @param spark is [[SparkSession]]
    * @param path is [[String]] output path
    * @return [[RDD]] of [[Twit]]
    */
  def readTwitsFromHdfs(spark: SparkSession, path: String): RDD[Twit]= {
    extractFromHdfsToTwit(readFromHdfs(spark, path))
  }

  /**
    * @param spark is [[SparkSession]]
    * @param path is [[String]] output path
    * @return [[RDD]] of [[Twit]]
    */
  def readFromHdfs(spark: SparkSession, path: String): RDD[String]= {

    import org.apache.spark.sql.functions._
    import spark.implicits._

    spark
      .read
      .text(path)
      .select(col(HdfsReader.COLUMN_NAME).cast(HdfsReader.COLUMN_TYPE))
      .as[String]
      .rdd

  }

  /**
    *
    * @param rdd is [[RDD]] of [[String]] which is mapped to [[Twit]] [[RDD]]
    * @return is [[RDD]] of [[Twit]]
    */
  def extractFromHdfsToTwit(rdd: RDD[String]): RDD[Twit]= {
    rdd.map(elem => {
      val fileds = elem.split(HdfsReader.REGEXP_DELIMETR)
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

/**
  * Common constants for [[HdfsReader]] class
  */
object HdfsReader{
  val COLUMN_NAME: String = "value"
  val COLUMN_TYPE: String = "String"
  val REGEXP_DELIMETR: String = ",,"
}