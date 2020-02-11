package Lectures.FUNDPart4PatternMatching

import scala.util.Random

object PatternMatching extends App {
  //switch on steroids
  val random = new Random
  val x = random.nextInt(10)

  //pattern matching
  val description = x match {
    case 1 => "A"
    case 2 => "B"
    case 3 => "C"
    case _ => "OOF"
  }

  //it looks like a switch but its  much more powerful

  println(x)
  println(description)


  //1) Decomposing values
  //One of the interesting properties of pattern matching is that it can be used to decompose values
  //especially in conjunction with case classes. Case classes have the ability to be deconstructed or
  //extracted in pattern matching

  case class Person(name : String, age : Int)
  val bob = Person("Bob", 20)

  //instead of writing a case, im gonna add in a pattern
  val greeting = bob match {
    case Person(n, a) => s"Hi my name is $n and I am $a years old"
    case _ => "Idk who I am"
  }

  /*
  So if Bob is a person, this pattern match expression is able to deconstruct Bob into its constituent parts.
  So it is able to extract the name and age out of Bob. Even tho the pattern matching expression doesnt know
  them beforehand. It can extract them and it can use them in the returned expression

  This is amazing, we can not only pattern match against any kind of value that we want, we can pattern match against a
  case class pattern and extract the values out of an object , out of an instance of a case class

  we can also add "guards"
  */

  val greeting2 = bob match {
    case Person(n, a) if a > 50 => s"Hi my name is $n and I am $a years old, im hella old"
    case Person(n, a) => s"Hi my name is $n and I am $a years old"
    case _ => "Idk who I am"
  }

  /*
  1) Cases are matched in order
  2) Compiler will try to unify the types for you
  3) works really well with case classes
   */


  //all case classes under a sealed class must be exhausted
  sealed class Animal
  case class Cat(breed : String) extends Animal
  case class Dog(breed : String) extends Animal

  val animal : Animal = Cat("Domestic long hair")
  animal match {
    case Dog(someBreed) => println(s"matched a dog of $someBreed")
    case Cat(someBreed) => println(s"matched a cat of $someBreed")
    case _ => println("Unknown animal")
  }


  trait Expr
  case class Number(n: Int) extends Expr
  case class Sum(e1 : Expr, e2 : Expr) extends Expr
  case class Prod(e1 : Expr, e2 : Expr) extends Expr

  val expr : Expr = Prod(Sum(Number(3), Number(4)), Prod(Number(9), Number(8)))
  def resolve (e :  Expr)  :  String = e match {
    case Number(n) => s"$n"
    case Sum(n1, n2) => s"${(resolve(n1))} + ${resolve(n2)}"
    case Prod(n1, n2) => {
      def showParentheses (ex : Expr) : String =  ex match {
        case Prod(_, _) => resolve(ex)+""
        case Number(_) => resolve(ex)+""
        case _ => s"(${resolve(ex)})"
      }
      s"${showParentheses(n1)} * ${showParentheses(n2)}"
    }
    case _ => "none"
  }

  println(resolve(expr))
}
