package com.kozitski.streaming.service

import com.kozitski.streaming.args.RunningArgument
import com.kozitski.streaming.domain.Twit
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream

/**
  * Twits grouper
  */
class TwitsStreamingGrouper extends Serializable {

  /**
    * Grouping twits by their hashTag
    *
    * @param twitsStream is stream [[DStream]] of twits
    * @param runningArguments command line args [[RunningArgument]]
    * @return [[DStream]] of hashTag [[String]] on count of hashTags [[Long]]
    */
  def groupTwitsByWindow(twitsStream: DStream[Twit], runningArguments: RunningArgument): DStream[(String, Long)]= {
    twitsStream
      .map(twit => twit.hashtag)
      .countByValueAndWindow(Seconds(runningArguments.windowDuration), Seconds(runningArguments.windowStep))
  }

}
