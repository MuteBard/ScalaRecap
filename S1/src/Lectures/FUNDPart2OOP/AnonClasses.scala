package Lectures.FUNDPart2OOP

object AnonClasses extends App{
  abstract class Animal{
    def eat : Unit
  }

  //Anonymous Class
  val funnyAnimal: Animal = new Animal{
    override def eat: Unit = println("HAHAHAHAHAHAH")
  }
  println(funnyAnimal.getClass)

  //equivalent with
//  class AnonClasses$$anon$1 extends Animal{
//    override def eat: Unit = println("HAHAHAHAHAHAH")
//  }
//
//  val funnyAnimal: Animal = new Animal AnonClasses$$anon$1

}
