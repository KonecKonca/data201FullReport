package com.kozitski.streaming.service

import com.kozitski.streaming.args.RunningArgument
import com.kozitski.streaming.domain.Twit
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream

class TwitsStreamingGrouper extends Serializable {

  def groupTwitsByWindow(twitsStream: DStream[Twit], runningArguments: RunningArgument): DStream[(String, Long)]= {
    twitsStream
      .map(twit => twit.hashtag)
      .countByValueAndWindow(Seconds(runningArguments.windowDuration), Seconds(runningArguments.windowStep))
  }

}
