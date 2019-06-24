package com.kozitski.twit.service;

import com.kozitski.twit.cli.config.LogicConfig;
import com.kozitski.twit.cli.config.TwitterConfig;
import lombok.extern.slf4j.Slf4j;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.concurrent.TimeUnit;

/**
 * The type Twitter streaming reader.
 */
@Slf4j
public class TwitterStreamingReader {

    private KafkaWriter kafkaWriter;

    /**
     * Instantiates a new Twitter streaming reader.
     */
    public TwitterStreamingReader() {
        kafkaWriter = new KafkaWriter();
    }

    /**
     * Read twits.
     *
     * @param listener    the listener
     * @param filterQuery the filter query
     */
    public void readTwits(OnStatusStatusListener listener, FilterQuery filterQuery){

        ConfigurationBuilder configBuilder = new ConfigurationBuilder();
        configBuilder.setDebugEnabled(true);
        configBuilder.setOAuthConsumerKey(TwitterConfig.consumerKey);
        configBuilder.setOAuthConsumerSecret(TwitterConfig.consumerSecretKey);
        configBuilder.setOAuthAccessToken(TwitterConfig.accessToken);
        configBuilder.setOAuthAccessTokenSecret(TwitterConfig.accessSecretToken);

        TwitterStream twitterStream = new TwitterStreamFactory(configBuilder.build()).getInstance();
        timeExit(twitterStream);

        twitterStream.addListener(listener);
        twitterStream.filter(filterQuery);

    }

    /**
     * Create listener on status status listener.
     *
     * @return the on status status listener
     */
    public OnStatusStatusListener createListener(){

        return status -> {
            log.info("... " + status.getId() + " was received");
            kafkaWriter.writeToKafka(status.toString());
        };

    }

    /**
     * Create filter query filter query.
     *
     * @return the filter query
     */
    public FilterQuery createFilterQuery(){
        FilterQuery filterQuery = new FilterQuery();

        String[] keywords = new String[LogicConfig.keyWords.size()];
        LogicConfig.keyWords.toArray(keywords);
        filterQuery.track(keywords);

        double[][] location = {
                {LogicConfig.locationWSLongtitude, LogicConfig.locationWSLatitude},  // WS-[long-lat]
                {LogicConfig.locationNELongtitude, LogicConfig.locationNELatitude}   // NE-[long-lat]
        };
        filterQuery.locations(location);

        return filterQuery;
    }

    /*
    * Time out handler,
    * stop the program when time is up(according with cli argument).
    */
    private void timeExit(TwitterStream twitterStream ) {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();

            while (LogicConfig.workingTimeMS > System.currentTimeMillis() - startTime){
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                }
                catch (InterruptedException e) {
                    log.warn("During sleeping exception", e);
                }
            }

            twitterStream.shutdown();
            log.info("... Application finished work according with timeWaiting parameter");
            System.exit(0);
        }).start();
    }

}
