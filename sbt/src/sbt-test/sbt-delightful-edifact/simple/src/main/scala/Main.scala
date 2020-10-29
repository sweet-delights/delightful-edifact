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
import hello.world.EdifactParser

object Usage extends App {
  val paores =
    """UNB+IATB:1+6XPPC:ZZ+LHPPC:ZZ+940101:0950+1'
      |UNH+1+PAORES:93:1:IA'
      |MSG+1:45'
      |IFT+3+XYZCOMPANY AVAILABILITY'
      |ERC+A7V:1:AMD'
      |IFT+3+NO MORE FLIGHTS'
      |ODI'
      |TVL+240493:1000::1220+FRA+JFK+DL+400+C'
      |PDI++C:3+Y::3+F::1'
      |APD+74C:0:::6++++++6X'
      |TVL+240493:1740::2030+JFK+MIA+DL+081+C'
      |PDI++C:4'
      |APD+EM2:0:1630::6+++++++DA'
      |UNT+13+1'
      |UNZ+1+1'""".stripMargin.replaceAll("\n", "")

  val parsed = EdifactParser.parse_PAORES_IA_93_1(paores.getBytes("UTF-8")) match {
    case EdifactParser.Success(iflirr, _) => Some(iflirr)
    case EdifactParser.Failure(_, _) => None
    case EdifactParser.Error(_, _) => None
  }
  
  assert(parsed.isDefined)
}
