package Playground

import scala.util.Random

object ScalaPlayground extends App {

  println("Hello Scala")
  for (_ <- 1 to 100) {
    val random = new Random
    val citizenPID = random.alphanumeric take 10 mkString
    val x = random.nextInt(4)BN                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             ,M
    val canidate = x match {
      case 0 => "Jared A."
      case 1 => "Erin H."
      case 2 => "Josslyn L."
      case 3 => "Carl S."
    }
    println(s"$citizenPID, $canidate")
  }
}
