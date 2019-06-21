package com.kozitski.twit;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class AppRunner {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", "sandbox-hdp.hortonworks.com:6667");
        props.put("group.id", "test-consumer-group");
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.seekToBeginning(consumer.assignment());

        consumer.subscribe(Arrays.asList("twitter_2"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("...   offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value() + "\n\n");
        }

    }

}
