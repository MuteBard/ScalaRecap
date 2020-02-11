package Lectures.FUNDPart2OOP

object OOBasics extends App{

//  val person = new Person("Carl", 27)
//  println(person.name)
//  person.greet("Erin")
//  person.greet()
//
//
//  val carla = new Writer("Carla", "Severe", 32)
//  val herbook = new Novel("Children of Tokua", 2023, carla)
//  println(herbook.authorAge())
//  println(herbook.isWrittenBy(carla))
//  println(herbook.copy().name)


  val counting  = new Counter(10)
  counting.inc(10).dec.dec.inc(50).dec(20).print




}
//constructor, you can use default parameters for constuctors as well
class Person(c_name: String, c_age: Int){
  val name: String = c_name
  val age: Int = c_age

  //remember that we a re in a codeblock so something like this can exist
  println("skjdnskjnf")

  //overloading constructors, calls the primary constructor, but just use default params in original
  def this(name: String) = this(name, 0)
  def greet (name : String) : Unit = println(s"Hello $name, my name is ${this.name} ")
  //overloading
  def greet(): Unit = println(s"Hi, I am ${this.name}")
}



class Writer(c_firstName: String, c_surName : String, c_age: Int){
  val firstName: String = c_firstName
  val surname: String = c_firstName
  val age : Int = c_age
  def fullName() : String = s"${this.firstName} ${this.surname}"
}

class Novel(c_name : String, c_yearOfRelease : Int, c_author : Writer){
  val name : String = c_name
  val yearOfRelease : Int = c_yearOfRelease
  val author : Writer = c_author
  def authorAge() : Int = author.age
  def isWrittenBy(author : Writer): Writer = author
  def copy () : Novel = this
}

class Counter_old(num :  Int){
  val counter : Int = num
  def currentCount() : Int = this.counter
  def increment() : Counter = new Counter(currentCount() + 1)
  def increment(num : Int) : Counter = new Counter(currentCount() + num)
  def decrement() : Counter = new Counter(currentCount() - 1)
  def decrement(num : Int) : Counter = new Counter(currentCount() + num)
}


//this has the same effect of defining a method that actually gets that field like a getter
class Counter(val count :  Int){
  /*The fact that we are returning a new counter rather than modifying the current counter is called
  immutability and it tis the same principle with declaring vals for primative types but extended to
  object and to classes. This implies that instances are fixed and cannot be modified. Whenever you need
  to modify something, you must return a new instance*/
  def inc : Counter = {
    println(s"Incrementing, number is now ${count + 1}")
    new Counter(count + 1)
  }

  def dec : Counter = {
    println(s"Decrementing, number is now ${count - 1}")
    new Counter(count - 1)
  }

  def innerOperation(counter : Counter, number : Int, sign : Int) : Counter = {
    if(number <= 0){
      println(s"${if (sign > 0) "Incrementing" else "Decrementing"}, number is now ${counter.count}")
      counter
    }else{
      if (counter.count != count) {
        println(s"${if (sign > 0) "Incrementing" else "Decrementing"}, number is now ${counter.count}")
      }
      innerOperation(new Counter(counter.count + sign), number - 1, sign)
    }
  }

  def inc(num : Int) : Counter = innerOperation(new Counter(count), num, 1)
  def dec(num : Int) : Counter = innerOperation(new Counter(count), num, -1)
  def print = println(s"Current number is $count")

}