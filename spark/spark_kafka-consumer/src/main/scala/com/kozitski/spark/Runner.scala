package com.kozitski.spark

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.from_json

object Runner extends App {

  val spark = SparkSession.builder()
    .master("local")
    .appName("kafka reader")
    .getOrCreate()

  val df = spark.read
    .format("kafka")
    .option("kafka.bootstrap.servers", "sandbox-hdp.hortonworks.com:6667")
    .option("subscribe", "twitter_2")
    .option("startingOffsets", "earliest")
    .option("endingOffsets", "latest")
    .load()



//  println("Start reading elements")
//
//  df.collect().foreach(row => println(":::   " + row.get(1)))

}



