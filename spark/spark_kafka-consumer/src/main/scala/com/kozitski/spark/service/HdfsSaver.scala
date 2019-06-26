package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import com.kozitski.spark.util.DateUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SaveMode, SparkSession}

class HdfsSaver extends Serializable {

  def saveTwitsToHdfsWithPartitioning(rdd: RDD[Twit], spark: SparkSession, path: String): Unit =
    saveToHdfsWithPartitioning(transformToSave(rdd), spark, path)

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

  def saveTwitsToHdfs(rdd: RDD[Twit], spark: SparkSession, path: String): Unit =
    saveToHdfs(transformToSave(rdd), spark, path)

  def saveToHdfs(rdd: RDD[(String, String, Int)], spark: SparkSession, path: String): Unit = {
    import spark.implicits._

    rdd
      .toDS()
      .write
      .mode(SaveMode.Overwrite)
      .format("csv")
      .save(path)
  }

  def transformToSave(rdd: RDD[Twit]): RDD[(String, String, Int)] =
    rdd.map(twit => {(twit.toString, DateUtils.extractYearDate(twit.createdAt), DateUtils.extractHour(twit.createdAt))})


}

object HdfsSaver{
  val ZONE_ID = "Europe/Paris"
}
