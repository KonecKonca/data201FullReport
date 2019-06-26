package com.kozitski.spark.util

import java.time.{Instant, ZoneId, ZonedDateTime}

import com.kozitski.spark.service.HdfsSaver

object DateUtils {

  def extractYearDate(timeCreation: Long): String = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getYear + "_" + zonedDateTimeUtc.getDayOfYear
  }

  def extractHour(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getHour
  }

  def extractYear(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getYear
  }

  def extractDay(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getDayOfYear
  }

}
