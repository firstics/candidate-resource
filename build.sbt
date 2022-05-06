import sbt.Keys._

lazy val akkaHttpVersion = "10.2.4"

val projectNamespace = "candidate.resource"
val projectName = "candidate-resource"
val projectVersion = "0.1.0"

lazy val commonSettings = Seq(
  organization := projectNamespace,
  scalaVersion := "2.13.8",
  version := projectVersion
)

parallelExecution in Test := true



lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    commonSettings,
    name := projectName,
    libraryDependencies ++= Seq(
      "com.typesafe.akka"             %%    "akka-http"                       %   akkaHttpVersion               ,
      "com.typesafe.akka"             %%    "akka-stream"                     %   "2.6.4"                       ,
      "com.typesafe.akka"             %%    "akka-slf4j"                      %   "2.6.4"                       ,
      "ch.qos.logback"                %     "logback-classic"                 %   "1.1.3"                       ,
      "org.json4s"                    %%    "json4s-native"                   %   "3.7.0-M8"                    ,
      "org.json4s"                    %%    "json4s-ext"                      %   "3.6.9"                       ,
      "org.postgresql"                %     "postgresql"                      %   "42.2.5"                      ,
      "org.scalatest"                 %%    "scalatest"                       %   "3.1.0"                 % Test,
      "org.mockito"                   %     "mockito-core"                    %   "3.5.11"                % Test,
      "org.mockito"                   %%    "mockito-scala"                   %   "1.16.1"                      ,
      "com.mockrunner"                %     "mockrunner-jdbc"                 %   "2.0.6"                 % Test

    )
  )
checksums in update := Nil