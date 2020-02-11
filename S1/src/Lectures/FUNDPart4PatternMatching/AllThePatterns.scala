package Lectures.FUNDPart4PatternMatching

object AllThePatterns extends App {

  // 1 - constants
  val x: Any = "Scala"
  val constants = x match {
    case 1 => "a number"
    case "Scala" => "The Scala"
    case true => "The Truth"
    case AllThePatterns => "A Singleton Object"
  }

  // 2 - match anything
  // 2.1 wildcard

  val matchAnything = x match {
    case _ =>
  }

  //2.2 variable
  val matchVariable = x match{
    case something => s"I've found $something"
  }


  //3 - Tuples
  val aTuple = (1,2)
  val matchATuple = aTuple match {
    case (1, 1) =>
    case (something, 2) => s"I've found $something"
  }

  val nestedTuple = (1, (2 , 3))
  val matchNestedTuple = nestedTuple match {
    case (_ , (2 , v)) => s"I've found $v"
  }

  //4 - Case classes = constructor pattern


  //5 - list patterns
  val aStandardList = List(1,2,3,42)
  val standardListMatching = aStandardList match {
    case List(1, _, _, _) => // extractor - advanced
    case List(1, _*) => // list of arbiterary length - advanced
    case 1 :: List(_) => //Infix pattern
    case List(1,2,3) :+ 42 => // Infix pattern
  }

  //6 - type specifiers
//  val unknown: Any = 2
//  val unknown = unknown match {
//    case list List[int] => //explict type specifier
//    case _ =>
//  }

  //7 - name binding
}
