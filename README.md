`delightful-edifact` is an EDIFACT data-binding [Scala](https://www.scala-lang.org/) code generator and library, inpired
by the excellent [`scalaxb`](https://scalaxb.org/) tool.

# [License](LICENSE.md)

# Introduction

## What is EDIFACT ?

EDIFACT is an electronic data interchange (EDI) format developed for the [United Nations](https://www.un.org/)
and approved and published by the [UNECE](https://www.unece.org/). For more information, please check the
[UNECE](https://www.unece.org/) site or [wikipedia](https://en.wikipedia.org/wiki/EDIFACT) page.

## What is EDIFACT data-binding ?

EDIFACT data-binding is the process of mapping and filling objects representing the structure of an EDIFACT message. In
the special case of `delightful-edifact`, the objects are case classes and objects.

## What does `delightful-edifact` ?

Firstly, `delightful-edifact` defines a XML Schema Definition (XSD) for describing EDIFACT grammars. Once such a grammar
is available, `delightful-edifact` is able to generate the data-binding case classes and parsers to read EDIFACT messages
that comply with the grammar into these case classes. The parsers are based on
[`scala-parser-combinators`](https://github.com/scala/scala-parser-combinators).

As of now, `delightful-edifact` does not write data-binding case classes back to EDIFACT. But contributions are welcome
to add this feature!

# How to use `delightful-edifact` ?

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

Side note: believe it or not, the above is the most user-friendly readable EDIFACT representation one can get. With time and practice,
it possible to start reading into the Matrix.