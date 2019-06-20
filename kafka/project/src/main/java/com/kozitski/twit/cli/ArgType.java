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

    KEY_WORDS("kw", "kewWords",true,"words through the comma like [big*data_ai_machine*learning] * - if space is necessary"),
    WORK_TIMEOUT("wt", "workTimeout",true,"application working time [ms]"),
    LOCATION_WS_LONGTITUDE("lwsl", "locationWSLongtitude",true,"West-Souht longtiude"),
    LOCATION_WS_LATITUDE("lwslat", "locationWSLatitude",true,"West-Souht latitude"),
    LOCATION_NE_LONGTITUDE("lnel", "locationNELongtitude",true,"North-East longtiude"),
    LOCATION_NE_LATITUDE("lnelat", "locationNELatude",true,"North-East latitude");

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
    private String description;
}
