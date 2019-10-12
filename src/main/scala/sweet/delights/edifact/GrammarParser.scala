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

import monocle.function.all._
import monocle.macros.GenLens
import scalaxb.DataRecord
import sweet.delights.xsd.edifact.{Composite, CompositeRef, Composites, Data, Grammar, Group, Groups, Message, Messages, Ref, Segment, SegmentRef, Segments}

/**
  * EDIFACT grammar parser and cleanser.
  */
object GrammarParser {

  private lazy val messagesLens = GenLens[Grammar](_.messages)

  private lazy val messageLens = GenLens[Messages](_.message)

  private lazy val messageRefsLens = GenLens[Message](_.messageoption)

  private lazy val groupsLens = GenLens[Grammar](_.groups)

  private lazy val groupLens = GenLens[Groups](_.group)

  private lazy val groupRefsLens = GenLens[Group](_.groupoption)

  private lazy val segmentsLens = GenLens[Grammar](_.segments)

  private lazy val segmentLens = GenLens[Segments](_.segment)

  private lazy val segmentRefsLens = GenLens[Segment](_.segmentoption)

  private lazy val compositesLens = GenLens[Grammar](_.composites)

  private lazy val compositeLens = GenLens[Composites](_.composite)

  private lazy val compositeRefsLens = GenLens[Composite](_.compositeoption)

  private lazy val dataLens = GenLens[Grammar](_.data)

  private lazy val datumLens = GenLens[Data](_.datum)

  private lazy val grammarMessageRefsLens = messagesLens composeLens messageLens composeTraversal each composeLens messageRefsLens

  private lazy val grammarGroupRefsLens = groupsLens composeLens groupLens composeTraversal each composeLens groupRefsLens

  private lazy val grammarSegmentsLens = segmentsLens composeLens segmentLens

  private lazy val grammarSegmentRefsLens = grammarSegmentsLens composeTraversal each composeLens segmentRefsLens

  private lazy val grammarCompositesLens = compositesLens composeLens compositeLens

  private lazy val grammarCompositeRefsLens = grammarCompositesLens composeTraversal each composeLens compositeRefsLens

  private lazy val grammarDataLens = dataLens composeLens datumLens

  def parseGrammar(uri: String): Grammar = {
    val xml = scala.xml.XML.load(uri)
    val grammar = scalaxb.fromXML[Grammar](xml)

    (enrich andThen clean)(grammar)
  }

  private val enrich: Grammar => Grammar = {
    val is = GrammarParser.getClass.getClassLoader.getResourceAsStream("xsd/edifact/standard.xml")
    val xml = scala.xml.XML.load(is)
    is.close()

    val standards = scalaxb.fromXML[Grammar](xml)
    val stdSegmentRefs = standards.segments.segment.map { seg =>
      DataRecord(SegmentRef(seg.id, seg.tag.toLowerCase, "C", 1))
    }

    val injectStdSegmentRefs = grammarMessageRefsLens.modify(stdSegmentRefs ++ _)
    val injectStdSegments = grammarSegmentsLens.modify(standards.segments.segment ++ _)
    val injectStdComposites = grammarCompositesLens.modify(standards.composites.composite ++ _)
    val injectStdData = grammarDataLens.modify(standards.data.datum ++ _)

    injectStdSegmentRefs andThen injectStdSegments andThen injectStdComposites andThen injectStdData
  }

  private val clean: Grammar => Grammar = {
    val messagesFiltered = grammarMessageRefsLens.modify(_.filter(dr => valid(dr.as[Ref])))
    val groupsFiltered = grammarGroupRefsLens.modify(_.filter(dr => valid(dr.as[Ref])))
    val segmentsFiltered = grammarSegmentRefsLens.modify(_.reverse.dropWhile(dr => !valid(dr.as[Ref].status)).reverse)
    val compositesFiltered = grammarCompositeRefsLens.modify(_.reverse.dropWhile(dr => !valid(dr.as[Ref].status)).reverse)

    messagesFiltered andThen groupsFiltered andThen segmentsFiltered andThen compositesFiltered
  }

  private def valid(ref: Ref): Boolean = valid(ref.status)

  private def valid(status: String): Boolean = status != "N/A"
}
