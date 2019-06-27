package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD

/**
  * TwitsGrouper is responsible for counting of usage hashTags
  */
class TwitsGrouper extends Serializable {

  /**
    * Group Twits by hashTags and collect count of them
    *
    * @param rdd id [[RDD]]
    * @return [[RDD]]
    */
  def groupByHashTag(rdd: RDD[Twit]): RDD[(String, Int)] ={

    rdd
      .groupBy(twit => twit.hashtag)
      .map(tuple => (tuple._1, tuple._2.size))

  }

  /**
    * Provide [[String]] report about count of used hadhTags
    *
    * @param rdd is [[RDD]]
    * @return [[String]] report
    */
  def hashTagWithCountReport(rdd: RDD[(String, Int)]): String = {
    var result = "------------------------------------------\n"

    rdd
      .collect()
      .foreach(elem => {result += "#" + elem._1 + ": " + elem._2 + "\n" })

    result += "------------------------------------------\n"

    result
  }

}
