package Lectures.ADVPart1Basics

object DarkSyntaxSugar extends App{
  // syntax  sugar #1: methods with single parameter

  def singleArgMethod(arg: Int): String = s"arg little ducks..."

  //We can call singleArgMethod
  val description = singleArgMethod{
    //write some complex code
    42
  }

//  val aTryInstance = Try { //
//    throw new RuntimeException
//  }
}
