package com.kozitski.spark.args

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
    RunningArgument(args(0), args(1), args(2), args(3), args(4), args(5), args(6), args(7))
  }

}
