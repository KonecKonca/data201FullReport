package by.epam.hiveudtf

import by.epam.hiveudf.UserAgentUDTF
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.io.{BufferedSource, Source}

class UserAgentUdtfTest extends FunSuite  with BeforeAndAfterAll{
  val userAgentsStringsPath = "src\\test\\resources\\UserAgentTestRecords.txt"
  val userAgentsParsedPath = "src\\test\\resources\\UserAgentParsed.txt"

  var stringSource: BufferedSource = _
  var actualSource: BufferedSource = _

  override def beforeAll(): Unit = {
    stringSource = Source.fromFile(userAgentsStringsPath)
    actualSource = Source.fromFile(userAgentsParsedPath)
  }

  override def afterAll(): Unit = {
    stringSource.close()
    actualSource.close()
  }

  test("parsing test"){

    var expectedArray = Array[String]()
    stringSource.getLines().toList.foreach(string => {UserAgentUDTF.parseUserAgent(string).foreach(elem => expectedArray :+= elem)})
    val actualList: List[String] = actualSource.getLines().toList

    var counter = 0
    while (counter < actualList.length){
      assertResult(actualList(counter))(expectedArray(counter))
      counter += 1
    }

  }

}
