package com.kozitski.twit.cli;

import lombok.Getter;

/**
 * The enum Arg type.
 */
public enum ArgType {

    /**
     * The Topic name.
     */
    TOPIC_NAME("t", "topic",true,"kafka topic name"),
    /**
     * The Kafka broker.
     */
    KAFKA_BROKER("kb", "kafkaBroker",true,"kafka broker host"),
    /**
     * The Kafka timeout.
     */
    KAFKA_TIMEOUT("kt", "kafkaTimeout",true,"kafka timeout"),
    /**
     * The Kafka batch size.
     */
    KAFKA_BATCH_SIZE("kbs", "kafkaBachSize",true,"kafka bach size"),
    /**
     * The Kafka buffer memory.
     */
    KAFKA_BUFFER_MEMORY("kbm", "kafkaBufferMemory",true,"kafka buffer memory"),

    /**
     * The Consumer key.
     */
    CONSUMER_KEY("ck", "consumerKey",true,"twitter consumer key"),
    /**
     * The Consumer secret key.
     */
    CONSUMER_SECRET_KEY("csk", "consumerSecretKey",true,"twitter consumer secret key"),
    /**
     * The Access token.
     */
    ACCESS_TOKEN("at", "accessToken",true,"twitter access token"),
    /**
     * The Access secret token.
     */
    ACCESS_SECRET_TOKEN("ast", "accessSecretToken",true,"twitter access secret token"),

    /**
     * The Key words.
     */
    KEY_WORDS("kw", "kewWords",true,"words through the comma like [big*data_ai_machine*learning] * - if space is necessary"),
    /**
     * The Work timeout.
     */
    WORK_TIMEOUT("wt", "workTimeout",true,"application working time [ms]"),
    /**
     * The Location ws longtitude.
     */
    LOCATION_WS_LONGTITUDE("lwsl", "locationWSLongtitude",true,"West-Souht longtiude"),
    /**
     * The Location ws latitude.
     */
    LOCATION_WS_LATITUDE("lwslat", "locationWSLatitude",true,"West-Souht latitude"),
    /**
     * The Location ne longtitude.
     */
    LOCATION_NE_LONGTITUDE("lnel", "locationNELongtitude",true,"North-East longtiude"),
    /**
     * The Location ne latitude.
     */
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
