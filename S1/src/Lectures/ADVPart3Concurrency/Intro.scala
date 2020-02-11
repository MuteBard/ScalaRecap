package Lectures.ADVPart3Concurrency

import java.util.concurrent.Executors

object Intro extends App {
  //This lesson will involve concurrent reading or writing to the same shared memory zone.
  //creation, manipulation and communication of jvm threads

  //One of the critical pieces of parallel programming on the JVM is called a thread.
  //A thread, much like anything else on the JVM is an instance of a class


  /*
    Interface Runnable{
      public void run()
    }
   */

  val run = new Runnable {
      override def run(): Unit = println("running in parallel")
    }
  val aThread = new Thread(run)

//  aThread.start() //gives a signal to the JVM to start a JVM thread
  //starting a thread will create a JVM thread which will run on top of an Operating system thread

//  aThread.join() //blocks until a thread finishes running
  //This is how you make sure that a thread has already run before you make some computations


  //you can use the abstract method call of runnable call and reduce it to a lambda
  val threadHello = new Thread(() => (1 to 5).foreach(_ => println("Hello")))
  val threadBye = new Thread(() => (1 to 5).foreach(_ => println("Bye")))

  //different runs produce different results

//  threadHello.start()
//  threadBye.start()

  /*
  Bye
  Hello
  Bye
  Bye
  Bye
  Bye
  Hello
  Hello
  Hello
   */
  //different executions with multithreaded environment produces different results


  //threads are very expensive to start and kill, and the solution to get around this is to reuse them
  //The Java standard library offers a nice standard API to reuse threads with executers and thread pools
  //And the way we do that is to create a pool

  //executors
  val pool = Executors.newFixedThreadPool(10)
  //So this runnable will get executed by one of the 10 threads managed by this thread pool
  //I have 10 threads at my disposal to execute my actions and the executer manages which thread executes which
  //Therefore i really dont need to care about starting and stopping threads which is really cool
  //
//  pool.execute(() => println("something in the thread pool"))

//  pool.execute(() => {
//    for(x <- 1 to 10) println(s"Thread A value is at $x")
//  })
//
//  pool.execute(() => {
//    for(x <- 1 to 10) println(s"Thread B value is at $x")
//  })

  //Thread pools have really nice API's
  //If i wanted to shutdown all the threads, no more actions can be submitted

    pool.shutdown() //if you try to execute after shutting it down it will create and exception in the main thread
//  pool.shutdownNow() //interrupts the sleeping threads that are currently running under the pool, violent
//    println(pool.isShutdown) // might return a result even while the thing is running

  //CONCURRENCY PROBLEMS ON THE JVM

//  def runInParallel: Unit ={
//    var x = 0
//    val thread1 = new Thread(() => x = 1 )
//    val thread2 = new Thread(() => x = 2 )
//    thread1.start()
//    thread2.start()
//    println(x)
//  }

//  for(_ <- 1 to 100) runInParallel
  //this is a race condition, which is bad
  //two threads are attempting to set the same memory zone at the same time.
  //Race conditions are bad baecause they can introduce bugs in multithreaded code and they are notoriously hard to diagnose and fix

  class BankAccount(var amount: Int){
    override def toString: String = ""+amount
    def buy(account : BankAccount, thing : String, price : Int) = {
      account.amount -= price
//      println(s"I've bought $thing")
//      println(s"My account is now $account")

    }
  }

  for(_ <- 1 to 10000){
    val account = new BankAccount(50000)
    val thread1 = new Thread(() => account.buy(account, "Shoes", 3000))
    val thread2 = new Thread(() => account.buy(account, "iPhone12", 4000))

    thread1.start()
    thread2.start()
    Thread.sleep(10)
      if (account.amount != 43000) println("AHA "+ account.amount)
  }

//  Race condition, both accessing the amount, not sharing the changed they inflicted with the class in time for the other to pick it up
//  AHA 46000
//  AHA 46000
//  AHA 47000
//  AHA 46000




  //You can syncronize on the critical thing thats about to be modified, in our case thats account.
  //Now, no two threads can evaluate the expression im passing here as a parameter to syncronized at the same time
  def buySafe1(account : BankAccount, thing : String, price : Int): Unit ={
    account.synchronized({
      account.amount -= price
      println(s"I've bought $thing")
      println(s"My account is now $account")
    })
  }

  //You can also use an annotation called @volatile annotated on a val or var of the class which means that all reads and writes to it are
  //syncronized


//  class BankAccount(@volatile var amount: Int){
//    override def toString: String = ""+amount
//    def buy(account : BankAccount, thing : String, price : Int) = {
//      account.amount -= price
//      //      println(s"I've bought $thing")
//      //      println(s"My account is now $account")
//
//    }
//  }

  /*
  Of these two options, the synchronized option is more powerful because you have access to whole expressions to sync rather than
  single vals are vars
   */


}
