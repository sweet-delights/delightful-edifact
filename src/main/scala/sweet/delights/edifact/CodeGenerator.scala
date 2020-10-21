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
package sweet.delights.edifact

import sweet.delights.xsd.edifact.{Composite, CompositeRef, DataRef, Datum, Def, Grammar, Group, GroupRef, Message, MessageRef, Ref, Segment, SegmentRef}

/**
  * Generates case classes and parser combinators to parse
  * EDIFACT messages represented as bytes.
  *
  * CodeGenerator can be customized by providing some properties:
  * - debug: true/false, adds loggs for debugging purposes
  * - mode: text/binary, determines the values of EDIFACT separators
  *
  * @param props collection of key/value properties
  */
class CodeGenerator(props: Map[String, String] = Map.empty) {

  private lazy val indent = "  "

  private lazy val debug = props.get("debug").exists(_.toBoolean)

  /**
    * Main entry point. Takes a grammar file in input (as a URI)
    * and outputs the generated code as a String.
    *
    * @param uri the location of a the grammar file
    * @return generated code
    */
  def compile(uri: String): String = {
    val grammar = GrammarParser.parseGrammar(uri)
    val dictionary = Dictionary(grammar)

    val lines =
      toPackage() ::
        toImports() ::
        (
        toCaseClasses(dictionary, grammar) ++
          toCombinators(dictionary, grammar)
      )

    lines.mkString("\n")
  }

  private def definitions(grammar: Grammar): List[Def] =
    grammar.messages.message ++
      grammar.groups.group ++
      grammar.segments.segment ++
      grammar.composites.composite

  private def references(definition: Def): List[Ref] = definition match {
    case m: Message =>
      m.messageoption.map(_.as[Ref])

    case g: Group =>
      g.groupoption.map(_.as[Ref])

    case s: Segment =>
      s.segmentoption.map(_.as[Ref])

    case c: Composite =>
      c.compositeoption.map(_.as[Ref])

    case _: Datum =>
      Nil
  }

  private def toPackage(): String =
    props
      .get("packageName")
      .map { packageName =>
        s"""package ${packageName}
         |""".stripMargin
      }
      .getOrElse("")

  private def toImports(): String =
    s"""import scala.annotation.tailrec
         |import scala.collection.mutable.ListBuffer
         |
         |import sweet.delights.edifact._
         |""".stripMargin

  private def toCaseClasses(dictionary: Dictionary, grammar: Grammar): List[String] = {
    val defs = definitions(grammar)
    toCaseClasses(dictionary, defs)
  }

  private def toCaseClasses(dictionary: Dictionary, defs: List[Def]): List[String] = defs.map(toCaseClass(0, _, dictionary))

  private def toCaseClass(depth: Int, definition: Def, dictionary: Dictionary): String = {
    val refs = references(definition).filterNot(ignore)

    refs match {
      case Nil =>
        s"""${indent * depth}object ${toEntityName(definition)}
             |""".stripMargin

      case filtered =>
        s"""${indent * depth}case class ${toEntityName(definition)}(
                 ${filtered.map(toField(depth + 1, _, dictionary)).mkString(",\n")}
             |${indent * depth})${toEmpty(depth, filtered)}
             |""".stripMargin
    }
  }

  private def toField(depth: Int, ref: Ref, dictionary: Dictionary): String = dictionary.get(ref.id) match {
    case Some(_: Datum) =>
      s"|${indent * depth}${toName(ref)}: ${if (ref.repetition > 1) s"List[String]" else if (mandatory(ref)) s"String" else s"Option[String]"}"

    case Some(definition) =>
      s"|${indent * depth}${toName(ref)}: ${if (ref.repetition > 1) s"List[${toEntityName(definition)}]"
      else if (mandatory(ref)) s"${toEntityName(definition)}"
      else s"Option[${toEntityName(definition)}]"}"

