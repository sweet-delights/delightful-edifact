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

import scala.util.parsing.input.{Position, Reader}

/**
  * Reader for bytes.
  *
  * @param bytes an array of bytes
  * @param offset an index from where to start
  */
class BytesReader(val bytes: Array[Byte], override val offset: Int) extends Reader[Byte] {
  override def first: Byte = if (offset < bytes.length) bytes(offset) else throw new IndexOutOfBoundsException

  override def rest: Reader[Byte] = new BytesReader(bytes, offset + 1)

  override def pos: Position = new Position {
    override def line: Int = 1

    override def column: Int = offset

    override protected def lineContents: String = "<not available>"
  }

  override def drop(n: Int): Reader[Byte] = new BytesReader(bytes, offset + n)

  override def atEnd: Boolean = offset >= bytes.length
}
