sbtPlugin := true

name := "sbt-findbugs"

organization := "uk.co.josephearl"

version := "2.4.1-SNAPSHOT"

libraryDependencies += "net.sf.saxon" % "Saxon-HE" % "9.4"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:_")

licenses += ("EPL-1.0", url("https://www.eclipse.org/legal/epl-v10.html"))

publishMavenStyle := false

publishArtifact in Test := false
