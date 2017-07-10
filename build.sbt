name := """learn-play"""
organization := "ronie"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  evolutions,
  jdbc,
  "org.postgresql" % "postgresql" % "42.1.1",
  "io.getquill" %% "quill-async-postgres" % "1.2.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
)
// Bootstrap
libraryDependencies ++= Seq(
  "org.webjars" %% "webjars-play" % "2.6.0",
  "org.webjars" % "bootstrap" % "3.1.1-2"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "ronie.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "ronie.binders._"
