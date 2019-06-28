package com.kozitski.streaming.service

class LoosedMessagesReviewer extends Serializable {

  private var previousWindow: Array[(String, Long)] = _

  def reviewPreviousBatch(currentWindow: Array[(String, Long)]): Unit = {

    if(previousWindow != null){


    }

    previousWindow = currentWindow
  }

//  def addMissingHashtags(previous: Array[(String, Long)], current: Array[(String, Long)]): Array[(String, Long)] ={
//
//  }

}
