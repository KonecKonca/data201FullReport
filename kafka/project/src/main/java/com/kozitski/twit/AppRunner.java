package com.kozitski.twit;

import com.kozitski.twit.cli.CLIParser;
import com.kozitski.twit.service.TwitterStreamingReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppRunner {

    public static void main(String[] args) {
        log.info("Application started...");

        new CLIParser().parse(args);

        new TwitterStreamingReader().readTwits();
    }

}
