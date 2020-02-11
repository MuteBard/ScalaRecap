package Lectures.ADVPart3Concurrency

import scala.collection.parallel.immutable.ParVector

object ParallelUtils extends App {

  //https://alvinalexander.com/scala/how-to-use-parallel-collections-in-scala-performance

  //a parallel collection means that operations on them are handled by multiple threads at the same time
  val parList = List(1,2,3,4).par

  val aParVector = ParVector[Int](1,2,3,4)

  /*
  Seq
  Vector
  Array
  Map - Hash
  Set - Hash
   */

  //Tends to lead to increased Perf for things about 1000 elems or more


  def measure[T](operation: => T) : Long = {
    val time = System.currentTimeMillis()
    operation //forces  evaluation
    System.currentTimeMillis() - time
  }

  val list = (1 to 1000).toList
  val serialTime = measure {
    list.map(_ + 1)
  }

  val parallelTime = measure {
      list.par.map(_ + 1)
    }

  println(s"Serial Time $serialTime")
  println(s"Parallel Time $parallelTime")

  //with (1 to 10000000).toList
//  Serial Time 2297
//  Parallel Time 1416


  //(1 to 1000).toList
//  Serial Time 4
//  Parallel Time 70

}
