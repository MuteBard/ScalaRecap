package Lectures.FUNDPart1Basics

object CBNvsCBV extends App{

  def calledByValue(x : Long): Unit = {
    println(s"by value: $x")
    println(s"by value: $x")
  }

  // => Tells the compiler that the function will be called by Name
  def calledByName(x : => Long): Unit = {
    println(s"by value: $x")
    println(s"by value: $x")
  }

  //in the by value call, the exact value of this expression is computed before the function evaluates.
  //The parameter passed to the function is actually used inside the function definition. The  value is computed  before the funciton is called
  //and the value is used throught the entire functino in addition no matter how many time the parameter x is used

  calledByValue(System.nanoTime())
  //in the by name call, It delays the evaluation of the expression passed as a parameter and its computed literally everytime its used in the  function definition
  //The parameter that  I pass in is not the value but the expression itself literally is passed in to the function with a call by  name.
  //The expression is passed as is and evaluated  at every  use within the function definition.
  calledByName(System.nanoTime())

  //Again, the by name parameter delays the evaluation of the expression until it is used

  def infinite() : Int = 1 + infinite();
  def printFirst(x : Int, y : => Int) = println(x)

  printFirst(34 , infinite())
  printFirst(infinite(), 34)
}
