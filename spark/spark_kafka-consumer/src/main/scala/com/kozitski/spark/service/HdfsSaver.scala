package com.kozitski.spark.service

import java.time.{Instant, ZoneId, ZonedDateTime}

import com.fasterxml.jackson.databind.ObjectMapper
import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD

class HdfsSaver extends Serializable {

  def transformToSave(rdd: RDD[Twit]): RDD[(String, String, Int)] ={
    rdd.map(twit => {
      val mapper = new ObjectMapper()
      val jsonTwit = mapper.writeValueAsString(twit)

      println("#################")
      println(twit)
      println("#################")
      println(jsonTwit)
      println("#################")

      (jsonTwit, extractYearDate(twit.createdAt), extractHour(twit.createdAt))
    })
  }

  private def extractYearDate(timeCreation: Long): String = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getYear + "_" + zonedDateTimeUtc.getDayOfYear
  }

  private def extractHour(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getHour
  }

}

object HdfsSaver{
  val ZONE_ID = "Europe/Paris"
}
