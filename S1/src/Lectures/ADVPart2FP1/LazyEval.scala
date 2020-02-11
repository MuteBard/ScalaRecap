package Lectures.ADVPart2FP1

object LazyEval extends App {

  //the program wont crash anymore because of lazy keyword
  lazy val x: Int = throw new RuntimeException
  //Lazy values are evaluated once but only when they are used for the first time
  //Lazy delays the evaluation of values
  //x is only evaluated during an on need basis, only when called


  lazy val something : Int = {
    println("Do I Talk?")
    750
  }

  val anything : Int = {
    println("Do I Speak?")
    376
  }



//  println(something)
//  println(something)
//  println("--------")
//  println(anything)
//  println(anything)

  //implications 1
  def sideEffectCondition: Boolean ={
    println("boo")
    true
  }
  def simpleCondition: Boolean = false
  lazy val lazyCondition = sideEffectCondition
  //compiler doesnt compute the lazyCondition because the simpleCondition is already false, so it just never evaluates the lazyCondition
  println(if(simpleCondition && lazyCondition) "yes" else "no")

  //implications 2
  //Call by Need


  //Quick reference to call by name vs call by value
  def calledByValue(x : Long): Unit = {
    println(s"by value: $x")
    println(s"by value: $x")
  }

  // => Tells the compiler that the function will be called by Name
  def calledByName(x : => Long): Unit = {
    println(s"by value: $x")
    println(s"by value: $x")
  }

  calledByValue(System.nanoTime())
  calledByName(System.nanoTime())

  //a call by name method (refer to CBNvsCBV)
  def byNameMethod(n: => Int) = {
    lazy val t = n
    t + t + t + 1
  }
  //a call by name method (refer to CBNvsCBV)
  def badByNameMethod(n: => Int) = {
    n + n + n + 1
  }

  def retrieveMagicValue = {
    println("waiting 1 sec")
    Thread.sleep(1000)
    42
  }
  println(byNameMethod(retrieveMagicValue))
  println(badByNameMethod(retrieveMagicValue))


  //Filtering with lazyVals
  def lessThan30(i: Int): Boolean ={
    println(s"is $i less than 30")
    i < 30
  }

  def greaterThan20(i: Int): Boolean ={
    println(s"is $i greater than 20")
    i > 20
  }

  val numbers = List(1,25,40,5,23)
  val lt30 = numbers.filter(lessThan30) //remember eta expansion? its happening here
  val gt20 = lt30.filter(greaterThan20)

  println(gt20)
  //with filter is a function on collections that uses lazy values under the hood

  println("_________________________")
  val lt30Lazy = numbers.withFilter(lessThan30)
  val gt20Lazy = lt30Lazy.withFilter(greaterThan20)
  gt20Lazy foreach println

  //what is a side effect again?

}
