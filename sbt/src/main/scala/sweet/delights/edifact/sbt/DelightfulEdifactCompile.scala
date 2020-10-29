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

import java.nio.file.{Files, Paths}

import sbt._
import sbt.internal.util.ManagedLogger
import sweet.delights.edifact.{BuildInfo, CodeGenerator, Config => ApiConfig}

object DelightfulEdifactCompile {

  def apply(sources: Seq[File], config: ApiConfig, outDir: File, cacheDir: File, log: ManagedLogger): Seq[File] = {
    import FilesInfo.{exists, lastModified}
    import Tracked.{inputChanged, outputChanged}
    import sbt.util.CacheImplicits._

    def removeSuffix(name: String): String = {
      val idx = name.lastIndexOf('.')
      if (idx == -1) name
      else name.substring(0, idx)
    }

    def compile: Seq[File] =
      sources.map { src =>
        val conf = config.withInput(src)
        val generator = CodeGenerator(conf)

        if (!outDir.exists()) Files.createDirectories(outDir.toPath)

        val outputFile = new File(outDir, removeSuffix(src.getName) + ".scala")
        Files.write(Paths.get(outputFile.toURI), generator.compile.getBytes("UTF-8"))

        log.info(s"[${DelightfulEdifactPlugin.pluginName}] generated ${outputFile}")

        outputFile
      }

    def cachedCompile =
      inputChanged(cacheDir / s"${DelightfulEdifactPlugin.pluginName}-inputs") { (inChanged, inputs: (List[File], FilesInfo[ModifiedFileInfo], String)) =>
        outputChanged(cacheDir / s"${DelightfulEdifactPlugin.pluginName}-output") { (outChanged, outputs: FilesInfo[PlainFileInfo]) =>
          if (inChanged || outChanged) compile
          else outputs.files.toSeq.map(_.file)
        }
      }

    def inputs: (List[File], FilesInfo[ModifiedFileInfo], String) =
      (sources.toList, lastModified(sources.toSet).asInstanceOf[FilesInfo[ModifiedFileInfo]], BuildInfo.version)

    cachedCompile(inputs)(() => exists((outDir ** "*.scala").get.toSet).asInstanceOf[FilesInfo[PlainFileInfo]])
  }
}
