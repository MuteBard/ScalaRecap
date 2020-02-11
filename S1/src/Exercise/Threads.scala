package Exercise

object Threads extends App{


  /*Exercises
    1) Construct 50 "Inception threads" (threads that construct other threads)
    Thread1 -> Thread2 -> Thread3 -> Thread4 ->  = ...
    println(s"Hello from thread#$x ")
    in reverse Order
  */

  //your way
//  @scala.annotation.tailrec
//  def threadGenerator(count: Int = 1, thread: Thread = new Thread(() => println(s"Hello from thread 1"))): Unit = {
//    if (count > 50) println("End")
//    else {
//      thread.start()
//      Thread.sleep(100)
//      println(count)
//      threadGenerator(count + 1, new Thread(() => println(s"Hello from thread #${count + 1}")))
//    }
//  }
//  threadGenerator()

  def inceptionThread(maxThreads : Int, i: Int = 1) : Thread = new Thread(() => {
    if(i < maxThreads){
      val newThread = inceptionThread(maxThreads, i + 1)
      newThread.start()
      newThread.join()
    }
    println(s"Hello form thread #$i")
  })

  inceptionThread(50).start()

  //2)
  var x = 0
  val threads = (1 to 100).map(_ => new Thread(() => x += 1))
  threads.foreach(_.start())

    /*
    what is the biggest value for x?
    what is the smallest value for x?
    */


  /*
  3 sleep fallacy
   */
//  var message = ""
//  val awesomeThread = new Thread(() => {
//    Thread.sleep(1000)
//    message = "Scala is awesome"
//  })
//
//  message = "Scala sucks"
//  awesomeThread.start()
//  Thread.sleep(2000)
//  println(message)
  //whats the value for message
  //is it guarenteed?
  //why or why not

}
