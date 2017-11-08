
name := "scalaio-twitter"
organization := "io.scala"

version := "0.1"

scalaVersion := "2.12.4"

scalacOptions in ThisBuild ++= Seq(
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:existentials",
  "-feature",
  "-deprecation")

scalafmtTestOnCompile := true
scalafmtShowDiff in scalafmt := true

libraryDependencies ++= List(
  "com.danielasfregola" %% "twitter4s" % "5.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)
