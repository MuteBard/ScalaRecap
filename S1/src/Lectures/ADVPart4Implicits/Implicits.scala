package Lectures.ADVPart4Implicits

object Implicits extends App{

  val pair = "Carl" -> "555"
  val intPair = 1 -> 2
  //these apparently are methods
  //From what we know about methods so far, we know that operators act as methods in which
  //the first operand is the instance and the second operand is the argument

  //this however is an implicit method or a method of an implicit class.
  //the first item turns into an ArrowAssoc instance then it will call the arrow method on it with the argument
  //and it will return a tuple



  case class Person(name : String){
    def greet = s"Hi my name is $name"
  }

  implicit def fromStringToPerson(str: String) : Person = Person(str)
  println("Peter".greet) //println(fromStringToPerson("Peter").greet) //Hi my name is Peter

  //normally the string class is not ablet to do this and normally it would give up and throw an error
  //but the compiler looks for all implicit classes, object and values and methods that can help in the compilation
  //That  is it looks for anything that can turn this string into something that has a greet method.
  //And it just so happens that we have a person type that has a greet  method and an implicit conversion


  //The compiler assumes that there is only one implicit that matches. If we have multiple, it will give up

//  class Garbage{
//    def greet : Int = 2
//  }
//
//  implicit def fromStringToA(str : String) : Garbage = new Garbage


  //implicit parameters

  def increment(x: Int)(implicit amount: Int) : Int = x + amount
  implicit val defaultAmount = 10
  println(increment(2))

  //this is not the same thing as default arguments because the implicit value here is found by the compiler from its search scope.
  //And then the compiler will take care to pass those values in as other parameter lists
}
