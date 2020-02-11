package Lectures.FUNDPart1Basics

object DefaultArgs extends App{

  def Factorialv1(number : Int) : BigInt = {
    @scala.annotation.tailrec
    def factorialHelper(x : Int, accumulator: BigInt) : BigInt ={
      if (x <= 1 ) accumulator
      else factorialHelper(x - 1, x * accumulator)
    }
    factorialHelper(number, 1)
  }


  @scala.annotation.tailrec
  def Factorialv2(number : Int, accumulator: BigInt = 1) : BigInt = {
      if (number <= 1 ) accumulator
      else Factorialv2(number - 1, number * accumulator)
  }



  @scala.annotation.tailrec
  def FactorialUnderstandingDefaults(number : Int = 5, accumulator: BigInt = 1) : BigInt = {
    if (number <= 1 ) accumulator
    else FactorialUnderstandingDefaults(number - 1, number * accumulator)
  }

  //Just make sure that the trailing values are the default values

  println(Factorialv1(5))
  println(Factorialv2(5))

  //doing this can let you do this in any order
  println(FactorialUnderstandingDefaults(accumulator = 5))
  println(FactorialUnderstandingDefaults(accumulator = 1, number = 5))
}

