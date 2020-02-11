package Lectures.FUNDPart3FuncProgramming

object HOFCurries extends App{

  //okay, whats stopping me from doing this?
  val superFunction: (Int, (String, (Int => Boolean)) => Int) => (Int => Int) = null

  //Anything that takes a function as a parameter or gives a function as a result is a HOF
  //map, flatMao, filter

  //function that applies a function n times over a value x
  //nTimes(f, n, x)
  //nTimes(f, n, x) = f(f(f(x))

  @scala.annotation.tailrec
  def nTimes(f: Int => Int, n :Int, x: Int) : Int = {
    if(n <= 0) x
    else nTimes(f, n-1, f(x))
  }

  val plusOne = (x : Int) => x + 1


  println(nTimes(plusOne, 10, 1))


  def nTimesBetter(f : Int => Int, n : Int) : (Int => Int) = {
    if(n <= 0) (x : Int) => x
    else (x : Int) => nTimesBetter(f, n-1)(f(x))//curried
  }

  val plus10 = nTimesBetter(plusOne, 10)
  println(plus10(2))

//  Currying = functions with multiple parameter lists
//  def curriedFormatter(a: Int, b :Int)(c: String): String

  def  toCurry (f : (Int, Int) => Int): (Int => Int => Int) = {
    x => y => f (x, y)
  }

  def fromCurry (f: (Int => Int => Int)) : ((Int, Int) => Int) = {
    (x , y) => f(x)(y)
  }

  //  A currying function is a function which could accept a fewer number of parameters than are declared,
  //   then it returns a function with unused parameters. This definition is totally weird.

  def concatenatorv1(w1 : String):(String => String) = w2 => w1 +" "+ w2
  def concatenatorv2(w1 : String):(String => String) = w2 => s"{$w1 $w2}"
/*
  What’s going on in the string above? Well, there is declared concatenator function.
  It accepts w1 argument of String type. It returns another function of
  String => String type. Moreover the returning function has its own body w2 => w1 +" "+ w2.

 */

}
