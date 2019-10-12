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

import sweet.delights.xsd.edifact.{Data, Def, Grammar}

/**
  * Indexes definitions for quick access.
  */
case class Dictionary(private val grammar: Grammar) {

  lazy val definitions = {
    val defs: List[Def] = grammar.messages.message ++
      grammar.groups.group ++
      grammar.segments.segment ++
      grammar.composites.composite ++
      grammar.data.datum

    defs.map(d => (d.id, d)).toMap
  }

  def keys: Iterable[String] = definitions.keys

  def get(id: String): Option[Def] = definitions.get(id)

  def foreach[C](f: ((String, Def)) => C): Unit = definitions.foreach(f)

  def foldLeft[B](z: B)(op: (B, (String, Def)) => B): B = definitions.foldLeft[B](z)(op)
}
