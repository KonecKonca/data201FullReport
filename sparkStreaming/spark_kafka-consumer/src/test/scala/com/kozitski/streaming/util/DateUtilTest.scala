//package com.kozitski.streaming.util
//
//import org.scalatest.FunSuite
//
//class DateUtilTest extends FunSuite {
//  val testDates = List(
//    (1144147188706140161L, 36258551, 28, 18, "36258551_28"),
//    (1144147198406140161L, 36258551, 141, 1, "36258551_141"),
//    (944147198406140161L, 29920803, 250, 6, "29920803_250"),
//    (2454147198406140161L, 77770798, 303, 1, "77770798_303")
//  )
//
//  test("extractYear"){
//    testDates.foreach(elem => {
//      assertResult(DateUtils.extractYear(elem._1))(elem._2)
//    })
//  }
//
//  test("extractDay"){
//    testDates.foreach(elem => {
//      assertResult(DateUtils.extractDay(elem._1))(elem._3)
//    })
//  }
//
//  test("extractHour"){
//    testDates.foreach(elem => {
//      assertResult(DateUtils.extractHour(elem._1))(elem._4)
//    })
//  }
//
//  test("extractYearDate"){
//    testDates.foreach(elem => {
//      assertResult(DateUtils.extractYearDate(elem._1))(elem._5)
//    })
//  }
//
//}