    case None =>
      throw new Exception(s"definition of reference ${ref.id} not found")
  }

  private def toEmpty(depth: Int, refs: List[Ref]): String =
    if (nullable(refs)) {
      s""" {
      |${indent * (depth + 1)}def isEmpty: Boolean =
        ${refs.map(toEmpty(depth + 2, _)).mkString(" &&\n")}
      |${indent * depth}}""".stripMargin
    } else {
      ""
    }

  private def toEmpty(depth: Int, ref: Ref): String =
    s"""|${indent * depth}${toName(ref)}.isEmpty"""

  private def toName(ref: Ref): String = ref.fieldName match {
    case "type" => "tpe"
    case fieldName => fieldName
  }

  private def mandatory(ref: Ref): Boolean = ref.status match {
    case "M" | "M*" => true
    case _ => false
  }

  private def toEntityName(definition: Def): String = definition match {
    case _: Datum => "Data"
    case c: Composite => s"Composite${if (c.id.startsWith("_")) "" else "_"}${c.id}"
    case s: Segment => s"Segment${if (s.id.startsWith("_")) "" else "_"}${s.id}"
    case g: Group => s"Group${if (g.id.startsWith("_")) "" else "_"}${g.id}"
    case m: Message => s"${m.tag}_${m.organization}_${m.major}_${m.minor}"
  }

  private def toCombinators(dictionary: Dictionary, grammar: Grammar): List[String] =
    (toCombinatorsStart() :: definitions(grammar).map(toCombinator(dictionary, 1, _))) ++ List(toCombinatorsEnd(grammar))

  private def toCombinatorsStart(): String = {
    s"""abstract class EdifactParser extends BytesParsers {
         |
         |  // TODO remove once scala-parsers-combinators 1.2.0 is released
         |  // cf. https://github.com/scala/scala-parser-combinators/pull/245
         |  def repMN[T](p: Parser[T], sep: Parser[Any], m: Int, n: Int): Parser[List[T]] = Parser { in =>
         |    require(0 <= m && m <= n)
         |    val required = if (m == 0) success(Nil) else (p ~ repN(m - 1, sep ~> p)).map { case head ~ tail => head :: tail }
         |    val elems = new ListBuffer[T]
         |
         |    def continue(in: Input): ParseResult[List[T]] = {
         |      val p0 = sep ~> p // avoid repeatedly re-evaluating by-name parser
         |      @tailrec def applyp(in0: Input): ParseResult[List[T]] = p0(in0) match {
         |        case Success(x, rest) => elems += x; if (elems.length == n) Success(elems.toList, rest) else applyp(rest)
         |        case e @ Error(_, _) => e // still have to propagate error
         |        case _ => Success(elems.toList, in0)
         |      }
         |
         |      applyp(in)
         |    }
         |
         |    required(in) match {
         |      case Success(x, rest) => elems ++= x; continue(rest)
         |      case ns: NoSuccess => ns
         |    }
         |  }
         |
         |  def repMN[T](p: Parser[T], m: Int, n: Int): Parser[List[T]] = repMN[T](p, success(()), m, n)
         |
         |  lazy val quote: Parser[Byte] = ${if (props.get("mode").exists(_ == "text")) """'\''.toByte""" else "0x1c.toByte"}
         |
         |  lazy val plus: Parser[Byte] = ${if (props.get("mode").exists(_ == "text")) "'+'.toByte" else "0x1d.toByte"}
         |
         |  lazy val colon: Parser[Byte] = ${if (props.get("mode").exists(_ == "text")) "':'.toByte" else "0x1f.toByte"}
         |
         |  lazy val star: Parser[Byte] = ${if (props.get("mode").exists(_ == "text")) "'*'.toByte" else "0x19.toByte"}
         |
         |  lazy val parse_Data /*(m: Int, n: Int)*/: Parser[String] = notIn(
         |    Set[Byte](
         |      ${if (props.get("mode").exists(_ == "text")) """'\''.toByte""" else "0x1c.toByte"},
         |      ${if (props.get("mode").exists(_ == "text")) "'+'.toByte" else "0x1d.toByte"},
         |      ${if (props.get("mode").exists(_ == "text")) "':'.toByte" else "0x1f.toByte"},
         |      ${if (props.get("mode").exists(_ == "text")) "'*'.toByte" else "0x19.toByte"}
         |    )
         |  ) ^^ { arr =>
         |    new String(arr, "UTF-8")
         |  }
         |""".stripMargin
  }

  private def toCombinatorsEnd(grammar: Grammar): String =
    s"""}
        |
        |object EdifactParser extends EdifactParser {
        |
           ${grammar.messages.message.map(toMessageParser(1, _)).mkString("\n")}
        |}
        |""".stripMargin

  private def toMessageParser(depth: Int, message: Message): String = {
    s"""|${indent * depth}def parse_${toEntityName(message)}(bytes: Array[Byte]): ParseResult[${toEntityName(message)}] = parse(parse_${toEntityName(message)}, bytes)
        |"""
  }

  private def toCombinator(dictionary: Dictionary, depth: Int, definition: Def): String = {
    val refs = references(definition)

    refs.filterNot(_.fieldName.isEmpty) match {
      case Nil =>
        s"""${indent * depth}lazy val parse_${toEntityName(definition)}: Parser[${toEntityName(definition)}.type] = success(${toEntityName(definition)})
             |""".stripMargin

      case _ =>
        val start = definition match {
          case seg: Segment => s""" "${seg.tag}"${if (refs.isEmpty) "" else " ~>"}"""
          case _ => ""
        }

        val termination = definition match {
          case _: Segment => s""" <~ quote"""
          case _ => ""
        }

        s"""|${indent * depth}lazy val parse_${toEntityName(definition)}: Parser[${toEntityName(definition)}] =${start}
          ${refs.zipWithIndex
             .map { case (ref, i) => toCombinator(dictionary, depth + 1, definition, ref, i == 0) }
             .mkString(" ~\n")}${termination} ^^ {
                           ${toInstances(dictionary, depth + 1, definition)}
              |${indent * (depth + 1)}}
              |""".stripMargin
    }
  }

  private def toCombinator(dictionary: Dictionary, depth: Int, parent: Def, ref: Ref, first: Boolean): String = dictionary.get(ref.id) match {
    case Some(_: Datum) =>
      val parentIsSegment = parent.isInstanceOf[Segment]
      s"""|${indent * depth}${toLog(
        s"""${toRepetition(
          s"""${if (first && !parentIsSegment) "" else s"""(${toSeparator(parent)} ~> """}parse_Data${if (first && !parentIsSegment)
            s"${toConditional(ref)}"
          else
            s")${toConditional(ref)}"}""",
          parent,
          ref
        )}""",
        ref
      )}"""

    case Some(definition: Composite) =>
      s"""|${indent * depth}${toLog(
        s"""${toRepetition(s"""(${toSeparator(parent)} ~> parse_${toEntityName(definition)}${toConditional(ref)})""", parent, ref)}""",
        ref
      )}"""

    case Some(definition: Segment) =>
      s"""|${indent * depth}${toLog(s"""${toRepetition(s"""parse_${toEntityName(definition)}""", parent, ref)}""", ref)}"""

    case Some(definition: Group) =>
      s"""|${indent * depth}${toLog(s"""${toRepetition(s"""parse_${toEntityName(definition)}""", parent, ref)}""", ref)}"""

    case Some(definition: Message) =>
      s"""|${indent * depth}${toLog(s"""${toRepetition(s"""parse_${toEntityName(definition)}""", parent, ref)}""", ref)}"""

    case _ =>
      ""
  }

  private def toInstances(dictionary: Dictionary, depth: Int, definition: Def): String = {
    val refs = references(definition)
    refs match {
      case Nil => s"""|${indent * depth}case _ => ${toEntityName(definition)}"""
      case _ =>
        s"""|${indent * depth}case ${refs.map(ref => if (ignore(ref)) "_" else toName(ref)).mkString(" ~ ")} =>
            |${indent * (depth + 1)}${toEntityName(definition)}(
             ${refs.filterNot(ignore).map(ref => s"|${indent * (depth + 2)}${toName(ref)}${toFlatten(dictionary, ref)}").mkString(",\n")}
            |${indent * (depth + 1)})"""
    }
  }

  private def toConditional(ref: Ref): String = ref match {
    case _: DataRef | _: CompositeRef =>
      if (mandatory(ref)) ""
      else if (ref.repetition > 1) ".?"
      else ""
    case _ => ""
  }

  private def toRepetition(parser: String, parent: Def, ref: Ref): String = {
    if (mandatory(ref) && ref.repetition > 1) (parent, ref) match {
      case (_: Composite, _: DataRef) => s"""repMN(${parser}, ${toSeparator(parent)}, 1, ${ref.repetition})"""
      case (_: Segment, _: DataRef) => s"""repMN(${parser}, ${toSeparator(parent)}, 1, ${ref.repetition})"""
      case _ => s"repMN(${parser}, 1, ${ref.repetition})"
    } else if (ref.repetition > 1) {
      s"repMN(${parser}, 0, ${ref.repetition})"
    } else if (mandatory(ref)) s"${parser}"
    else
      ref match {
        case _: DataRef | _: CompositeRef => if (ref.repetition > 1) s"${parser}" else s"${parser}.?"
        case _ => s"${parser}.?"
      }
  }

  private def toFlatten(dictionary: Dictionary, ref: Ref): String = ref match {
    case _: DataRef =>
      if (ref.repetition > 1 && !mandatory(ref)) {
        if (nullable(dictionary, ref)) s".flatten.reverse.dropWhile(_.trim.isEmpty).reverse"
        else ".flatten"
      } else if (!mandatory(ref) && nullable(dictionary, ref)) ".filterNot(_.isEmpty)"
      else ""

    case _: CompositeRef =>
      if (ref.repetition > 1 && !mandatory(ref)) {
        if (nullable(dictionary, ref)) s".flatten.reverse.dropWhile(_.isEmpty).reverse"
        else ".flatten"
      } else if (!mandatory(ref) && nullable(dictionary, ref)) ".filterNot(_.isEmpty)"
      else ""

    case _ =>
      if (ref.repetition > 1 && nullable(dictionary, ref)) ".filterNot(_.isEmpty)"
      else if (!mandatory(ref) && nullable(dictionary, ref)) ".filterNot(_.isEmpty)"
      else ""
  }

  private def toSeparator(definition: Def): String = definition match {
    case _: Datum => "star"
    case _: Composite => "colon"
    case _: Segment => "plus"
    case _: Group => "quote"
    case _: Message => "quote"
  }

  private def toLog(parser: String, ref: Ref): String =
    if (debug) ref match {
      case _: DataRef => s"""log(${parser})("(D) ${toName(ref)}")"""
      case _: CompositeRef => s"""log(${parser})("(C) ${toName(ref)}")"""
      case _: SegmentRef => s"""log(${parser})("(S) ${toName(ref)}")"""
      case _: GroupRef => s"""log(${parser})("(G) ${toName(ref)}")"""
      case _: MessageRef => s"""log(${parser})("(M) ${toName(ref)}")"""
    } else parser

  private def ignore(ref: Ref): Boolean = ref.fieldName.isEmpty || ref.status == "N/A"

  private def nullable(dictionary: Dictionary, ref: Ref): Boolean = dictionary.get(ref.id) match {
    case Some(definition) => nullable(definition)
    case None => throw new Exception(s"definition for ${ref.id} not found")
  }

  private def nullable(definition: Def): Boolean = nullable(references(definition).filterNot(ignore))

  private def nullable(refs: List[Ref]): Boolean = refs.forall { ref =>
    ref.repetition > 1 || ref.status == "C" || ref.status == "N/A"
  }
}
