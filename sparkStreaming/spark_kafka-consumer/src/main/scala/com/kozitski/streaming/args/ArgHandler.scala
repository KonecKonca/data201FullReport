package com.kozitski.streaming.args

/**
  * ArgHandler tool for handling command line arguments
  */
class ArgHandler {

  /**
    *
    * @param args is a [[Array]] which contain command line arguments
    * @return [[RunningArgument]] which union command line arguments
    */
  def handleArgs(args: Array[String]): RunningArgument= {
    var runningArgument: RunningArgument = null

    if(args(11).toBoolean.equals(false)){
      runningArgument = RunningArgument(args(0), args(1), args(2), args(3), args(4), args(5),
        args(6).toInt, args(7).toInt, args(8).toInt, args(9).toInt, args(10).toInt, args(11).toBoolean, null, null, 0, 0, null)
    }
    else {
      runningArgument = RunningArgument(args(0), args(1), args(2), args(3), args(4), args(5),
        args(6).toInt, args(7).toInt, args(8).toInt, args(9).toInt, args(10).toInt,  args(11).toBoolean,
        args(12), args(13), args(14).toLong, args(15).toLong, args(16))
    }

    runningArgument
  }

}
