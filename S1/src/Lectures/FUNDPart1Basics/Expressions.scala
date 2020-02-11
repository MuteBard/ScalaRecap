package Lectures.FUNDPart1Basics

object Expressions extends App {
    val aCondition = true
    val value = if (aCondition) 5 else 3

  if(!aCondition){
    println(4)
  }else{
    println(22)
  }

  println(value)

  //Code blocks in Scala are expresssions. The value of a code block is the value of its last expression
  //If statement blocks are also expressions in scala
  val xc = {
    val condition: Boolean = true
    if (condition) 42 else 0
  }

  //////////////////////////////////////////
  //functions can be given an expression or a code block

  //function evaluating to an expression
  //the return type needs to be explicit if you are using recursion
  def fibonacci (number : Int) : Int =
    if (number <= 2 ) 1
    else  {
      fibonacci(number - 1) + fibonacci(number - 2)
    }

  //function evaluating to a code block which evaluates to an expression
  def factorial (number : Int) : Int = {
    if (number < 2) number
    else number * factorial(number - 1)
  }

  //Instructions are executed, expressions are evaluated
}

