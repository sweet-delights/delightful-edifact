package sweet.delights.edifact

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

import sweet.delights.edifact._

case class PAORES_IA_93_1(
  unb: Option[Segment__UNB],
  ung: Option[Segment__UNG],
  unh: Option[Segment__UNH],
  messageEntry: Option[Segment_S2],
  freeflowText: List[Segment_S1],
  errorOrWarningSection: List[Group_G1],
  singleCityPairInfo: List[Group_G2]
) {
  def isEmpty: Boolean =
    unb.isEmpty &&
      ung.isEmpty &&
      unh.isEmpty &&
      messageEntry.isEmpty &&
      freeflowText.isEmpty &&
      errorOrWarningSection.isEmpty &&
      singleCityPairInfo.isEmpty
}

case class Group_G1(
  errorOrWarningInfo: Segment_S3,
  textInformation: Option[Segment_S1]
)

case class Group_G2(
  boardoff: Segment_S4,
  flightInfo: List[Group_G3]
)

case class Group_G3(
  basicFlightInfo: Segment_S8,
  infoOnClass: Option[Segment_S6],
  flightDetails: Option[Segment_S5],
  flightFreeflowText: Option[Segment_S1],
  trafficRestrictionDetails: Option[Segment_S7],
  flightErrorWarningSection: List[Group_G4]
)

case class Group_G4(
  flightErrorWarningInfo: Segment_S3,
  flightErrorWarningText: Option[Segment_S1]
)

case class Segment__UNB(
  syntax: Composite__UNB_1,
  sender: Composite__UNB_2,
  receiver: Composite__UNB_3,
  preparation: Composite__UNB_4,
  controlReference: Option[String],
  recipientControlReference: Option[String],
  applicationReference: Option[String],
  associationCode: Option[String]
)

case class Segment__UNG(
  messageGroupIdentification: Option[String],
  applicationSenderIdentification: Option[Composite__UNG_2],
  applicationRecipientIdentification: Option[Composite__UNG_3],
  dateAndTimeOfPreparation: Option[Composite__UNG_4],
  groupReferenceNumber: String,
  controllingAgency: Option[String],
  messageVersion: Option[Composite__UNG_7],
  applicationPassword: Option[String]
)

case class Segment__UNH(
  messageReference: String,
  messageIdentifier: Composite__UNH_2,
  transferStatus: Option[String],
  subsetIdentification: Option[String],
  messageImplementation: Option[String],
  scenarioIdentification: Option[String]
)

case class Segment_S5(
  flightDetails: Option[Composite_C4],
  departureStation: Option[Composite_C8],
  arrivalStation: Option[Composite_C8],
  travellerTimeDetails: Option[Composite_C6],
  productFacilities: List[Composite_C7]
) {
  def isEmpty: Boolean =
    flightDetails.isEmpty &&
      departureStation.isEmpty &&
      arrivalStation.isEmpty &&
      travellerTimeDetails.isEmpty &&
      productFacilities.isEmpty
}

case class Segment_S3(
  error: Composite_C3
)

case class Segment_S1(
  freeTextQualification: Option[Composite_C1],
  freeText: List[String]
) {
  def isEmpty: Boolean =
    freeTextQualification.isEmpty &&
      freeText.isEmpty
}

case class Segment_S2(
  functionDetails: Composite_C2,
  responseType: Option[String]
)

case class Segment_S4(
  origin: Option[String],
  destination: Option[String]
) {
  def isEmpty: Boolean =
    origin.isEmpty &&
      destination.isEmpty
}

case class Segment_S6(
  productClassDetail: List[Composite_C9]
) {
  def isEmpty: Boolean =
    productClassDetail.isEmpty
}

case class Segment_S7(
  trafficRestriction: List[Composite_C10]
) {
  def isEmpty: Boolean =
    trafficRestriction.isEmpty
}

