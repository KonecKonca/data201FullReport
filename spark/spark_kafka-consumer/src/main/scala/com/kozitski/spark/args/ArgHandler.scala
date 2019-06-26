package com.kozitski.spark.args

class ArgHandler {

  def handleArgs(args: Array[String]): RunningArgument= {
    RunningArgument(args(0), args(1), args(2), args(3), args(4), args(5), args(6), args(7))
  }

}
