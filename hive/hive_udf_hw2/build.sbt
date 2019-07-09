name := "hive_udf_hw2"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.11.12",
  "org.apache.hive" % "hive-exec" % "3.1.1" % "provided",
  "eu.bitwalker" % "UserAgentUtils" % "1.21",
  "org.scalatest" % "scalatest_2.11" % "3.0.5" % "test"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

assemblyJarName in assembly := "hive_udf.jar"
