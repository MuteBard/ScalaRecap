package Lectures.FUNDPart3FuncProgramming

object AnonFunctions extends App {

  // Object oriented way of defining an anonymous function, damn its so ugly
  val doublerUGLY : (Int) => Int = new Function1[Int, Int]{
    override def apply(x: Int) = x * 2
  }

  // anonymous function (Lambda)
  // (x : Int) => x * 2 is a value, and an instance of function 1
  val doublerv1 = (x : Int) => x * 2
  val doublerv2 : Int => Int = x => x * 2


  val adderv1 = (a: Int, b: Int) => a + b
  val adderv2 :(Int, Int) => Int = (a, b) => a + b
  val adderv3 :(Int, Int) => Int = _ + _


  val doSomethingv1 = () => 3
  val doSomethingv2: () => Int = () => 3

  println(doSomethingv1) // the function itself
  println(doSomethingv1()) // you must call lampda's with parentheses


  val stringToIntv1 = {(str: String) =>
    str.toInt
  }

  val stringToIntv2 = (str: String) => {
    str.toInt
  }

  println(stringToIntv1("100"))
  println(stringToIntv2("200"))


  val niceIncrementerv1 : Int => Int = (x: Int) => x + 1
  val niceIncrementerv2 : Int => Int = _ + 1 // i dont like this


  //nested functions/ Curries
  val superAdd = (x: Int) => (y: Int) => x + y
  println(superAdd(2)(4))

}
