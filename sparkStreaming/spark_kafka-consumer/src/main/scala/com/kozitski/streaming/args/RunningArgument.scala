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
                            waterMark: Int,
                            isCheckingLoosedMode: Boolean,
                            kafkaStartOffsets: String,
                            kafkaEndOffsets: String,
                            revisionStartTime: Long,
                            revisionEndTime: Long,
                            revisionKafkaTopic: String
                          )
