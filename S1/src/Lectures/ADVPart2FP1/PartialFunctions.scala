package Lectures.ADVPart2FP1

object PartialFunctions extends App {

  //Any Int can be passes and returned a result.
  val aFunction = (x  : Int) => x  +  1 // Function[Int, Int] == Int => Int
  /*
  But sometimes you may want some functions that only accept certain parts of the
  Int Domain, say only values 1, 2 and 5, otherwise the function should not be applicable
  So  we want our function to  not accep  any other values other than in our case 1 to 5

  When you write a function, we cant really restrict what value we can parse to it unless we
  say something like
  */

  val aFussyFunction = (x : Int) => {
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 78
    else throw new FunctionNotApplicableException
  }

  class FunctionNotApplicableException extends RuntimeException


  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 3 => 78
  }

  //the aNicerFussyFunction is a partial function, it restricts the input such that the domain
  //is {1,2,5} Because it accept only a part of the mean  as arguments


  //complete declaration of an official partial function
  val aPartialFunction : PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 3 => 78
  }
  //{content in curly braces} this is the partial function value  and it is equivalent to this
  // (x: Int) => x match {
  //    case 1 => 42
  //    case 2 => 56
  //    case 3 => 78
  //  }

  //This however is a proper function and it cant be assigned to a partial function because it is
  //A total function
  // (x: Int) => x match {
  //     case 1 => 42
  //     case 2 => 56
  //     case 3 => 78
  //   }

  println(aPartialFunction(2))
  //println(aPartialFunction(289))

  //Utilities for partial functions

  //It will test if a partial function can actually be run with these arguments instead of actually applying it with the wrong
  //arguments and crashing the program
  println(aPartialFunction.isDefinedAt(67))

  /*
  Partial functions can be lifted to total functions returning options because if a partial function
  is not defined for a given argument, it will just return none
   */

  val lifted = aPartialFunction.lift // Int => Option[Int]
  println(lifted(2))
  println(lifted(98))

  /*
  If you want to chain multiple partial functions
   */
  val pfChain1 = aPartialFunction.orElse[Int, Int]{
    case 45 => 67
  }

  println(pfChain1(2))
  println(pfChain1(45))

  val pfChain2 = pfChain1.orElse[Int, Int]{
    case 67 => 1
  }

  println(pfChain2(2))
  println(pfChain2(45))
  println(pfChain2(67))


  //HOF's accept partial functions as well
  val aMappedList = List(1,2,3).map{
    case 1 => 1000
    case 2 => 2000
    case 3 => 3000
  }
  println(aMappedList)

  /*Unlike functions which can have multiple parameters, partial functions
  can only have one parameter type. which make sense, how would you pattern match with multiple parameters?
   */

  /*
    1 - construct a PF instance yourself (ananymous class)
    2 - dumb chatbot as PF
   */

  case class Bank(name: String, amount : Int, mode : Int = 0){
    def setMode(num: Int): Bank = Bank(name, amount, num)
    def withdraw(num : Int): Bank = Bank(name, amount - num)
    def deposit(num : Int): Bank = Bank(name, amount + num)
    def status() : Int = amount
  }

  val bank = Bank("Carl", 100)
  val chatBotResponsesP1 : PartialFunction[String, String] = {
    case "Start" => s"Hello ${bank.name}."
    case "Withdraw" =>
      bank.setMode(1)
      s"${bank.name}, How much would you like to withdraw?"
    case "Deposit" =>
      bank.setMode(2)
      s"${bank.name}, How much would you like to deposit?"

    case "Finish" => s"You are welcome ${bank.name}, goodbye."
    case "Status" => s"${bank.name}, You now have ${"$"+bank.status()}."
  }

  val chatBotResponsesP2 = chatBotResponsesP1.orElse[String, String]{
    case "10" => {if (bank.mode == 1) "withdrew" else "deposited"} + " 10 dollars."
    case "20" => {if (bank.mode == 1) "withdrew" else "deposited"} + " 20 dollars"
    case "50" => {if (bank.mode == 1) "withdrew" else "deposited"} + " 50 dollars"
    case "100" => {if (bank.mode == 1) "withdrew" else "deposited"} + " 100 dollars"
  }
  //cannot seem to change Bank outside of the partial function

  print("type Start to begin: ")
//  scala.io.Source.stdin.getLines().foreach(line => println(chatBotResponsesP2(line)))
  scala.io.Source.stdin.getLines().map(chatBotResponsesP2).foreach(println)
}
