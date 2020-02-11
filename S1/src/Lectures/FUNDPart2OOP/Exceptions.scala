package Lectures.FUNDPart2OOP

object Exceptions extends App {

//  val excuse_me = throw new NullPointerException
  //Exceptions denote that something went wrong with the program
  //Errors denote somehting went wring with the s

  def getInt (withExceptions : Boolean ): Int =
    if(withExceptions) throw new RuntimeException("No Int for you")
    else 42

  try {
    getInt(true)
  }catch{
    case e : RuntimeException => println("caught a Runtime Exception")
  }finally{
    println("All Good")
  }
  //exceptions come from java language
}



