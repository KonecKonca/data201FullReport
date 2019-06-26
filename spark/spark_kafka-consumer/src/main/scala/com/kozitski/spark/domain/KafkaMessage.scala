package com.kozitski.spark.domain

case class KafkaMessage(
                        key: String,
                        value: String,
                        offset: Long,
                        timestamp: Long
                       )