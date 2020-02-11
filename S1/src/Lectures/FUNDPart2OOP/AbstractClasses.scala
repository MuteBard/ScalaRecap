package Lectures.FUNDPart2OOP

object AbstractClasses extends App{
  //abstract members classses which contain unimplemented or abstract fields or methods
  //cannot be instansiated

  abstract class Animal{
    val creatureType : String
    def eat : Unit
  }

  trait Carnivore {
    def eat (animal : Animal) : Unit
    val prefferedMeal : String = "fresh meat"
  }

 class Crocodile extends Animal with Carnivore {
    val creatureType: String = ""
    def eat: Unit = println("nomnom")
    def eat(animal: Animal): Unit = println("munch munch")
  }

  //traits vs abstract
  //1 - traits do not have constructor parameters
  //2 - you can only extend one class but you can extend multiple traits

  //3 - abstract classess = decribes a thing
  //    traits = behavior and what they do


}
