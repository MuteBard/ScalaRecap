package Lectures.ADVPart3Concurrency

import scala.concurrent.Future
import scala.util.{Failure, Success}
//important for futures
import scala.concurrent.ExecutionContext.Implicits.global //the compiler looks for this value into the second parameter list of the apply mehtod of Future, it handles thread allocation of futures
object FuturesPromises extends App{

  def areEggsCooked: Boolean = {
    Thread.sleep(2000)
    true
  }

  val aFuture = Future {
    areEggsCooked //calculates if eggs are cooked on another thread
  }//(global) passed by the compiler


  println(aFuture.value)// none, returns an option cause it could either fail or not be ready

  println("Waiting on Future")
  aFuture.onComplete { //a partial function
    case Success(eggsCooked) => println(s"Eggs are cooked: $eggsCooked")
    case Failure(exception) => println(s"Something has gone wrong: $exception")
  }

  Thread.sleep(3000)

  //Success cases can be nested




}
