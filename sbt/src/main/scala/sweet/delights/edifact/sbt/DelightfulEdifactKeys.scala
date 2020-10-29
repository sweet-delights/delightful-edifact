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
package sweet.delights.edifact.sbt

import sbt._

import sweet.delights.edifact.{Config => ApiConfig}

trait DelightfulEdifactKeys {
  lazy val delightfulEdifact = taskKey[Seq[File]]("Generates case classes and typeclass instances")
  lazy val delightfulEdifactGenerate = taskKey[Seq[File]]("Generates case classes and typeclass instances")
  lazy val delightfulEdifactConfig = settingKey[ApiConfig]("Configuration for code generator")
  lazy val delightfulEdifactInput = settingKey[File]("EDIFACT grammars directory")
  lazy val delightfulEdifactPackageName = settingKey[String]("Specifies the target package")
  lazy val delightfulEdifactDebug = settingKey[Boolean]("Specifies if parsers should include debug logging or not")
  lazy val delightfulEdifactIndent = settingKey[String]("""Specified indent string for generated code (ex: \t or "  ")""")
  lazy val delightfulEdifactMode = settingKey[String]("Specifies EDIFACT encoding: binary or text")
}

object DelightfulEdifactKeys extends DelightfulEdifactKeys
