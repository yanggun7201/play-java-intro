name := """play-java-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  javaJpa,
  cache,
  javaWs,
  "com.h2database" % "h2" % "1.4.191",
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final"
)
