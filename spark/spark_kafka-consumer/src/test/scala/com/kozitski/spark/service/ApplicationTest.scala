//package com.kozitski.spark.service
//
//import com.holdenkarau.spark.testing.SharedSparkContext
//import org.scalatest.FunSuite
//
//class ApplicationTest extends FunSuite with SharedSparkContext{
//
//  test("111"){
//    sc.parallelize(Array(1, 2 , 3)).foreach(println)
//
//    assertionsHelper
//  }
//
////  var spark: SparkSession = _z
////
//////  override def beforeAll() {
//////    spark = SparkSession
//////      .builder()
//////      .appName("twitter test")
//////      .master("local[*]")
//////      .getOrCreate()
//////  }
////
//////  override def afterAll(): Unit = {
//////    spark.stop()
//////  }
////
////  test("test test"){
////    val conf = new SparkConf()
////      .setMaster("local")
////      .setAppName("Spark test")
////
////    val sparkContext = new SparkContext(conf)
////    val rdd = sparkContext.parallelize(Array(1, 2, 4))
////    rdd.foreach(println)
////
////    sparkContext.stop()
////
//////    val df = mockStreamGenerate()
//////    df.rdd.foreach(println)
////
//////    assertResult(true)(true)
////  }
////
//////  def mockStreamGenerate() : DataFrame = {
//////    val localSpark = spark
//////    import localSpark.implicits._
//////
//////    val memoryStream = new MemoryStream[String](42, spark.sqlContext)
//////
//////    memoryStream.addData(Seq(
//////      "1111111111111111111",
//////      "2222222222222222222",
//////      "3333333333333333333",
//////      "4444444444444444444"
//////    ))
//////
//////    memoryStream.toDF()
//////  }
//
//}
