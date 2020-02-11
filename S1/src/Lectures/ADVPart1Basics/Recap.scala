package Lectures.ADVPart1Basics

class Recap extends App {
  val aCondition: Boolean = false
  val aConditionedVal = if (aCondition) 43 else 65

  //code blocks, the compiler infers types for us
  val aCodeBlock = {
    if (aCondition) 54
    56
  }

  //unit = void
  val theUnit: Unit = println("hi")

  //functions
  def aFunction(x: Int): Int = x + 1

  //recursion: stack and tail
  def factorial(n: Int, acc : Int ): Int ={
    if(n <= 0) acc
    else factorial(n - 1, acc * n)
  }

  class Animal
  class Dog extends Animal
  val doggo: Animal = new Dog //OOP polymorphism by subtyping, whats the benefit of this?

  //Abstract data types and traits
  /*
  Traits are like interfaces, they can have method definitions that must be implemented within
  in whoever is subclasses
   */
  trait Carnivore{
    def eat(a: Animal): Unit
  }
  class Crocodile extends Animal with Carnivore{
    override def eat(a: Animal): Unit = println("Crunch")
  }

  //method notations
  val aCrock = new Crocodile
  aCrock eat doggo // aCrock.eat(doggo)
  //+ is a method

  //Anonymous Classes
  val aCarnivore = new Carnivore{
    override def eat(a: Animal):Unit = println("Munch Munch")
  }

  //Generics
  abstract class MyList[+A]// variance versions

  //Singleton Objects and companions
  object MyList

  //Case classes
  case class Person(name: String, age : Int)

  //exceptions and try/catch/finally
  val throwsExceptions = throw new RuntimeException //nothing Type
  val aPotentialFailure = try {
    throwsExceptions
  }catch {
    case e: Exception => "got caught"
  } finally{
    println("some logs")
  }

  //funtional programming
  /*
  We stated functional programming with a littel bit of bias from
  the object oriented world by discussing what a function actually is.
  Because we know that the applied methods in scala is so special in how it
  allows instances and singleton objects to be called like they were functions,
  we actually found that their functions were actually instances of classes with
  apply methods
   */

  val incrementer = new Function1[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }

  val anonIncrementer = (x : Int) => x + 1

  /*
  Functional programming is all about working with functions as first class
  elements
   */

  List(1,2,3).map(anonIncrementer)

  /*I can put anonIncrementer here  and use this functino as
  a parameter to another function and map. we call that a higher order
  function
   */

  //for-comprehension

  val pairs = for{
    num <- List(1,2,3)
    char <- List("a","b","c")
  } yield s"$num $char"

  //Scala collections: Seqs, Arrays, Lists, vectors, Maps, Tuples
  val aMap = Map(
    "Carl" -> 123,
    "Erin" -> 124
  )

  //collections : Options, Try
  val anOption = Some(2)


  //pattern matching
  val x = 2
  val order = x match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => s"${x}th"
  }
}



