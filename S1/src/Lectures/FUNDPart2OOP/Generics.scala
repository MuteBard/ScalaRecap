package Lectures.FUNDPart2OOP

object Generics extends App{

  class LL[A]{ //generic type
    //use the type A inside the class definition
  }
  val listofIntegers = new LL[Int]
  val listofStrings = new LL [String]


  class Maps[Key, Value]


  //generic methods
  object LL {
    def empty[A]: LL[A] = ???
  }

  val emptyListofIntegers = LL.empty[Int]


  //variance problem

  class Animal
  class Cat extends Animal
  class Dog extends Animal

  //List[Cat] extends List[Animal]
  //this behavior is called covariance

  //1)Covariance, the parent class assumes being assigned to
  class CovariantList[+A]
  val animal : Animal = new Cat
  val animalList: CovariantList[Animal] = new CovariantList[Cat]

  //2)Invariance , both classes are each in their own world, classes are independent
  class InvariantList[A]
  //val invariantAnimalList: InvariantList[Animal] = new InvariantList[Cat]

  //3)Contrivance
  //the subclass assumes being assigned to
  class Trainer[-A]
  val trainer : Trainer[Cat] = new Trainer[Animal]

  //4)Bounded Types,
  class Cage[A <: Animal](animal : A)

  val cage = new Cage(new Dog)
  //val cage2 =  new Cage(new Trainer[Int]) //this is not allowed as Trainer is not a subclass of Animal


}
