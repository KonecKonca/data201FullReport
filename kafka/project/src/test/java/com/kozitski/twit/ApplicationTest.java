package com.kozitski.twit;

import com.kozitski.twit.cli.config.KafkaConfig;
import com.kozitski.twit.cli.config.LogicConfig;
import com.kozitski.twit.cli.config.TwitterConfig;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

public class ApplicationTest {

    @BeforeTest
    public void init() throws IllegalAccessException {

        Class<KafkaConfig> kafkaConfigClass = KafkaConfig.class;
        setInitilaValues(kafkaConfigClass);

        Class<LogicConfig> logicConfigClass = LogicConfig.class;
        setInitilaValues(logicConfigClass);

        Class<TwitterConfig> twitterConfigClass = TwitterConfig.class;
        setInitilaValues(twitterConfigClass);

    }

    private void setInitilaValues(Class<?> clazz) throws IllegalAccessException {
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            field.set(null, null);
        }
    }


    @Test(priority = 10)
    public void correctRunTest() {
        AppRunner.main(new String[]{
                "-t", "twitter_2",
                "-kb", "sandbox-hdp.hortonworks.com:6667",
                "-kt", "30000",
                "-kbs", "16384",
                "-kbm", "33554432",
                "-ck", "3OiCJM8TkzyG1eDKlp0tjXde2",
                "-csk", "Mq92Y6zCheVkmo2UbOhvLFD2NAxBv1GisxDoFpOTEObgEgG7cl",
                "-at", "1120427063066992640-GfY2GbEMNXVqGILrgr6Ww2BR3CEFr8exs",
                "-ast", "9SRWRAK9RDdVJBrkDr2xP7HEGZWoDPDERyixLGdKz8MPx",
                "-kw", "big*data_ai_machine*learning",
                "-wt", "10000000",
                "-lwsl", "2113.565755",
                "-lwslat", "51.459355",
                "-lnel", "30.987806",
                "-lnelat", "56.345658"
        });
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void missedArgumentsTest() throws NullPointerException{
        AppRunner.main(new String[]{
                "-t", "twitter_2",
                "-lnelat", "56.345658"
        });
    }

}
