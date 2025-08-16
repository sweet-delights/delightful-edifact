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
scalaVersion := "2.12.20"
name := "foo"
libraryDependencies ++= Seq(
  "org.sweet-delights"     %% "delightful-edifact"       % s"${sys.props.getOrElse("plugin.version", "0")}",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.4.0"
)
enablePlugins(DelightfulEdifactPlugin)
delightfulEdifactPackageName in (Compile, delightfulEdifact) := "hello.world"
