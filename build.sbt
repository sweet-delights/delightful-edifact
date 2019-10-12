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

lazy val commonSettings = Seq(
  organization := "sweet-delights",
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
  scalaVersion := "2.12.12",
  crossScalaVersions := Seq("2.12.12", "2.13.3"),
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %% "monocle-core"             % "2.0.3",
    "com.github.julien-truffaut" %% "monocle-macro"            % "2.0.3",
    "org.scala-lang.modules"     %% "scala-xml"                % "1.3.0",
    "org.scala-lang.modules"     %% "scala-parser-combinators" % "1.1.1",
    "org.slf4j"                  % "slf4j-api"                 % "1.7.25",
    "org.slf4j"                  % "slf4j-log4j12"             % "1.7.25" % "test",
    "io.spray"                   %% "spray-json"               % "1.3.5" % "test",
    "org.specs2"                 %% "specs2-core"              % "4.5.1" % "test"
  ),
  publishArtifact in (Compile, packageDoc) := false,
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),
  releaseCrossBuild := true,
  releaseVersion := { ver => // taken from ti-starter-kit
    val bumpedVersion = Version(ver)
      .map { v =>
        suggestedBump.value match {
          case Version.Bump.Bugfix => v.withoutQualifier.string // Already bumped by previous release
          case _ => v.bump(suggestedBump.value).withoutQualifier.string
        }
      }
      .getOrElse(versionFormatError(ver))
    bumpedVersion
  },
  releaseNextVersion := { ver =>
    Version(ver).map(_.withoutQualifier.bump.string).getOrElse(versionFormatError(ver)) + "-SNAPSHOT"
  },
  releaseCommitMessage := s"[sbt-release] setting version to ${(version in ThisBuild).value}",
  bugfixRegexes := List(s"${Pattern.quote("[patch]")}.*").map(_.r),
  minorRegexes := List(s"${Pattern.quote("[minor]")}.*").map(_.r),
  majorRegexes := List(s"${Pattern.quote("[major]")}.*").map(_.r),
  // releasePublishArtifactsAction := publishLocal.value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    runClean,
    runTest,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  ),
  scalafmtOnCompile := true
)

// lazy val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.2"
// lazy val scalaParser = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1"

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(codeGenSettings: _*)

def codeGenSettings: Seq[Def.Setting[_]] =
  inConfig(Edifact)(baseScalaxbSettings ++ inTask(scalaxb)(customScalaxbSettings("edifact"))) ++
    Seq(
      sourceGenerators in Compile += (scalaxb in Edifact).taskValue,
      scalaxbGenerateRuntime in (Edifact, scalaxb) := true
    )

def customScalaxbSettings(base: String): Seq[Def.Setting[_]] = Seq(
  scalaxbXsdSource := file("src/main/resources/xsd"),
  sourceManaged := (sourceManaged in Compile).value / "sbt-scalaxb" / base,
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
