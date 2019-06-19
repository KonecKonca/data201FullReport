package com.kozitski.twit;

import com.kozitski.twit.domain.Twitt;
import com.kozitski.twit.service.ParseTwittService;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.producer.KeyedMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class AppRunner {
    private static final String TOPIC_NAME = "twitter";
    private static final ParseTwittService parseTwitt = new ParseTwittService();

    public static void main(String[] args){
    log.error("______1");

        Properties props = new Properties();
        props.put("metadata.broker.list", "sandbox-hdp.hortonworks.com:6667");
        props.put("bootstrap.servers", "sandbox-hdp.hortonworks.com:6667");
        props.put("acks", "all");
        props.put("delivery.timeout.ms", 30000);
        props.put("batch.size", 16384);
        props.put("buffer.memory", 33554432);
        props.put("serializer.class", "kafka.serializer.StringEncoder");

        ProducerConfig producerConfig = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(producerConfig);

        pushTwitterMessage(producer);

//        GeoQuery geoQuery = new GeoQuery((String) null);
//        geoQuery.setQuery("New York");

    }

    public static void pushTwitterMessage(Producer<String, String> producer){
        String consumerKey = "3OiCJM8TkzyG1eDKlp0tjXde2";
        String consumerSecret = "Mq92Y6zCheVkmo2UbOhvLFD2NAxBv1GisxDoFpOTEObgEgG7cl";
        String token = "1120427063066992640-GfY2GbEMNXVqGIL6Ww2BR3CEFr8exs";
        String secretToken = "9SRWRAK9RDdVJBrkDr2xP7HEGZWoDPDERyixLGdKz8MPx";
        KeyedMessage<String, String> message = null;

        log.error("______2");

        BlockingQueue<String> queue = new LinkedBlockingQueue<>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();

        endpoint.trackTerms(Arrays.asList("big data", "ai", "machine learning"));

        Authentication auth = new OAuth1(consumerKey, consumerSecret, token, secretToken);

        log.error("______3");

        Client client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        log.error("______3");

        client.connect();

        log.error("______4");

        for (int msgRead = 0; msgRead < 1000; msgRead++) {

            try{
                String msg = queue.take();
                Optional<Twitt> twitt = parseTwitt.parseTwitt(msg);

                log.error("______5");
                twitt.ifPresent(System.out::println);
                message = new KeyedMessage<>(TOPIC_NAME, queue.take());
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }

            producer.send(message);
        }

        producer.close();
        client.stop();

    }


}
