package Lectures.FUNDPart1Basics

object StackAndTailRecursion extends App {

  def factorial (number : Int) : Int = {
    if (number < 2) {
      println(s"Computed factorial of $number")
      number
    }
    else {
      println(s"Computing factorial of $number - I first need factorial of ${number - 1}")
      val result = number * factorial(number - 1)
      println(s"Computed factorial of $number")
      result
    }
  }

//  println(factorial(10))

  //stack overflow errors happen when the recursive depth is too big. The stack can blow up with memory.
  //This can be a problem if we are only using recursion as our means to loop

  def newFactorial(number : Int) : BigInt = {
    @scala.annotation.tailrec
    def factorialHelper(x : Int, accumulator: BigInt) : BigInt ={
      if (x <= 1 ) accumulator
      else factorialHelper(x - 1, x * accumulator)
    }
    factorialHelper(number, 1)
  }


//  println(newFactorial(200))


  //so why doesnt this one crash?
  //We wrote factorialHelp right over here as the las expression of its code path. This allows Scala to preserve the smae stack frame
  //and not use additional stack frames for recursive calls. This is called tail recursion. SO the key ingredient of tail recursion
  //is to use a recursive call as the last expression of each code.

  //Any function can be turned into a tail recursive function, the trick here is to use these litter accumulators to store intermediate
  //results rather than call the function recursively

  def concatenateStrings(str : String, n : Int) : String = {
    @scala.annotation.tailrec
    def helper(str: String, x : Int, acc : String): String = {
      if (x == 0) acc
      else helper(str, x - 1, acc + str )
    }
    helper(str, n, "")
  }


  def isPrime(num : Int) : Boolean = {
    @scala.annotation.tailrec
    def helper(n : Int, divisor : Int): Boolean  ={
      if(n % divisor == 0 && divisor > 2){
        false
      }else if(divisor > n / 2 ){
        true
      }else helper(num, divisor + 1)
    }
    helper(num, 2)
  }

  def Fibbonaci(num : Int) : Int = {
    @scala.annotation.tailrec
    def helper(n : Int, x : Int, y : Int) : Int = {
      if (n == 0) x
      else helper(n - 1, x + y, x)
    }
    helper(num, 0, 1)
  }

  println(concatenateStrings("meow", 2))
  println(concatenateStrings("🔥", 5))
  println(isPrime(2))
  println(isPrime(7))
  println(isPrime(10))
  println(isPrime(21))
  println(isPrime(41))
  println(Fibbonaci(0))
  println(Fibbonaci(1))
  println(Fibbonaci(2))
  println(Fibbonaci(3))
  println(Fibbonaci(4))
  println(Fibbonaci(5))
  println(Fibbonaci(6))
  println(Fibbonaci(7))
  println(Fibbonaci(8))
  println(Fibbonaci(9))
  println(Fibbonaci(10))
  println(Fibbonaci(11))





}








