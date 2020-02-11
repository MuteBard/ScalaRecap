package Recap

object AdvancedScalaRecap extends App{

  //Partial Functions
  //functions that only operate on a particular subset of the input domain

  val partialFunction : PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case 5 => 99
  }

  //function only operate on the functions 1,2 and 5 and when anythgin else this parial function will throw an exception

  //another way to write it, because partial functions are based on pattern matching and they extend int => int function
  val pf = (x : Int) => x match {
    case 1 => 42
    case 2 => 65
    case 5 => 99
  }

  //valid assignment
  val function: (Int => Int) = partialFunction

  //collections my operate on partial functions as well
  val modifiedList = List(1,2,3).map {
    case 1 => 42
    case _ => 65
  }

  //turn a partial function into a total function
  val lifted = partialFunction.lift
  lifted(2) // Some(65)
}
