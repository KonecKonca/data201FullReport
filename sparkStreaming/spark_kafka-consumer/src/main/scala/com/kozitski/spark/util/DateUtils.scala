package com.kozitski.spark.util

import java.time.{Instant, ZoneId, ZonedDateTime}

import com.kozitski.spark.service.HdfsSaver

/**
  * DateUtils tool for extracting data from [[Long]] value
  */
object DateUtils {

  /**
    * @param timeCreation is [[Long]] representation of date
    * @return [[String]] which contains year and day of creation in user-friendly format
    */
  def extractYearDate(timeCreation: Long): String = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getYear + "_" + zonedDateTimeUtc.getDayOfYear
  }

  /**
    * @param timeCreation is [[Long]] representation of date
    * @return [[Int]] which define hour
    */
  def extractHour(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getHour
  }

  /**
    * @param timeCreation is [[Long]] representation of date
    * @return [[Int]] which define year
    */
  def extractYear(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getYear
  }

  /**
    * @param timeCreation is [[Long]] representation of date
    * @return [[Int]] which define day
    */
  def extractDay(timeCreation: Long): Int = {
    val instant = Instant.ofEpochMilli(timeCreation)
    val zonedDateTimeUtc = ZonedDateTime.ofInstant(instant, ZoneId.of(HdfsSaver.ZONE_ID))

    zonedDateTimeUtc.getDayOfYear
  }

}
