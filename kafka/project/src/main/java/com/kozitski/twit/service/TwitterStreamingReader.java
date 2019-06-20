package com.kozitski.twit.service;

import com.kozitski.twit.cli.config.LogicConfig;
import com.kozitski.twit.cli.config.TwitterConfig;
import lombok.extern.slf4j.Slf4j;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TwitterStreamingReader {

    private KafkaWriter kafkaWriter;

    public TwitterStreamingReader() {
        kafkaWriter = new KafkaWriter();
    }

    public void readTwits(){

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true);
        configBuilder.setOAuthConsumerKey(TwitterConfig.consumerKey);
        configBuilder.setOAuthConsumerSecret(TwitterConfig.consumerSecretKey);
        configBuilder.setOAuthAccessToken(TwitterConfig.accessToken);
        configBuilder.setOAuthAccessTokenSecret(TwitterConfig.accessSecretToken);

        TwitterStream twitterStream = new TwitterStreamFactory(configBuilder.build()).getInstance();
        timeExit(twitterStream);

        StatusListener listener = new StatusListener() {

            @Override
            public void onStatus(Status status) {
                System.out.println("... " + status.getId() + " was received");
                kafkaWriter.writeToKafka(status.toString());
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
        String[] keywords = new String[LogicConfig.keyWords.size()];
        LogicConfig.keyWords.toArray(keywords);
        filterQuery.track(keywords);
        double[][] location = {
                {LogicConfig.locationWSLongtitude, LogicConfig.locationWSLatitude},  // WS-[long-lat]
                {LogicConfig.locationNELongtitude, LogicConfig.locationNELatitude}   // NE-[long-lat]
        };
        filterQuery.locations(location);

        twitterStream.addListener(listener);
        twitterStream.filter(filterQuery);

    }

    private void timeExit(TwitterStream twitterStream ) {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();

            while (LogicConfig.workingTimeMS > System.currentTimeMillis() - startTime){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (InterruptedException e) {
                    log.warn("During slepping exception", e);
                }
            }

            twitterStream.shutdown();
            log.info("... Application finished work according with timeWaiting parameter");
            System.exit(0);
        }).start();
    }

}
