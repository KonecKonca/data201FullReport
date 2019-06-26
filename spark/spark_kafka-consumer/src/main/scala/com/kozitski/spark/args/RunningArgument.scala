package com.kozitski.spark.args

case class RunningArgument(
                            sparkMaster: String,
                            appName: String,
                            path: String,
                            year: String,
                            day: String,
                            hour: String,
                            kafkaTopic: String,
                            hostName: String
                          )
