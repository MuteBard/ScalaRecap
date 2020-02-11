package Lectures.FUNDPart2OOP

object Inheritance extends App{


  /*
  Scala has single class inheritance much like other languages
   */

  class Animal{
    def eat = println("nomnonm")
  }

  class Cat{

  }

//  //inheritance and modifiers
//  sealed class Animal {
//    protected def eat = println("nomnom")
//  }
//
//  class Cat extends Animal{
//    def crunch =  {eat}
//  }
//
//  //constructors and inheretance
//  class Person(val name :String, val age : Int){
//
//  }
//
//  class Adult(override val name :String, override val age : Int, val ID : Int) extends Person(name, age){
//
//  }
//
//  //overriding
//  final class Dog extends Animal{
//    override def eat = println("munch munch")
//    final def devour = super.eat
//  }
//
//  val cat = new Cat
//  cat.crunch
//
//  val dog = new Dog()
//  dog.devour

  //preventing overrides
  //1 - final on a member
  //2 - final on a class
  //3 - prevents extension in other Files, allows only on this file
}

