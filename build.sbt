enablePlugins(ScalaJSPlugin)

enablePlugins(ScalaJSBundlerPlugin)

enablePlugins(WorkbenchPlugin)

name := "PaperStx"

version := "0.1-SNAPSHOT"

scalaVersion := "2.12.4"

scalacOptions := Seq(
  "-language:higherKinds",
  "-language:implicitConversions"
)

scalaJSUseMainModuleInitializer := true
mainClass in Compile := Some("paperstx.App")

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.2",
  "com.github.japgolly.scalajs-react" %%% "core" % "1.1.1",
  "com.github.japgolly.scalacss" %%% "core" % "0.5.3",
  "com.github.japgolly.scalacss" %%% "ext-react" % "0.5.3",
  "org.scalaz" %%% "scalaz-core" % "7.2.19",
  "com.lihaoyi" %%% "fastparse" % "1.0.0",
  "com.lihaoyi" %%% "utest" % "0.6.0" % "test"
)

npmDependencies in Compile ++= Seq(
  "react" -> "15.6.1",
  "react-dom" -> "15.6.1"
)

testFrameworks += new TestFramework("utest.runner.Framework")