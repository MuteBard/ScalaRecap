package Lectures.ADVPart3Concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunicationPart3 extends App {


  /*
    producer1 -> [?, ?, ?] -> consumer1
    producer2 ---^       v----> consumer2
  */


  class Consumer(id : Int, buffer: mutable.Queue[Int]) extends Thread {
    override def run(): Unit = {
      val random = new Random()
      while(true){
        buffer.synchronized({
          /*
          producer produces value, two Consumers are waiting
          producer notifies One Consumer, notifies on buffer
          it is possible that the consumer's notify may awaken the other consumer This is where it gets tricky

           */
          //changing from if to while
          /*
          If i've reached the buff.dequeue then it must because im awakes AND the buffer is not empty
          otherwisw it will put itself back in self inflicted sleep
           */
          while(buffer.isEmpty) {
            println(s"[consumer #$id] Buffer empty, waiting...")
            buffer.wait()
          }
          //there must be at least one value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer #$id] I have consumed value : $x")
          buffer.notify()
        })

        Thread.sleep(random.nextInt(1000))
      }
    }
  }
  class Producer(id: Int, buffer: mutable.Queue[Int], capacity : Int) extends Thread {
    override def run(): Unit = {
      val random = new Random
      var i = 0
      while(true){
        buffer.synchronized({
          while(buffer.size == capacity){
            println(s"[producer #$id] buffer is full, waiting")
            buffer.wait()
          }

          //there must be at least one empty space in the buffer
          println(s"[producer #$id] producing $i")
          buffer.enqueue(i)
          buffer.notify()
          i += 1
        })
        Thread.sleep(random.nextInt(1000))
      }
    }
  }

  def multiProducersConsumers(nProducers: Int, nConsumers: Int): Unit = {
    val buffer : mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 20

    (1 to nConsumers).foreach(i => new Consumer(i, buffer).start())
    (1 to nProducers).foreach(i => new Producer(i, buffer, capacity).start())
  }


  multiProducersConsumers(3, 3)

}
