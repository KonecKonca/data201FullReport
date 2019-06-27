package streamming

import com.kozitski.spark.domain.Twit
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.dstream.DStream

class TwitsStreamingGrouper {

  def groupTwitsByWindow(twitsStream: DStream[Twit]): Unit= {

    // размер окна, шаг перемещения(охват)

    val hashTagsCountDStream: DStream[(String, Long)] = twitsStream
      .map(twit => twit.hashtag)
      .countByValueAndWindow(Seconds(100), Seconds(100))

    hashTagsCountDStream.saveAsTextFiles("/user/maria_dev/streaming_advanced/3/")

    hashTagsCountDStream.foreachRDD(rdd => rdd.foreach(elem => println("        " + elem)))

  }

}
