package com.kozitski.twit;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import lombok.extern.slf4j.Slf4j;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;

@Slf4j
public class AppRunner {
    private static final String TOPIC_NAME = "twitter";

    public static void main(String[] args) throws TwitterException {


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

        KeyedMessage<String, String> message = null;
        message = new KeyedMessage<>(TOPIC_NAME, "any text");
        producer.send(message);

        twittStreaming();

    }
    private static void twittStreaming() {

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true);
        configBuilder.setOAuthConsumerKey("3OiCJM8TkzyG1eDKlp0tjXde2");
        configBuilder.setOAuthConsumerSecret("Mq92Y6zCheVkmo2UbOhvLFD2NAxBv1GisxDoFpOTEObgEgG7cl");
        configBuilder.setOAuthAccessToken("1120427063066992640-GfY2GbEMNXVqGIL6Ww2BR3CEFr8exs");
        configBuilder.setOAuthAccessTokenSecret("9SRWRAK9RDdVJBrkDr2xP7HEGZWoDPDERyixLGdKz8MPx");

        TwitterStream twitterStream = new TwitterStreamFactory(configBuilder.build()).getInstance();
        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                System.out.println(status.toString());
            }
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) { }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) { }
            @Override
            public void onScrubGeo(long userId, long upToStatusId) { }
            @Override
            public void onStallWarning(StallWarning stallWarning) { }
            @Override
            public void onException(Exception ex) {
                log.error("Exception during receiving twit", ex);
            }

        };

        FilterQuery filterQuery = new FilterQuery();
        String keywords[] = {"big data", "machine learning", "ai"};
        filterQuery.track(keywords);
        double[][] location = {
                {23.190837, 51.568748},  // WS-[long-lat]
                {30.482569, 56.113596}   // NE-[long-lat]
        };
        filterQuery.locations(location);

        twitterStream.addListener(listener);
        twitterStream.filter(filterQuery);

    }

}
