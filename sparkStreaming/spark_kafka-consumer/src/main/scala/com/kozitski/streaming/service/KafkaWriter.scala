package com.kozitski.streaming.service

import java.util.Properties

import com.kozitski.streaming.args.RunningArgument
import org.apache.kafka.clients.producer.{ProducerRecord, KafkaProducer}
import org.apache.kafka.common.serialization.{StringSerializer, StringDeserializer}

class KafkaWriter() extends Serializable {
  var producer: KafkaProducer[String, String]= _

  def init(runningArgument: RunningArgument): Unit= {

    val props = new Properties()
    props.put("bootstrap.servers", runningArgument.hostName)
    props.put("key.serializer", classOf[StringSerializer])
    props.put("value.serializer", classOf[StringSerializer])
    props.put("serializer.class", classOf[StringDeserializer])

    producer = new KafkaProducer[String, String](props)
  }

  def writeToKafkaTopic(topic: String, message: String): Unit= {
    val keyedMessage: ProducerRecord[String, String] = new ProducerRecord[String, String](topic, message)
    producer.send(keyedMessage)
  }

}
