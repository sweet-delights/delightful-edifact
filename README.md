[![Build Status](https://travis-ci.com/sweet-delights/delightful-edifact.svg?branch=master)](https://travis-ci.com/sweet-delights/delightful-edifact)
[![Maven Central](https://img.shields.io/maven-central/v/org.sweet-delights/delightful-edifact_2.13.svg)](https://maven-badges.herokuapp.com/maven-central/org.sweet-delights/delightful-edifact_2.13)

`delightful-edifact` is a data-binding code generator and library for [EDIFACT](https://en.wikipedia.org/wiki/EDIFACT)
messages, written in [Scala](https://www.scala-lang.org/) . It is inspired by the excellent [`scalaxb`](https://scalaxb.org/)
tool. The parsing itself is implemented with [`scala-parser-combinators`](https://github.com/scala/scala-parser-combinators).

`delightful-edifact` is built for Scala 2.12.12 and 2.13.3. The plugin is tested against SBT 1.3.13.

## Quick Start

### SBT

*Step 1* - in `build.sbt`, add:
```scala
libraryDependencies += "org.sweet-delights" %% "delightful-edifact" % "0.0.1"
```

*Step 2* - in `project/plugins.sbt`, add:
```scala
addSbtPlugin("org.sweet-delights" % "sbt-delightful-edifact" % "0.0.1")
```

*Step 3* - in `src/main/resources/edifact/`, add EDIFACT grammar files

The SBT plugin generates the code in `target/scala-${scalaVersion}/src_managed/main/sbt-delightful-edifact/`

### Maven
```xml
<dependency>
  <groupId>org.sweet-delights</groupId>
  <artifactId>delightful-edifact_2.12</artifactId>
  <version>0.0.1</version>
</dependency>
```

## [License](LICENSE.md)

All files in `delightful-edifact` are under the GNU Lesser General Public License version 3.
Please read files [`COPYING`]("COPYING") and [`COPYING.LESSER`]("COPYING.LESSER") for details.

## Presentation

### What is EDIFACT ?

EDIFACT is an electronic data interchange (EDI) format developed for the [United Nations](https://www.un.org/)
and approved and published by the [UNECE](https://www.unece.org/). For more information, please check the
[UNECE](https://www.unece.org/) site or [wikipedia](https://en.wikipedia.org/wiki/EDIFACT) page.

### What is EDIFACT data-binding ?

EDIFACT data-binding is the process of mapping and filling objects representing the structure of an EDIFACT message. In
the special case of `delightful-edifact`, the objects are Scala case classes and objects.

### What does `delightful-edifact` ?

Firstly, `delightful-edifact` defines a XML Schema Definition (XSD) for describing EDIFACT grammars. Once such a grammar
is available, `delightful-edifact` is able to generate the data-binding case classes and parsers to read EDIFACT messages.
The parsers are based on [`scala-parser-combinators`](https://github.com/scala/scala-parser-combinators).

As of now, `delightful-edifact` does not write data-binding case classes back to EDIFACT. But contributions are welcome
to add this feature!

### How to use `delightful-edifact` ?

Suppose we want to parse the following EDIFACT message (taken from [wikipedia](https://en.wikipedia.org/wiki/EDIFACT)):

```
UNB+IATB:1+6XPPC:ZZ+LHPPC:ZZ+940101:0950+1'
UNH+1+PAORES:93:1:IA'
MSG+1:45'
IFT+3+XYZCOMPANY AVAILABILITY'
ERC+A7V:1:AMD'
IFT+3+NO MORE FLIGHTS'
ODI'
TVL+240493:1000::1220+FRA+JFK+DL+400+C'
PDI++C:3+Y::3+F::1'
APD+74C:0:::6++++++6X'
TVL+240493:1740::2030+JFK+MIA+DL+081+C'
PDI++C:4'
APD+EM2:0:1630::6+++++++DA'
UNT+13+1'
UNZ+1+1'
```

The structure of this message (aka "*the grammar*") is described in a XML file. Example:
[`PAORES_IA_93_1.xml`](src/test/resources/PAORES_IA_93_1.xml).
The XSD for this file is available [here](src/main/resources/xsd/edifact/grammar.xsd).

`delightful-edifact` takes care of generating the case classes and parser combinators from the grammar file.

## Contributing

This project follows the *Typelevel Code of Conduct*, available [here](https://typelevel.org/code-of-conduct.html).

To contribute, please feel free to open an issue and submit pull requests.

## Acknowledgements

- the [`scalaxb`] tool for inspiration and example for SBT plugin code
- the [`scala-parser-combinators`](https://github.com/scala/scala-parser-combinators) library
- the [`monocle`](https://www.optics.dev/Monocle/) library
