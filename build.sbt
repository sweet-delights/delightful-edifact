// This file is part of delightful-edifact.
//
// delightful-edifact is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.
import java.util.regex.Pattern
import sbtrelease._
import ReleaseTransformations._
import ScalaxbPlugin._
import sbtrelease.ReleasePlugin.autoImport.{releaseCommitMessage, releaseNextVersion}

lazy val Common = config("common") extend (Compile)
lazy val Edifact = config("xsd") extend (Compile)

lazy val scala212 = "2.12.17"
lazy val scala213 = "2.13.10"

lazy val commonSettings = Seq(
  organization := "org.sweet-delights",
  name := "delightful-edifact",
  homepage := Option(url("https://github.com/sweet-delights/delightful-edifact")),
  licenses := List("GNU Lesser General Public License Version 3" -> url("https://www.gnu.org/licenses/lgpl-3.0.txt")),
  description := "delightful-edifact is an EDIFACT data-binding library and code generator for Scala",
  scmInfo := Option(ScmInfo(url("https://github.com/sweet-delights/delightful-edifact"), "scm:git@github.com:sweet-delights/delightful-edifact.git")),
  developers := List(
    Developer(
      id = "pgrandjean",
      name = "Patrick Grandjean",
      email = "pgrandjean.github.com@gmail.com",
      url = url("https://github.com/pgrandjean")
    )
  ),
  scalaVersion := scala212,
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %% "monocle-core"             % "2.1.0",
    "com.github.julien-truffaut" %% "monocle-macro"            % "2.1.0",
    "org.scala-lang.modules"     %% "scala-xml"                % "2.4.0",
    "org.scala-lang.modules"     %% "scala-parser-combinators" % "2.3.0",
    "org.slf4j"                  % "slf4j-api"                 % "2.0.7",
    "org.slf4j"                  % "slf4j-log4j12"             % "2.0.7" % "test",
    "io.spray"                   %% "spray-json"               % "1.3.6" % "test",
    "org.specs2"                 %% "specs2-core"              % "4.20.0" % "test"
  ),
  publishMavenStyle := true,
  publishTo := Some {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      "snapshots" at nexus + "content/repositories/snapshots"
    else
      "releases" at nexus + "service/local/staging/deploy/maven2"
  },
  // sbt-release
//  releaseCrossBuild := true,
  releaseVersion := { ver =>
    val bumpedVersion = Version(ver)
      .map { v =>
        suggestedBump.value match {
          case Version.Bump.Bugfix => v.withoutQualifier.string
          case _ => v.bump(suggestedBump.value).withoutQualifier.string
        }
      }
      .getOrElse(versionFormatError(ver))
    bumpedVersion
  },
  releaseNextVersion := { ver =>
    Version(ver).map(_.withoutQualifier.bump.string).getOrElse(versionFormatError(ver)) + "-SNAPSHOT"
  },
  releaseCommitMessage := s"[sbt-release] setting version to ${(ThisBuild / version).value}",
  bugfixRegexes := List(s"${Pattern.quote("[patch]")}.*").map(_.r),
  minorRegexes := List(s"${Pattern.quote("[minor]")}.*").map(_.r),
  majorRegexes := List(s"${Pattern.quote("[major]")}.*").map(_.r),
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    releaseStepCommandAndRemaining("api/test"),
    releaseStepCommandAndRemaining("api2_12/test"),
    releaseStepCommandAndRemaining("sbtPlugin/scripted"),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("api/publishSigned"),
    releaseStepCommandAndRemaining("api2_12/publishSigned"),
    releaseStepCommandAndRemaining("sbtPlugin/publishSigned"),
    setNextVersion,
    commitNextVersion,
    releaseStepCommand("sonatypeRelease"),
    pushChanges
  ),
  scalafmtOnCompile := true
)

lazy val api = (projectMatrix in file("api"))
  .settings(commonSettings: _*)
  .settings(codeGenSettings: _*)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](organization, name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "sweet.delights.edifact"
  )
  .jvmPlatform(scalaVersions = Seq(scala213, scala212))

lazy val sbtPlugin = (project in file("sbt"))
  .enablePlugins(SbtPlugin)
  .dependsOn(api.jvm(scala212))
  .settings(
    commonSettings,
    name := "sbt-delightful-edifact",
    description := "SBT plugin to run delightful-edifact",
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    scripted := scripted.dependsOn(api.jvm(scala212) / publishLocal).evaluated
  )

lazy val root = (project in file("."))
  .aggregate(api.projectRefs: _*)
  .aggregate(sbtPlugin)
  .settings(commonSettings: _*)

def codeGenSettings: Seq[Def.Setting[_]] =
  inConfig(Edifact)(baseScalaxbSettings ++ inTask(scalaxb)(customScalaxbSettings("edifact"))) ++
    Seq(
      Compile / sourceGenerators += (Edifact / scalaxb).taskValue,
      Edifact / scalaxb / scalaxbGenerateRuntime := true
    )

def customScalaxbSettings(base: String): Seq[Def.Setting[_]] = Seq(
  scalaxbXsdSource := file("api/src/main/resources/xsd"),
  sourceManaged := (Compile / sourceManaged).value / "sbt-scalaxb" / base,
  scalaxbGenerateRuntime := false,
  sources := listOrdered(scalaxbXsdSource.value / base, ".xsd"),
  scalaxbPackageName := "sweet.delights.xsd." + base,
  scalaxbProtocolFileName := "xmlprotocol.scala",
  scalaxbNamedAttributes := true,
  scalaxbGenerateMutable := false,
  scalaxbUseLists := true
)

def listOrdered(f: File, suffix: String): Seq[File] = {
  val files = listFiles(f, suffix).sortBy(_.getCanonicalPath)
  files
}

def listFiles(f: File, suffix: String): Seq[File] = {
  val all = Option(f.listFiles).toSeq.flatten
  val files = all.filter(_.isFile).filter(_.getName.endsWith(suffix))
  val dirs = all.filter(_.isDirectory)
  files ++ dirs.flatMap(listFiles(_, suffix))
}

def crossReleaseStepCommandAndRemaining(scalaVersions: Seq[String])(releaseSteps: Seq[ReleaseStep]): Seq[ReleaseStep] = {
  scalaVersions.map { scalaVersion =>
    ReleaseStep(releaseStepCommandAndRemaining(s"++${scalaVersion}!"))
  } ++ releaseSteps
}