case class Segment_S8(
  flightDate: Composite_C14,
  boardPointDetails: Composite_C15,
  offPointDetails: Composite_C15,
  companyDetails: Option[Composite_C11],
  flightIdentification: Composite_C12,
  flightTypeDetails: Option[Composite_C13],
  itemNumber: Option[String]
)

case class Composite__UNB_1(
  identifier: String,
  version: Option[String],
  serviceCode: Option[String],
  encoding: Option[String]
)

case class Composite__UNB_2(
  identifier: String,
  qualifier: Option[String],
  internalIdentification: Option[String],
  internalSubIdentification: Option[String]
)

case class Composite__UNB_3(
  identifier: String,
  qualifier: Option[String],
  internalIdentification: Option[String],
  internalSubIdentification: Option[String]
)

case class Composite__UNB_4(
  date: String,
  time: String
)

case class Composite__UNG_2(
  applicationSenderIdentification: String,
  identificationCodeQualifier: Option[String]
)

case class Composite__UNG_3(
  applicationRecipientIdentification: String,
  identificationCodeQualifier: Option[String]
)

case class Composite__UNG_4(
  date: String,
  time: String
)

case class Composite__UNG_7(
  versionNumber: String,
  releaseNumber: String,
  agencyCode: Option[String]
)

case class Composite__UNH_2(
  tpe: String,
  version: String,
  release: String,
  agency: String,
  code: Option[String],
  codeVersion: Option[String],
  subType: Option[String]
)

case class Composite_C2(
  businessFunction: Option[String],
  actionCode: String,
  additionalActionCode: List[String]
)

case class Composite_C9(
  serviceClass: String,
  availabilityStatus: Option[String],
  modifier: List[String]
)

case class Composite_C11(
  marketingCompany: String,
  operatingCompany: Option[String]
)

case class Composite_C12(
  flightNumber: String,
  operationalSuffix: Option[String]
)

case class Composite_C13(
  flightIndicator: List[String]
) {
  def isEmpty: Boolean =
    flightIndicator.isEmpty
}

case class Composite_C14(
  depDate: String,
  depTime: String,
  arrDate: Option[String],
  arrTime: String,
  dateVariation: Option[String]
)

case class Composite_C4(
  typeOfAircraft: Option[String],
  numberOfStops: Option[String],
  legDuration: Option[String],
  dayOfOperation: Option[String]
) {
  def isEmpty: Boolean =
    typeOfAircraft.isEmpty &&
      numberOfStops.isEmpty &&
      legDuration.isEmpty &&
      dayOfOperation.isEmpty
}

object Composite_C5

case class Composite_C6(
  departureTime: Option[String],
  arrivalTime: Option[String]
) {
  def isEmpty: Boolean =
    departureTime.isEmpty &&
      arrivalTime.isEmpty
}

case class Composite_C7(
  tpe: Option[String]
) {
  def isEmpty: Boolean =
    tpe.isEmpty
}

case class Composite_C15(
  trueLocationId: String
)

case class Composite_C10(
  code: Option[String]
) {
  def isEmpty: Boolean =
    code.isEmpty
}

case class Composite_C1(
  codedIndicator: String,
  typeOfInfo: Option[String],
  isoLanguageCode: Option[String]
)

case class Composite_C8(
  gate: Option[String],
  terminal: Option[String],
  concourseTerminal: Option[String]
) {
  def isEmpty: Boolean =
    gate.isEmpty &&
      terminal.isEmpty &&
      concourseTerminal.isEmpty
}

case class Composite_C3(
  code: String,
  tpe: Option[String],
  listResponsible: Option[String]
)

abstract class EdifactParser extends BytesParsers {

