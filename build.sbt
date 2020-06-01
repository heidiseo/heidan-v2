name := """play-getting-started"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  ws,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.slf4j" % "slf4j-nop" % "1.7.25",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2"
)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ )
