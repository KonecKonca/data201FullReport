package com.kozitski.twit.cli;

import lombok.Getter;

public enum ArgType {

    TOPIC_NAME("t", "topic",true,"kafka topic name"),
    KAFKA_BROKER("kb", "kafkaBroker",true,"kafka broker host"),
    KAFKA_TIMEOUT("kt", "kafkaTimeout",true,"kafka timeout"),
    KAFKA_BATCH_SIZE("kbs", "kafkaBachSize",true,"kafka bach size"),
    KAFKA_BUFFER_MEMORY("kbm", "kafkaBufferMemory",true,"kafka buffer memory"),

    CONSUMER_KEY("ck", "consumerKey",true,"twitter consumer key"),
    CONSUMER_SECRET_KEY("csk", "consumerSecretKey",true,"twitter consumer secret key"),
    ACCESS_TOKEN("at", "accessToken",true,"twitter access token"),
    ACCESS_SECRET_TOKEN("ast", "accessSecretToken",true,"twitter access secret token"),

    KEY_WORDS,
    LOCATION_WS_LONGTITUDE,
    LOCATION_WS_LATITUDE,
    LOCATION_NE_LONGTITUDE,
    LOCATION_NE_LATITUDE;

    ArgType(String shortName, String fullName, boolean hasArgument, String description) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.hasArgument = hasArgument;
        this.description = description;
    }

    @Getter
    private String shortName;
    @Getter
    private String fullName;
    @Getter
    private boolean hasArgument;
    @Getter
    private String description
}
