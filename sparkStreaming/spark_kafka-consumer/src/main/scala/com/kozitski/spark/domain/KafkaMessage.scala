package com.kozitski.spark.domain

/**
  *
  * @param key is [[String]]
  * @param value is [[String]]
  * @param offset is [[Long]]
  * @param timestamp is [[Long]]
  */
case class KafkaMessage(
                        key: String,
                        value: String,
                        offset: Long,
                        timestamp: Long
                       )