package com.kozitski.twit.cli;

import com.kozitski.twit.cli.config.KafkaConfig;
import com.kozitski.twit.cli.config.LogicConfig;
import com.kozitski.twit.cli.config.TwitterConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class CLIParser {
    private static final Integer EXIT_CODE_ON_FAIL = 1;

    public void parse(String ... args){
        CommandLineParser cliParser = new DefaultParser();
        Options options = new Options();

        List<ArgType> argTypes = Arrays.asList(ArgType.values());
        argTypes.forEach(arg -> options.addOption(arg.getShortName(), arg.getFullName(), arg.isHasArgument(), arg.getDescription()));

        try {
            CommandLine commandLine = cliParser.parse(options, args);
            handleArguments(Arrays.asList(commandLine.getOptions()));        }
        catch (ParseException e){
            System.exit(EXIT_CODE_ON_FAIL);
            log.error("Can not parse command line args", e);
        }
    }

    private void handleArguments(List<Option> options){

        options.forEach(option -> {
            String argument = option.getValue();
            String shortName = option.getOpt();
            String fullName = option.getLongOpt();

            if(argument != null && !argument.isEmpty()){

                // kafka configs
                if(ArgType.TOPIC_NAME.getShortName().equalsIgnoreCase(shortName) || ArgType.TOPIC_NAME.getFullName().equalsIgnoreCase(fullName)){
                    KafkaConfig.topicName = argument;
                }

                if(ArgType.KAFKA_BROKER.getShortName().equalsIgnoreCase(shortName) || ArgType.KAFKA_BROKER.getFullName().equalsIgnoreCase(fullName)){
                    KafkaConfig.kafkaBroker = argument;
                }

                if(ArgType.KAFKA_TIMEOUT.getShortName().equalsIgnoreCase(shortName) || ArgType.KAFKA_TIMEOUT.getFullName().equalsIgnoreCase(fullName)){
                    KafkaConfig.kafkaTimeout = argument;
                }

                if(ArgType.KAFKA_BATCH_SIZE.getShortName().equalsIgnoreCase(shortName) || ArgType.KAFKA_BATCH_SIZE.getFullName().equalsIgnoreCase(fullName)){
                    KafkaConfig.kafkaBatchSize = argument;
                }

                if(ArgType.KAFKA_BUFFER_MEMORY.getShortName().equalsIgnoreCase(shortName) || ArgType.KAFKA_BUFFER_MEMORY.getFullName().equalsIgnoreCase(fullName)){
                    KafkaConfig.kafkaBufferMemory = argument;
                }

                // Twitter configs
                if(ArgType.CONSUMER_KEY.getShortName().equalsIgnoreCase(shortName) || ArgType.CONSUMER_KEY.getFullName().equalsIgnoreCase(fullName)){
                    TwitterConfig.consumerKey = argument;
                }

                if(ArgType.CONSUMER_SECRET_KEY.getShortName().equalsIgnoreCase(shortName) || ArgType.CONSUMER_SECRET_KEY.getFullName().equalsIgnoreCase(fullName)){
                    TwitterConfig.consumerSecretKey = argument;
                }

                if(ArgType.ACCESS_TOKEN.getShortName().equalsIgnoreCase(shortName) || ArgType.ACCESS_TOKEN.getFullName().equalsIgnoreCase(fullName)){
                    TwitterConfig.accessToken = argument;
                }

                if(ArgType.ACCESS_SECRET_TOKEN.getShortName().equalsIgnoreCase(shortName) || ArgType.ACCESS_SECRET_TOKEN.getFullName().equalsIgnoreCase(fullName)){
                    TwitterConfig.accessSecretToken = argument;
                }

                // logic configs
                if(ArgType.KEY_WORDS.getShortName().equalsIgnoreCase(shortName) || ArgType.KEY_WORDS.getFullName().equalsIgnoreCase(fullName)){
                    LogicConfig.keyWords.addAll(Arrays.asList(argument.replace("*", " ").split("_")));
                }

                if(ArgType.WORK_TIMEOUT.getShortName().equalsIgnoreCase(shortName) || ArgType.WORK_TIMEOUT.getFullName().equalsIgnoreCase(fullName)){
                    LogicConfig.workingTimeMS = Long.parseLong(argument);
                }

                if(ArgType.LOCATION_WS_LONGTITUDE.getShortName().equalsIgnoreCase(shortName) || ArgType.LOCATION_WS_LONGTITUDE.getFullName().equalsIgnoreCase(fullName)){
                    LogicConfig.locationWSLongtitude = Double.parseDouble(argument);
                }

                if(ArgType.LOCATION_WS_LATITUDE.getShortName().equalsIgnoreCase(shortName) || ArgType.LOCATION_WS_LATITUDE.getFullName().equalsIgnoreCase(fullName)){
                    LogicConfig.locationWSLatitude = Double.parseDouble(argument);
                }

                if(ArgType.LOCATION_NE_LONGTITUDE.getShortName().equalsIgnoreCase(shortName) || ArgType.LOCATION_NE_LONGTITUDE.getFullName().equalsIgnoreCase(fullName)){
                    LogicConfig.locationNELongtitude = Double.parseDouble(argument);
                }

                if(ArgType.LOCATION_NE_LATITUDE.getShortName().equalsIgnoreCase(shortName) || ArgType.LOCATION_NE_LATITUDE.getFullName().equalsIgnoreCase(fullName)){
                    LogicConfig.locationNELatitude = Double.parseDouble(argument);
                }

            }

        });

    }

}
