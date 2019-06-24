package com.kozitski.twit.service;

import com.kozitski.twit.cli.config.KafkaConfig;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * The type Kafka writer.
 */
@Slf4j
public class KafkaWriter {
    private Producer<String, String> producer;

    /**
     * Instantiates a new Kafka writer.
     */
    public KafkaWriter() {
        Properties props = new Properties();
        props.put("metadata.broker.list", KafkaConfig.kafkaBroker);
        props.put("bootstrap.servers", KafkaConfig.kafkaBroker);
        props.put("acks", "all");
        props.put("delivery.timeout.ms", KafkaConfig.kafkaTimeout);
        props.put("batch.size", KafkaConfig.kafkaBatchSize);
        props.put("buffer.memory", KafkaConfig.kafkaBufferMemory);
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ProducerConfig producerConfig = new ProducerConfig(props);
        producer = new Producer<>(producerConfig);
    }

    /**
     * Write to kafka.
     *
     * @param twitterMessage the twitter message
     */
    public void writeToKafka(String twitterMessage){
        KeyedMessage<String, String> message = new KeyedMessage<>(KafkaConfig.topicName, twitterMessage);
        producer.send(message);
    }

}
