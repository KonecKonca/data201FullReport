package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import com.kozitski.spark.util.DateUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * HdfsSaver is tool which responsible for saving [[Twit]] into hdfs
  */
class HdfsSaver extends Serializable {

  /**
    * @param rdd for saving
    * @param spark is [[SparkSession]]
    * @param path is [[String]] outputPath
    */
  def saveTwitsToHdfsWithPartitioning(rdd: RDD[Twit], spark: SparkSession, path: String): Unit =
    saveToHdfsWithPartitioning(transformToSave(rdd), spark, path)

  /**
    * @param rdd for saving
    * @param spark is [[SparkSession]]
    * @param path is [[String]] outputPath
    */
  def saveToHdfsWithPartitioning(rdd: RDD[(String, String, Int)], spark: SparkSession, path: String): Unit = {
    import spark.implicits._

    rdd
      .toDS()
      .write
      .mode(SaveMode.Overwrite)
      .partitionBy("_2", "_3")
      .format("csv")
      .save(path)

  }

  /**
    * @param rdd for saving
    * @param spark is [[SparkSession]]
    * @param path is [[String]] outputPath
    */
  def saveTwitsToHdfs(rdd: RDD[Twit], spark: SparkSession, path: String): Unit =
    saveToHdfs(transformToSave(rdd), spark, path)

  /**
    * @param rdd for saving
    * @param spark is [[SparkSession]]
    * @param path is [[String]] outputPath
    */
  def saveToHdfs(rdd: RDD[(String, String, Int)], spark: SparkSession, path: String): Unit = {
    import spark.implicits._

    rdd
      .toDS()
      .write
      .mode(SaveMode.Overwrite)
      .format("csv")
      .save(path)
  }

  /**
    * @param rdd of [[Twit]]
    * @return mapped rdd for saving into hdfs 2 [[String]] arguments define partitioning during saving
    */
  def transformToSave(rdd: RDD[Twit]): RDD[(String, String, Int)] =
    rdd.map(twit => {(twit.toString, DateUtils.extractYearDate(twit.createdAt), DateUtils.extractHour(twit.createdAt))})

}

/**
  * HdfsSaver constants object
  */
object HdfsSaver{
  val ZONE_ID = "Europe/Paris"
}