  // TODO remove once scala-parsers-combinators 1.2.0 is released
  // cf. https://github.com/scala/scala-parser-combinators/pull/245
  def repMN[T](p: Parser[T], sep: Parser[Any], m: Int, n: Int): Parser[List[T]] = Parser { in =>
    require(0 <= m && m <= n)
    val required = if (m == 0) success(Nil) else (p ~ repN(m - 1, sep ~> p)).map { case head ~ tail => head :: tail }
    val elems = new ListBuffer[T]

    def continue(in: Input): ParseResult[List[T]] = {
      val p0 = sep ~> p // avoid repeatedly re-evaluating by-name parser
      @tailrec def applyp(in0: Input): ParseResult[List[T]] = p0(in0) match {
        case Success(x, rest) => elems += x; if (elems.length == n) Success(elems.toList, rest) else applyp(rest)
        case e @ Error(_, _) => e // still have to propagate error
        case _ => Success(elems.toList, in0)
      }

      applyp(in)
    }

    required(in) match {
      case Success(x, rest) => elems ++= x; continue(rest)
      case ns: NoSuccess => ns
    }
  }

  def repMN[T](p: Parser[T], m: Int, n: Int): Parser[List[T]] = repMN[T](p, success(()), m, n)

  lazy val quote: Parser[Byte] = '\''.toByte

  lazy val plus: Parser[Byte] = '+'.toByte

  lazy val colon: Parser[Byte] = ':'.toByte

  lazy val star: Parser[Byte] = '*'.toByte

  lazy val parse_Data /*(m: Int, n: Int)*/: Parser[String] = notIn(
    Set[Byte](
      '\''.toByte,
      '+'.toByte,
      ':'.toByte,
      '*'.toByte
    )
  ) ^^ { arr =>
    new String(arr, "UTF-8")
  }

  lazy val parse_PAORES_IA_93_1: Parser[PAORES_IA_93_1] =
    log(parse_Segment__UNB.?)("(S) unb") ~
      log(parse_Segment__UNG.?)("(S) ung") ~
      log(parse_Segment__UNH.?)("(S) unh") ~
      log(parse_Segment_S2.?)("(S) messageEntry") ~
      log(repMN(parse_Segment_S1, 0, 2))("(S) freeflowText") ~
      log(repMN(parse_Group_G1, 0, 5))("(G) errorOrWarningSection") ~
      log(repMN(parse_Group_G2, 0, 2))("(G) singleCityPairInfo") ^^ {
      case unb ~ ung ~ unh ~ messageEntry ~ freeflowText ~ errorOrWarningSection ~ singleCityPairInfo =>
        PAORES_IA_93_1(
          unb,
          ung,
          unh,
          messageEntry,
          freeflowText.filterNot(_.isEmpty),
          errorOrWarningSection,
          singleCityPairInfo
        )
    }

