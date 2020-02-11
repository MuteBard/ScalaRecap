package Lectures.FUNDPart1Basics

object ValuesVariablesTypes extends App {

  //////////////////////////////////////////
  //vals are Immutable
  val x: Int = 42
  println(x)

  //once a val has a value, it is set in stone, the operation below cannot happen
  //x = 2

  //The types of vals are optional, compiler can infer types
  val y = 43
  //////////////////////////////////////////

  val str: String = "hello"
  val str2 = "hello"

  //////////////////////////////////////////

  val isTrue: Boolean = true
  val isFalse = false

  //////////////////////////////////////////

  val chr : Char = 'a'
  val chr2 = 'b'

  //////////////////////////////////////////

  val num : Int = 1
  val num2 = num

  //////////////////////////////////////////

  val sht1 : Short = 10

  //////////////////////////////////////////

  val lng1 : Long = 100000

  //////////////////////////////////////////

  val flt : Float = 1.0f
  val flt2 = 2.0f

  //////////////////////////////////////////

  val doub : Double = 1.0
  val doub2 = 2.0

  //////////////////////////////////////////

  var number: Int = 4
  number = 5

  //Variables in functional programing are known as side effects. Side effects are useful because they allow us to see what our program is doing

  //////////////////////////////////////////

}
