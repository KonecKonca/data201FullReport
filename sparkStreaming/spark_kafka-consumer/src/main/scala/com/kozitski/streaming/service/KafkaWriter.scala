package com.kozitski.streaming.service

import java.util.Properties

import com.kozitski.streaming.args.RunningArgument
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

/**
  * Kafka writer.
  */
class KafkaWriter() extends Serializable {
  var producer: KafkaProducer[String, String]= _

  /**
    * Set properties for kafka writer
    *
    * @param runningArgument are command line arguments [[RunningArgument]]
    */
  def init(runningArgument: RunningArgument): Unit= {

    val props = new Properties()
    props.put("bootstrap.servers", runningArgument.hostName)
    props.put("key.serializer", classOf[StringSerializer])
    props.put("value.serializer", classOf[StringSerializer])
    props.put("serializer.class", classOf[StringDeserializer])

    producer = new KafkaProducer[String, String](props)
  }

  /**
    * Write message to kafka topic
    *
    * @param topic is kafka topic name [[String]]
    * @param message is sending message [[String]]
    */
  def writeToKafkaTopic(topic: String, message: String): Unit= {
    val keyedMessage: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, message)
    producer.send(keyedMessage)
  }

}