  lazy val parse_Group_G1: Parser[Group_G1] =
    log(parse_Segment_S3)("(S) errorOrWarningInfo") ~
      log(parse_Segment_S1.?)("(S) textInformation") ^^ {
      case errorOrWarningInfo ~ textInformation =>
        Group_G1(
          errorOrWarningInfo,
          textInformation.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Group_G2: Parser[Group_G2] =
    log(parse_Segment_S4)("(S) boardoff") ~
      log(repMN(parse_Group_G3, 0, 99))("(G) flightInfo") ^^ {
      case boardoff ~ flightInfo =>
        Group_G2(
          boardoff,
          flightInfo
        )
    }

  lazy val parse_Group_G3: Parser[Group_G3] =
    log(parse_Segment_S8)("(S) basicFlightInfo") ~
      log(parse_Segment_S6.?)("(S) infoOnClass") ~
      log(parse_Segment_S5.?)("(S) flightDetails") ~
      log(parse_Segment_S1.?)("(S) flightFreeflowText") ~
      log(parse_Segment_S7.?)("(S) trafficRestrictionDetails") ~
      log(repMN(parse_Group_G4, 0, 5))("(G) flightErrorWarningSection") ^^ {
      case basicFlightInfo ~ infoOnClass ~ flightDetails ~ flightFreeflowText ~ trafficRestrictionDetails ~ flightErrorWarningSection =>
        Group_G3(
          basicFlightInfo,
          infoOnClass.filterNot(_.isEmpty),
          flightDetails.filterNot(_.isEmpty),
          flightFreeflowText.filterNot(_.isEmpty),
          trafficRestrictionDetails.filterNot(_.isEmpty),
          flightErrorWarningSection
        )
    }

  lazy val parse_Group_G4: Parser[Group_G4] =
    log(parse_Segment_S3)("(S) flightErrorWarningInfo") ~
      log(parse_Segment_S1.?)("(S) flightErrorWarningText") ^^ {
      case flightErrorWarningInfo ~ flightErrorWarningText =>
        Group_G4(
          flightErrorWarningInfo,
          flightErrorWarningText.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Segment__UNB: Parser[Segment__UNB] = "UNB" ~>
    log((plus ~> parse_Composite__UNB_1))("(C) syntax") ~
    log((plus ~> parse_Composite__UNB_2))("(C) sender") ~
    log((plus ~> parse_Composite__UNB_3))("(C) receiver") ~
    log((plus ~> parse_Composite__UNB_4))("(C) preparation") ~
    log((plus ~> parse_Data).?)("(D) controlReference") ~
    log((plus ~> parse_Data).?)("(D) recipientControlReference") ~
    log((plus ~> parse_Data).?)("(D) applicationReference") ~
    log((plus ~> parse_Data).?)("(D) associationCode") <~ quote ^^ {
    case syntax ~ sender ~ receiver ~ preparation ~ controlReference ~ recipientControlReference ~ applicationReference ~ associationCode =>
      Segment__UNB(
        syntax,
        sender,
        receiver,
        preparation,
        controlReference.filterNot(_.isEmpty),
        recipientControlReference.filterNot(_.isEmpty),
        applicationReference.filterNot(_.isEmpty),
        associationCode.filterNot(_.isEmpty)
      )
  }

  lazy val parse_Segment__UNG: Parser[Segment__UNG] = "UNG" ~>
    log((plus ~> parse_Data).?)("(D) messageGroupIdentification") ~
    log((plus ~> parse_Composite__UNG_2).?)("(C) applicationSenderIdentification") ~
    log((plus ~> parse_Composite__UNG_3).?)("(C) applicationRecipientIdentification") ~
    log((plus ~> parse_Composite__UNG_4).?)("(C) dateAndTimeOfPreparation") ~
    log((plus ~> parse_Data))("(D) groupReferenceNumber") ~
    log((plus ~> parse_Data).?)("(D) controllingAgency") ~
    log((plus ~> parse_Composite__UNG_7).?)("(C) messageVersion") ~
    log((plus ~> parse_Data).?)("(D) applicationPassword") <~ quote ^^ {
    case messageGroupIdentification ~ applicationSenderIdentification ~ applicationRecipientIdentification ~ dateAndTimeOfPreparation ~ groupReferenceNumber ~ controllingAgency ~ messageVersion ~ applicationPassword =>
      Segment__UNG(
        messageGroupIdentification.filterNot(_.isEmpty),
        applicationSenderIdentification,
        applicationRecipientIdentification,
        dateAndTimeOfPreparation,
        groupReferenceNumber,
        controllingAgency.filterNot(_.isEmpty),
        messageVersion,
        applicationPassword.filterNot(_.isEmpty)
      )
  }

  lazy val parse_Segment__UNH: Parser[Segment__UNH] = "UNH" ~>
    log((plus ~> parse_Data))("(D) messageReference") ~
    log((plus ~> parse_Composite__UNH_2))("(C) messageIdentifier") ~
    log((plus ~> parse_Data).?)("(D) transferStatus") ~
    log((plus ~> parse_Data).?)("(D) subsetIdentification") ~
    log((plus ~> parse_Data).?)("(D) messageImplementation") ~
    log((plus ~> parse_Data).?)("(D) scenarioIdentification") <~ quote ^^ {
    case messageReference ~ messageIdentifier ~ transferStatus ~ subsetIdentification ~ messageImplementation ~ scenarioIdentification =>
      Segment__UNH(
        messageReference,
        messageIdentifier,
        transferStatus.filterNot(_.isEmpty),
        subsetIdentification.filterNot(_.isEmpty),
        messageImplementation.filterNot(_.isEmpty),
        scenarioIdentification.filterNot(_.isEmpty)
      )
  }

  lazy val parse_Segment_S5: Parser[Segment_S5] = "APD" ~>
    log((plus ~> parse_Composite_C4).?)("(C) flightDetails") ~
    log((plus ~> parse_Composite_C8).?)("(C) departureStation") ~
    log((plus ~> parse_Composite_C8).?)("(C) arrivalStation") ~
    log((plus ~> parse_Composite_C5).?)("(C) milageDetails") ~
    log((plus ~> parse_Composite_C6).?)("(C) travellerTimeDetails") ~
    log((plus ~> parse_Composite_C7).?)("(C) ") ~
    log((plus ~> parse_Composite_C7).?)("(C) ") ~
    log(repMN((plus ~> parse_Composite_C7.?), 0, 10))("(C) productFacilities") <~ quote ^^ {
    case flightDetails ~ departureStation ~ arrivalStation ~ _ ~ travellerTimeDetails ~ _ ~ _ ~ productFacilities =>
      Segment_S5(
        flightDetails.filterNot(_.isEmpty),
        departureStation.filterNot(_.isEmpty),
        arrivalStation.filterNot(_.isEmpty),
        travellerTimeDetails.filterNot(_.isEmpty),
        productFacilities.flatten.reverse.dropWhile(_.isEmpty).reverse
      )
  }

  lazy val parse_Segment_S3: Parser[Segment_S3] = "ERC" ~>
    log((plus ~> parse_Composite_C3))("(C) error") <~ quote ^^ {
    case error =>
      Segment_S3(
        error
      )
  }

  lazy val parse_Segment_S1: Parser[Segment_S1] = "IFT" ~>
    log((plus ~> parse_Composite_C1).?)("(C) freeTextQualification") ~
    log(repMN((plus ~> parse_Data).?, 0, 99))("(D) freeText") <~ quote ^^ {
    case freeTextQualification ~ freeText =>
      Segment_S1(
        freeTextQualification,
        freeText.flatten.reverse.dropWhile(_.trim.isEmpty).reverse
      )
  }

  lazy val parse_Segment_S2: Parser[Segment_S2] = "MSG" ~>
    log((plus ~> parse_Composite_C2))("(C) functionDetails") ~
    log((plus ~> parse_Data).?)("(D) responseType") <~ quote ^^ {
    case functionDetails ~ responseType =>
      Segment_S2(
        functionDetails,
        responseType.filterNot(_.isEmpty)
      )
  }

  lazy val parse_Segment_S4: Parser[Segment_S4] = "ODI" ~>
    log((plus ~> parse_Data).?)("(D) origin") ~
    log((plus ~> parse_Data).?)("(D) destination") <~ quote ^^ {
    case origin ~ destination =>
      Segment_S4(
        origin.filterNot(_.isEmpty),
        destination.filterNot(_.isEmpty)
      )
  }

  lazy val parse_Segment_S6: Parser[Segment_S6] = "PDI" ~>
    log((plus ~> parse_Data).?)("(D) ") ~
    log(repMN((plus ~> parse_Composite_C9), 1, 26))("(C) productClassDetail") <~ quote ^^ {
    case _ ~ productClassDetail =>
      Segment_S6(
        productClassDetail
      )
  }

  lazy val parse_Segment_S7: Parser[Segment_S7] = "TRF" ~>
    log(repMN((plus ~> parse_Composite_C10.?), 0, 5))("(C) trafficRestriction") <~ quote ^^ {
    case trafficRestriction =>
      Segment_S7(
        trafficRestriction.flatten.reverse.dropWhile(_.isEmpty).reverse
      )
  }

  lazy val parse_Segment_S8: Parser[Segment_S8] = "TVL" ~>
    log((plus ~> parse_Composite_C14))("(C) flightDate") ~
    log((plus ~> parse_Composite_C15))("(C) boardPointDetails") ~
    log((plus ~> parse_Composite_C15))("(C) offPointDetails") ~
    log((plus ~> parse_Composite_C11).?)("(C) companyDetails") ~
    log((plus ~> parse_Composite_C12))("(C) flightIdentification") ~
    log((plus ~> parse_Composite_C13).?)("(C) flightTypeDetails") ~
    log((plus ~> parse_Data).?)("(D) itemNumber") <~ quote ^^ {
    case flightDate ~ boardPointDetails ~ offPointDetails ~ companyDetails ~ flightIdentification ~ flightTypeDetails ~ itemNumber =>
      Segment_S8(
        flightDate,
        boardPointDetails,
        offPointDetails,
        companyDetails,
        flightIdentification,
        flightTypeDetails.filterNot(_.isEmpty),
        itemNumber.filterNot(_.isEmpty)
      )
  }

  lazy val parse_Composite__UNB_1: Parser[Composite__UNB_1] =
    log(parse_Data)("(D) identifier") ~
      log((colon ~> parse_Data).?)("(D) version") ~
      log((colon ~> parse_Data).?)("(D) serviceCode") ~
      log((colon ~> parse_Data).?)("(D) encoding") ^^ {
      case identifier ~ version ~ serviceCode ~ encoding =>
        Composite__UNB_1(
          identifier,
          version.filterNot(_.isEmpty),
          serviceCode.filterNot(_.isEmpty),
          encoding.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite__UNB_2: Parser[Composite__UNB_2] =
    log(parse_Data)("(D) identifier") ~
      log((colon ~> parse_Data).?)("(D) qualifier") ~
      log((colon ~> parse_Data).?)("(D) internalIdentification") ~
      log((colon ~> parse_Data).?)("(D) internalSubIdentification") ^^ {
      case identifier ~ qualifier ~ internalIdentification ~ internalSubIdentification =>
        Composite__UNB_2(
          identifier,
          qualifier.filterNot(_.isEmpty),
          internalIdentification.filterNot(_.isEmpty),
          internalSubIdentification.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite__UNB_3: Parser[Composite__UNB_3] =
    log(parse_Data)("(D) identifier") ~
      log((colon ~> parse_Data).?)("(D) qualifier") ~
      log((colon ~> parse_Data).?)("(D) internalIdentification") ~
      log((colon ~> parse_Data).?)("(D) internalSubIdentification") ^^ {
      case identifier ~ qualifier ~ internalIdentification ~ internalSubIdentification =>
        Composite__UNB_3(
          identifier,
          qualifier.filterNot(_.isEmpty),
          internalIdentification.filterNot(_.isEmpty),
          internalSubIdentification.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite__UNB_4: Parser[Composite__UNB_4] =
    log(parse_Data)("(D) date") ~
      log((colon ~> parse_Data))("(D) time") ^^ {
      case date ~ time =>
        Composite__UNB_4(
          date,
          time
        )
    }

  lazy val parse_Composite__UNG_2: Parser[Composite__UNG_2] =
    log(parse_Data)("(D) applicationSenderIdentification") ~
      log((colon ~> parse_Data).?)("(D) identificationCodeQualifier") ^^ {
      case applicationSenderIdentification ~ identificationCodeQualifier =>
        Composite__UNG_2(
          applicationSenderIdentification,
          identificationCodeQualifier.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite__UNG_3: Parser[Composite__UNG_3] =
    log(parse_Data)("(D) applicationRecipientIdentification") ~
      log((colon ~> parse_Data).?)("(D) identificationCodeQualifier") ^^ {
      case applicationRecipientIdentification ~ identificationCodeQualifier =>
        Composite__UNG_3(
          applicationRecipientIdentification,
          identificationCodeQualifier.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite__UNG_4: Parser[Composite__UNG_4] =
    log(parse_Data)("(D) date") ~
      log((colon ~> parse_Data))("(D) time") ^^ {
      case date ~ time =>
        Composite__UNG_4(
          date,
          time
        )
    }

  lazy val parse_Composite__UNG_7: Parser[Composite__UNG_7] =
    log(parse_Data)("(D) versionNumber") ~
      log((colon ~> parse_Data))("(D) releaseNumber") ~
      log((colon ~> parse_Data).?)("(D) agencyCode") ^^ {
      case versionNumber ~ releaseNumber ~ agencyCode =>
        Composite__UNG_7(
          versionNumber,
          releaseNumber,
          agencyCode.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite__UNH_2: Parser[Composite__UNH_2] =
    log(parse_Data)("(D) tpe") ~
      log((colon ~> parse_Data))("(D) version") ~
      log((colon ~> parse_Data))("(D) release") ~
      log((colon ~> parse_Data))("(D) agency") ~
      log((colon ~> parse_Data).?)("(D) code") ~
      log((colon ~> parse_Data).?)("(D) codeVersion") ~
      log((colon ~> parse_Data).?)("(D) subType") ^^ {
      case tpe ~ version ~ release ~ agency ~ code ~ codeVersion ~ subType =>
        Composite__UNH_2(
          tpe,
          version,
          release,
          agency,
          code.filterNot(_.isEmpty),
          codeVersion.filterNot(_.isEmpty),
          subType.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C2: Parser[Composite_C2] =
    log(parse_Data.?)("(D) businessFunction") ~
      log((colon ~> parse_Data))("(D) actionCode") ~
      log((colon ~> parse_Data).?)("(D) ") ~
      log(repMN((colon ~> parse_Data).?, 0, 9))("(D) additionalActionCode") ^^ {
      case businessFunction ~ actionCode ~ _ ~ additionalActionCode =>
        Composite_C2(
          businessFunction.filterNot(_.isEmpty),
          actionCode,
          additionalActionCode.flatten.reverse.dropWhile(_.trim.isEmpty).reverse
        )
    }

  lazy val parse_Composite_C9: Parser[Composite_C9] =
    log(parse_Data)("(D) serviceClass") ~
      log((colon ~> parse_Data).?)("(D) availabilityStatus") ~
      log((colon ~> parse_Data).?)("(D) ") ~
      log(repMN((colon ~> parse_Data).?, 0, 2))("(D) modifier") ^^ {
      case serviceClass ~ availabilityStatus ~ _ ~ modifier =>
        Composite_C9(
          serviceClass,
          availabilityStatus.filterNot(_.isEmpty),
          modifier.flatten.reverse.dropWhile(_.trim.isEmpty).reverse
        )
    }

  lazy val parse_Composite_C11: Parser[Composite_C11] =
    log(parse_Data)("(D) marketingCompany") ~
      log((colon ~> parse_Data).?)("(D) operatingCompany") ^^ {
      case marketingCompany ~ operatingCompany =>
        Composite_C11(
          marketingCompany,
          operatingCompany.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C12: Parser[Composite_C12] =
    log(parse_Data)("(D) flightNumber") ~
      log((colon ~> parse_Data).?)("(D) bookingClass") ~
      log((colon ~> parse_Data).?)("(D) operationalSuffix") ^^ {
      case flightNumber ~ _ ~ operationalSuffix =>
        Composite_C12(
          flightNumber,
          operationalSuffix.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C13: Parser[Composite_C13] =
    log(repMN(parse_Data.?, 0, 9))("(D) flightIndicator") ^^ {
      case flightIndicator =>
        Composite_C13(
          flightIndicator.flatten.reverse.dropWhile(_.trim.isEmpty).reverse
        )
    }

  lazy val parse_Composite_C14: Parser[Composite_C14] =
    log(parse_Data)("(D) depDate") ~
      log((colon ~> parse_Data))("(D) depTime") ~
      log((colon ~> parse_Data).?)("(D) arrDate") ~
      log((colon ~> parse_Data))("(D) arrTime") ~
      log((colon ~> parse_Data).?)("(D) dateVariation") ^^ {
      case depDate ~ depTime ~ arrDate ~ arrTime ~ dateVariation =>
        Composite_C14(
          depDate,
          depTime,
          arrDate.filterNot(_.isEmpty),
          arrTime,
          dateVariation.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C4: Parser[Composite_C4] =
    log(parse_Data.?)("(D) typeOfAircraft") ~
      log((colon ~> parse_Data).?)("(D) numberOfStops") ~
      log((colon ~> parse_Data).?)("(D) legDuration") ~
      log((colon ~> parse_Data).?)("(D) onTimePercentage") ~
      log((colon ~> parse_Data).?)("(D) dayOfOperation") ^^ {
      case typeOfAircraft ~ numberOfStops ~ legDuration ~ _ ~ dayOfOperation =>
        Composite_C4(
          typeOfAircraft.filterNot(_.isEmpty),
          numberOfStops.filterNot(_.isEmpty),
          legDuration.filterNot(_.isEmpty),
          dayOfOperation.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C5: Parser[Composite_C5.type] = success(Composite_C5)

  lazy val parse_Composite_C6: Parser[Composite_C6] =
    log(parse_Data.?)("(D) departureTime") ~
      log((colon ~> parse_Data).?)("(D) arrivalTime") ^^ {
      case departureTime ~ arrivalTime =>
        Composite_C6(
          departureTime.filterNot(_.isEmpty),
          arrivalTime.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C7: Parser[Composite_C7] =
    log(parse_Data.?)("(D) tpe") ^^ {
      case tpe =>
        Composite_C7(
          tpe.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C15: Parser[Composite_C15] =
    log(parse_Data)("(D) trueLocationId") ^^ {
      case trueLocationId =>
        Composite_C15(
          trueLocationId
        )
    }

  lazy val parse_Composite_C10: Parser[Composite_C10] =
    log(parse_Data.?)("(D) code") ^^ {
      case code =>
        Composite_C10(
          code.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C1: Parser[Composite_C1] =
    log(parse_Data)("(D) codedIndicator") ~
      log((colon ~> parse_Data).?)("(D) typeOfInfo") ~
      log((colon ~> parse_Data).?)("(D) ") ~
      log((colon ~> parse_Data).?)("(D) ") ~
      log((colon ~> parse_Data).?)("(D) isoLanguageCode") ^^ {
      case codedIndicator ~ typeOfInfo ~ _ ~ _ ~ isoLanguageCode =>
        Composite_C1(
          codedIndicator,
          typeOfInfo.filterNot(_.isEmpty),
          isoLanguageCode.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C8: Parser[Composite_C8] =
    log(parse_Data.?)("(D) gate") ~
      log((colon ~> parse_Data).?)("(D) terminal") ~
      log((colon ~> parse_Data).?)("(D) concourseTerminal") ^^ {
      case gate ~ terminal ~ concourseTerminal =>
        Composite_C8(
          gate.filterNot(_.isEmpty),
          terminal.filterNot(_.isEmpty),
          concourseTerminal.filterNot(_.isEmpty)
        )
    }

  lazy val parse_Composite_C3: Parser[Composite_C3] =
    log(parse_Data)("(D) code") ~
      log((colon ~> parse_Data).?)("(D) tpe") ~
      log((colon ~> parse_Data).?)("(D) listResponsible") ^^ {
      case code ~ tpe ~ listResponsible =>
        Composite_C3(
          code,
          tpe.filterNot(_.isEmpty),
          listResponsible.filterNot(_.isEmpty)
        )
    }

}

object EdifactParser extends EdifactParser {

  def parse_PAORES_IA_93_1(bytes: Array[Byte]): ParseResult[PAORES_IA_93_1] = parse(parse_PAORES_IA_93_1, bytes)

}
