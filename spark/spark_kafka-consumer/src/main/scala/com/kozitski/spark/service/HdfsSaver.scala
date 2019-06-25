package com.kozitski.spark.service

import java.time.{Instant, ZoneId, ZonedDateTime}

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SaveMode

class HdfsSaver extends Serializable {

  def transformToSave(rdd: RDD[Twit]): RDD[(String, String, Int)] ={
    rdd.map(twit => (twit.toString, extractYearDate(twit.createdAt), extractHour(twit.createdAt)))
  }

  private def extractYearDate(timeCreation: Long): String = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Paris"))

    zonedDateTimeUtc.getYear + "_" + zonedDateTimeUtc.getDayOfYear
  }

  private def extractHour(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of("Europe/Paris"))

    zonedDateTimeUtc.getHour
  }

}
