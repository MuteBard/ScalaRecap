package Lectures.FUNDPart3FuncProgramming

object WhatsAFunction extends App{

  /*
  The whole purpose of the functional programming section is to use and work with functions
  as first class elements. We want to work with functions as we work with plain values. Thats the dream.
  The problem with that is that we come from an OOP world. ANd when you come from an OOP world,
  everything is an instance of some sort of class. This is how the JVM was originally designed.
  So the only way you could simulate funcitonal programing was to use classes and instances of those
  classes.
  */

  val doubler = new MyFunction[Int, Int] {
    override def apply(element: Int) : Int = element * 2
  }
  println(doubler(2))


//  val adder: (Int, Int) => Int = new Function2[Int, Int, Int] {
//    override def apply(a: Int, b: Int): Int = a + b
//  }

  val adder: (Int, Int) => Int = (a: Int, b: Int) => a + b

  //Function types Function2[Int, Int, Int] === (Int, Int) => Int
  //All scala functions are objects, Or instances of classes deriving from function1 all
  // he way to function22


  /*
    1. a function which takes 2 strings and concats them
   */

  val concat = (a: String, b: String) => a + b
  println(concat("abc", "efg"))

  val concatUGLY : (String, String) => String = new Function2[String, String, String]{
    override def  apply(a: String, b: String) :  String = a + b
  }
}




class Action1 {
  def execute(element: Int) : String = ???
}

trait Action2[A, B] {
  def execute(element: A) : B = ???
}

trait MyFunction[A, B] {
  def apply(element : A) : B
}

