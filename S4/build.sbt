//name := "S4"
//
//version := "0.1"
//
//scalaVersion := "2.13.1"
//
//val akkaVersion = "2.5.23"
//
//val scalaTestVersion = "3.1.5"
//
//libraryDependencies ++= Seq(
//	"com.typesafe.akka" %% "akka-stream" % akkaVersion,
//	"com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
//	"com.typesafe.akka" %% "akka-testkit" % akkaVersion,
//	"org.scalatest" %% "scalatest" % scalaTestVersion
//)

name := "S4"

version := "0.1"

scalaVersion := "2.13.1"

val akkaVersion = "2.5.23"

val scalaTestVersion = "3.1.5"
libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % akkaVersion,
	"com.typesafe.akka" %% "akka-testkit" % akkaVersion,
	"com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
	"org.scalatest" %% "scalatest" % "3.1.0"
)