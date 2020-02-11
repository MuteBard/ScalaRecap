package Lectures.ADVPart2FP1

object CurriesPAF extends App{
  //curried functions
  val superAdder: Int => Int => Int =
    x => y => x + y

  val add3 = superAdder(3) //Int => Int = y => 3 + y
  println(add3(5))
  println(superAdder(5)(3))

  //curried method
  def curriedAdder(x : Int)(y : Int): Int = x + y

  //we defined the Type annotations so the compiler is happy, otherwise it cant if we want a value or a function back
  //what the compiler does is that it figures out that we want the reminder function after curriedAdder with fewer parameter lists
  //What happens behind the scenes is something called lifting. We converted a method into a function value of type int error int
  val add4: Int => Int = curriedAdder(4)


  //we cannot use methods in higher order functions unless they are transformed into function values

  //functions != methods (dues to JVM limitation)
  //methods are part of instances of classes or part of the singleton object CurriesPAF
  //method are not instances of function X
  //converting a method to a function is called lifting : val add4: Int => Int = curriedAdder(4)
  //lifting = ETA-EXPANSION
  //ETA-EXPANSION is a simple technique for wrapping functions into this extra layer while preserving identical functionality
  //And this is performed by the compiler to create funcitons out of methods


  def inc(x: Int) : Int = x + 1
  //when you use this inc method as a function
  List(1,2,3).map(inc)
  //the compiler does eta-expansion for us and then turns the inc method into a function and then it uses that function value on maps so the
  //compiler basically rewites this as List(1,2,3).map(x => inc (x))

  //Why do we care?

  //The way we can force the compiler to Eta-expansion when we want, is when we to use partial function applications
  //I want to define a function value call curriedAdder at 4750, i can put in an underscore. This will tell the compiler,
  //Hey compiler, do an eta-expansion for me and turn curredAdder into a function value after you applied the first parameter list
  val add4750 = curriedAdder(4750) _



  //EXERCISE
  val simpleAddFunction = (x : Int , y : Int) => x + y
  def simpleAddMethod(x : Int, y : Int) = x + 1
  def curriedAddMethod(x : Int)(y : Int) = x + 1

  val add7v1 = (y : Int) => simpleAddFunction(7, y)
  val add7v12 = simpleAddFunction.curried(7)

  val add7v2 = (y : Int) => simpleAddMethod(7, y)
  val add7v22 = simpleAddMethod(7, _ : Int)
  val add7v3 = curriedAddMethod(7)_
  val add7v32 = curriedAddMethod(7)(_)


  def concatenator(a: String, b: String, c: String) = a + b + c
  val concat2 = concatenator("I ", _ :String, " Scala") // x: String => concatenator("I", x, "Scala")
  println(concat2("love"))
  //add7 : Int => Int = 7 + y

  //very good for very granular API where you want support the general general function but a also smaller and more concert functions
  //this frees you from defaults, cant use overloaded methods in package objects
  //Also overloading is frowned upon
}
