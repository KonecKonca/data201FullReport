package com.kozitski.spark.service

import com.kozitski.spark.domain.Twit
import org.apache.spark.rdd.RDD

class TwitsGrouper extends Serializable {

  // todo: Here must be count by hashTags
  def groupByHashTag(rdd: RDD[Twit]): RDD[(String, List[Twit])] ={

    rdd
      .groupBy(twit => twit.hashtag)
      .map(tuple => (tuple._1, tuple._2.toList))

  }

}
