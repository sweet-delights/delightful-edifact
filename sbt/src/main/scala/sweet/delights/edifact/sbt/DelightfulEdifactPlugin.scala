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

import sbt.Keys._
import sbt._
import sweet.delights.edifact.{Mode, Config => ApiConfig}

object DelightfulEdifactPlugin extends sbt.AutoPlugin {
  override def requires = plugins.JvmPlugin

  object autoImport extends DelightfulEdifactKeys
  import autoImport._

  lazy val pluginName = "sbt-delightful-edifact"

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inConfig(Compile)(baseDelightfulEdifactSettings) ++
      Set(
        sourceGenerators in Compile += (delightfulEdifact in Compile).taskValue
      )

  lazy val baseDelightfulEdifactSettings: Seq[Def.Setting[_]] = Seq(
    delightfulEdifact := (delightfulEdifactGenerate in delightfulEdifact).value,
    sourceManaged in delightfulEdifact := {
      sourceManaged.value / pluginName
    },
    delightfulEdifactInput in delightfulEdifact := {
      val src = sourceDirectory.value
      if (Seq(Compile, Test) contains configuration.value) src / "resources" / "edifact"
      else src / "main" / "resources" / "edifact"
    },
    logLevel in delightfulEdifact := (logLevel ?? Level.Info).value
  ) ++ inTask(delightfulEdifact)(
    Seq(
      delightfulEdifactGenerate := {
        val s = streams.value
        DelightfulEdifactCompile(sources.value, delightfulEdifactConfig.value, sourceManaged.value, s.cacheDirectory, s.log)
      },
      sources := {
        val xml = delightfulEdifactInput.value
        (xml ** "*.xml").get.sorted
      },
      clean := {
        val outdir = sourceManaged.value
        IO.delete((outdir ** "*").get)
        IO.createDirectory(outdir)
      },
      delightfulEdifactPackageName := "foo.bar",
      delightfulEdifactMode := "binary",
      delightfulEdifactIndent := "  ",
      delightfulEdifactDebug := false,
      delightfulEdifactConfig :=
        ApiConfig(
          input = delightfulEdifactInput.value,
          packageName = delightfulEdifactPackageName.value,
          debug = delightfulEdifactDebug.value,
          indent = delightfulEdifactIndent.value,
          mode = Mode(delightfulEdifactMode.value)
        )
    )
  )
}
