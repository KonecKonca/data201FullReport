package by.epam.hiveudf

import eu.bitwalker.useragentutils.UserAgent
import org.apache.hadoop.hive.ql.exec.{UDFArgumentException, UDFArgumentTypeException}
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory
import org.apache.hadoop.hive.serde2.objectinspector.{ObjectInspector, ObjectInspectorFactory, PrimitiveObjectInspector, StructObjectInspector}

import scala.collection.JavaConversions.seqAsJavaList

/*
  UDTF for hive. Parse from UserAgent string
  into 4 separate fields [city_id, client_os, client_browser, client_device]
 */
class UserAgentUDTF extends GenericUDTF{

  var intObjectInspector: PrimitiveObjectInspector = _
  var stringObjectInspector: PrimitiveObjectInspector = _

  override def initialize(argOIs: StructObjectInspector): StructObjectInspector = {
    val inputFields = argOIs.getAllStructFieldRefs

    // check correctness of input parameters
    if (inputFields.size() != 2) {
      throw new UDFArgumentException("UDF takes exactly two arguments!")
    }

    // here must be UserAgent in String format
    val inputIdOI = inputFields.get(0).getFieldObjectInspector
    if (inputIdOI.getCategory != ObjectInspector.Category.PRIMITIVE
      && inputIdOI.asInstanceOf[PrimitiveObjectInspector].getPrimitiveCategory != PrimitiveObjectInspector.PrimitiveCategory.INT) {
      throw new UDFArgumentTypeException(0, "UDF takes INT as a first parameter!")
    }

    // here must be city_id in Int format
    val inputUaOI = inputFields.get(1).getFieldObjectInspector
    if (inputUaOI.getCategory != ObjectInspector.Category.PRIMITIVE
      && inputUaOI.asInstanceOf[PrimitiveObjectInspector].getPrimitiveCategory != PrimitiveObjectInspector.PrimitiveCategory.STRING) {
      throw new UDFArgumentTypeException(1, "UDF takes String as a second parameter!")
    }

    intObjectInspector = inputIdOI.asInstanceOf[PrimitiveObjectInspector]
    stringObjectInspector = inputUaOI.asInstanceOf[PrimitiveObjectInspector]

    val fieldNames = List("city_id", "client_device", "client_os", "client_browser")
    val fieldIOs = List[ObjectInspector] (
      PrimitiveObjectInspectorFactory.javaIntObjectInspector,
      PrimitiveObjectInspectorFactory.javaStringObjectInspector,
      PrimitiveObjectInspectorFactory.javaStringObjectInspector,
      PrimitiveObjectInspectorFactory.javaStringObjectInspector
    )


    ObjectInspectorFactory.getStandardStructObjectInspector(seqAsJavaList[String](fieldNames), seqAsJavaList[ObjectInspector](fieldIOs))
  }

  override def process(args: Array[AnyRef]): Unit = {
    val cityID = intObjectInspector.getPrimitiveJavaObject(args(0))
    val userAgent = stringObjectInspector.getPrimitiveJavaObject(args(1))

    val parsedUA = UserAgentUDTF.parseUserAgent(userAgent.asInstanceOf[String])

    val result = Array[Object] (
      cityID.asInstanceOf[java.lang.Integer],
      parsedUA(0),
      parsedUA(1),
      parsedUA(2)
    )

    forward(result)
  }

  override def close(): Unit = {
    /* NOP */
  }

}

/*
  Parse from string with using eu.bitwalker.useragentutils
  client_os, client_browser, client_device and return them as Array[String]
 */
object UserAgentUDTF{
  def parseUserAgent(uaString: String): Array[String] = {

    val userAgent: UserAgent = UserAgent.parseUserAgentString(uaString)

    Array[String] (
      userAgent.getOperatingSystem.getDeviceType.getName,
      userAgent.getOperatingSystem.getName,
      userAgent.getBrowser.getName
    )

  }
}
