package Lectures.FUNDPart2OOP

object Objects extends App{

  //Scala  objects are a single ton instance, you define its type and its only instance
  object Person {
      val eyes = 2
      def greet : String = "Hi"
      def apply (mother : String, father : String) : Person = { new Person ("Severe")} // its doing this  in the background
  }

  class Person(val name : String) {//but interfacing with you over here
    def apply() : String = name
  }
  //Both  object and class  person are companions, same scope and name
  //This means that all the code that we will ever access  will be from som kind of instanceeither the regular one or
  //singleton one. truly an object oriented language



  println(Person.eyes)
  println(Person.greet)

  //example of how  singleton instaces work
  val x = Person
  val y = Person
  println( x == y)

  val severe = Person("Erin", "Carl")
  println(severe())


//  def main(args : Array[String]) : Unit = {}



}
