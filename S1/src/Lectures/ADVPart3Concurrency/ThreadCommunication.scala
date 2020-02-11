package Lectures.ADVPart3Concurrency

object ThreadCommunication extends App {

  /*producer consumer problem


  producer -> [ x ] -> consumer

  In parallel we have two threads running, one is called the
  producer which has the sole purpose of setting a value in this container and a thread called
  consumer whose sole purpose is to extract the value of the container

  The problem is that the producer and consumer are running in papallel at the same time so they dont know
  when eachother has finished working. So the problem here is that because these threads dont really run in a
  predictable fashion we have somehow force the consumer to wit for the producer to finish that job.


  So the whole problem is about forcing threads to run actions in a guaranteed certain order.
  */


  class SimpleContainer {
    private var value: Int = 0
    def isEmpty: Boolean = value == 0

    def set (newValue : Int) = value = newValue

    def get: Int = {
      val result = value
      value = 0
      result
    }
  }


  def naiveProducerConsumer(): Unit = {
    val container = new SimpleContainer
    val producer = new Thread(() => {
      println("Computing...")
      Thread.sleep(500)
      val value = 376
      println(s"Produced value : $value")
      container.set(value)
    })

    val consumer = new Thread(() => {
      println("Waiting...")
      while(container.isEmpty){
        println("Actively Waiting...")
      }
      println(s"Consumed ${container.get}")
    })

    consumer.start()
    producer.start()
  }

//  naiveProducerConsumer()

//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Actively Waiting...
//  Produced value : 376
//  Consumed 376

  //this works but the amount of time the consumer spends busily waiting is largely a waste of resources
  //This is why we will introduce wait and notify
  //before that we will discuss synchronized

  /*
  Synchronized: Entering a synchronized expresssion on an object locks the object from other threads.
  So when you see some object that is synchronized, this will lock the object's monitor

  Monitor: monitor is a data structure that is internally used by the JVM to keep track of which object is locked by which thread
  Once you have locked the object, any other thread that's trying to read the same expression will block until you are done evaluating.
  And when you are done, you will release the lock and any other thread is free to evaluate the expression if it reaches that point.

  Only AnyRefs can have synchronized blocks, the primitive types like int or boolean, they do not have synchronized expressions

  General priciples
  - make no assumptions about who gets the lock first
  - keep locking and synchronizing to a minimum but still maintain thread safety at all times in multithreaded applications because otherwise
  you'll get into very nasty bugs that are incredibly hard to debug. locking should be kept to a minimum for performance constraints but thread
  safety is more important at all times
   */

  /*
  wait() and notify()
  waiting on an object's monitor suspends you(the thread) indefinitely
   */


//  val someObject = "hello"
//  someObject.synchronized({ // lock the object's monitor
//    someObject.wait() // release the lock and wait, when allowed to proceed, lock the monitor again and continue
//  })

//  someObject.synchronized({ // lock the object's monitor
//    someObject.notify() // signale ONE sleeping thread they may continue
//  })

  //Now bear in mind that multiple threads may be waiting on this objec's monitor, but only one of them will be signaled to continue. This is completely
  //at the mercy of the JVM and OS. If you want to awaken all threads that they may continue, use NotifyAll(). Waiting and notifying are only available
  //withing synchronized expressions


  def smartProducerConsumer() : Unit = {
    val container = new SimpleContainer
    val consumer = new Thread(() => {
      println("[Consumer] waiting...")
      container.synchronized({
        container.wait()
      })
      //container must have some value at this point
      println(s"[Consumer] I have consumed ${container.get}")
    })
    val producer = new Thread(() => {
      println("[Producer] computing")
      Thread.sleep(2000)
      val value = 376

      container.synchronized({
        println(s"[Producer] producing value $value")
        container.set(value)
        container.notify()
      })
    })
    consumer.start()
    producer.start()
  }

  smartProducerConsumer()
}
