package sweet.delights.edifact

import java.io.File

case class Config(
  input: File,
  packageName: String = "foo.bar",
  debug: Boolean = false,
  indent: String = "  ",
  mode: Mode = Mode.Binary
) {
  def withInput(input: File): Config = this.copy(input = input)
}

sealed trait Mode
object Mode {
  case object Binary extends Mode
  case object Text extends Mode

  def apply(mode: String): Mode = mode match {
    case "binary" => Mode.Binary
    case "text" => Mode.Text
    case _ => throw new IllegalArgumentException(s"Unknown mode ${mode}")
  }
}
