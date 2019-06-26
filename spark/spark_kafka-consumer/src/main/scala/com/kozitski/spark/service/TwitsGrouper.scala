package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD

class TwitsGrouper extends Serializable {

  def groupByHashTag(rdd: RDD[Twit]): RDD[(String, Int)] ={

    rdd
      .groupBy(twit => twit.hashtag)
      .map(tuple => (tuple._1, tuple._2.size))

  }

  def hashTagWithCountReport(rdd: RDD[(String, Int)]): String = {
    var result = "------------------------------------------\n"

    rdd
      .collect()
      .foreach(elem => {result += "#" + elem._1 + ": " + elem._2 + "\n" })

    result += "------------------------------------------\n"

    result
  }

}
