package com.kozitski.twit;

import com.kozitski.twit.cli.CLIParser;
import com.kozitski.twit.service.TwitterStreamingReader;
import lombok.extern.slf4j.Slf4j;

/**
 * The type App runner.
 */
@Slf4j
public class AppRunner {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        log.info("Application started...");

        new CLIParser().parse(args);

        TwitterStreamingReader twitterStreamingReader = new TwitterStreamingReader();
        twitterStreamingReader.readTwits(
                twitterStreamingReader.createListener(),
                twitterStreamingReader.createFilterQuery());

    }

}
