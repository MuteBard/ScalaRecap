package Lectures.FUNDPart1Basics

object StringsOPs extends App {
  val str : String = "Hello I am Carl"
  println(str.charAt(2))
  println(str.substring(2, 5))
  println(str.split(" ").toList)
  println(str.split("").toList)
  println(str.startsWith("Hello"))
  println(str.replace(" ", "-"))
  println(str.toLowerCase())
  println(str.length)


  val numberStr : String = "750"
  val num = numberStr.toInt
  val test1 = s"a${numberStr}b"
  println(test1.reverse)

  val weight = 210.5f
  val message : String = f"I weigh $weight%2.2f now"
  // two chars minimum and 2 decimal places
  println(message)

}
