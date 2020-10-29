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

import org.slf4j.LoggerFactory

import scala.util.parsing.combinator.Parsers

/**
  * BytesParsers is a parser combinator over bytes.
  */
trait BytesParsers extends Parsers {
  type Elem = Byte

  implicit def string(s: String): Parser[String] = new Parser[String] {
    override def apply(in: Input): ParseResult[String] = {
      if (s.isEmpty) Success(s, in)
      else {
        val bytesInput = in.asInstanceOf[BytesReader]
        val bytes = bytesInput.bytes
        val offset = bytesInput.offset
        val last = bytes.length

        val arr = s.getBytes("UTF-8")
        val len = arr.length

        var i = offset
        var j = 0

        while (i < last && j < len && bytes(i) == arr(j)) {
          i += 1
          j += 1
        }

        if (j == len) Success(s, bytesInput.drop(len))
        else if (i == last) Error(s"unexpected EOF when trying to read ${s}", bytesInput.drop(last - offset))
        else Failure(s"could not read ${s}", bytesInput)
      }
    }
  }

  implicit def character(c: Char): Parser[Byte] = new Parser[Byte] {
    override def apply(in: Input): ParseResult[Byte] = {
      val bytesInput = in.asInstanceOf[BytesReader]
      val bytes = bytesInput.bytes
      val offset = bytesInput.offset
      val bte = c.toByte

      if (bytes(offset) == bte) Success(bytes(offset), bytesInput.drop(1))
      else Failure(s"could not read ${c}", bytesInput)
    }
  }

  def notIn(exclusion: Set[Byte]): Parser[Array[Byte]] = new Parser[Array[Byte]] {
    override def apply(in: Input): ParseResult[Array[Byte]] = {
      val bytesInput = in.asInstanceOf[BytesReader]
      val bytes = bytesInput.bytes
      val offset = bytesInput.offset
      val blen = bytes.length

      var i = offset
      while (i < blen && !exclusion.contains(bytes(i))) {
        i += 1
      }

      if (i == offset) Success(Array.emptyByteArray, bytesInput)
      else {
        val len = i - offset
        val arr = new Array[Byte](len)
        System.arraycopy(bytes, offset, arr, 0, len)

        Success(arr, bytesInput.drop(len))
      }
    }
  }

  def parse[T](p: Parser[T], in: Array[Byte]): ParseResult[T] =
    p(new BytesReader(in, 0))

  lazy val log = LoggerFactory.getLogger(this.getClass)

  override def log[T](p: => Parser[T])(name: String): Parser[T] = Parser { in =>
    log.debug("trying " + name + " at " + in)
    val r = p(in)
    log.debug(name + " --> " + r)
    r
  }
}
