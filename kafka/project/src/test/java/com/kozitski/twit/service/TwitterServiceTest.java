package com.kozitski.twit.service;


import com.kozitski.twit.cli.config.KafkaConfig;
import com.kozitski.twit.cli.config.LogicConfig;
import com.kozitski.twit.cli.config.TwitterConfig;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TwitterServiceTest {

    private OnStatusStatusListener listener;
    private List<String> twitts;

    @BeforeTest
    public void init() throws InterruptedException {
        setConfigs();

        twitts = new ArrayList<>();

        TwitterStreamingReader twitterStreamingReader = new TwitterStreamingReader();
        twitterStreamingReader.readTwits(
                listener,
                twitterStreamingReader.createFilterQuery());

        System.out.println(twitts);

        TimeUnit.SECONDS.sleep(5);
    }

    private void setConfigs(){
        TwitterConfig.accessToken = "1120427063066992640-GfY2GbEMNXVqGIL6Ww2BR3CEFr8exs";
        TwitterConfig.accessSecretToken = "9SRWRAK9RDdVJBrkDr2xP7HEGZWoDPDERyixLGdKz8MPx";
        TwitterConfig.consumerKey = "3OiCJM8TkzyG1eDKlp0tjXde2";
        TwitterConfig.consumerSecretKey = "Mq92Y6zCheVkmo2UbOhvLFD2NAxBv1GisxDoFpOTEObgEgG7cl";

        KafkaConfig.kafkaBufferMemory = "";
        KafkaConfig.topicName = "";
        KafkaConfig.kafkaBatchSize = "";
        KafkaConfig.kafkaTimeout = "";
        KafkaConfig.kafkaBroker = "";

        LogicConfig.locationWSLongtitude = 23.565755 ;
        LogicConfig.locationWSLatitude = 51.459355;
        LogicConfig.locationNELongtitude = 30.987806 ;
        LogicConfig.locationNELatitude = 56.345658;

        LogicConfig.workingTimeMS = 1_000_000L;

        listener = status -> twitts.add(status.toString());
    }

    @Test
    public void contentTest(){
        twitts.forEach(twit ->
                Assert.assertTrue(
                        twit.contains("big data") ||
                            twit.contains("ai") ||
                            twit.contains("machine learning")
                )
        );
    }

}
