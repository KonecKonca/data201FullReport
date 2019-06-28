package com.kozitski.streaming.args

case class RunningArgument(
                            sparkMaster: String,
                            appName: String,
                            kafkaReadTopic: String,
                            kafkaWriteTopic: String,
                            hostName: String,
                            checkpointLocation: String,
                            windowDuration: Int,
                            windowStep: Int,
                            kafkaTimeout: Int,
                            kafkaBatchSize: Int,
                            kafkaBufferMemory: Int,
                            isCheckingLoosedMode: Boolean,
                            kafkaReadingOffsets: String,
                            kafkaWritingOffsets: String
                          )
