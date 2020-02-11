package Lectures.ADVPart3Concurrency

import scala.collection.mutable
import scala.util.Random

object ThreadCommunicationPart2 extends App {


  //producer - > [?  ?  ?] -> consumer
  /*
  Inside this buffer, this is more complicated because noew we have many values and the producer and the consumer may run indefinitely.
  Both the producer and the consumer my block eachother. So say for example the buffer is full--The producer has produced enough values to fill the buffer
  In that case the producer must block until the consumer has finished extracting some values out of the buffer and vise versa. If the buffer is empty,
  that is if the consumer is too fast then the consumer must block until the producer produces more. So this is the level two of our producer consumer
  problem and see how we would approach that
  */

  def prodConsumerProducerWithBigBufferEnergy(): Unit = {
    val buffer : mutable.Queue[Int] = new mutable.Queue[Int]
    val capacity = 3
    val random = new Random()
    val consumer = new Thread(() => {
      while(true){
        buffer.synchronized({
          if(buffer.isEmpty) {
            println(s"[consumer] Buffer empty, waiting...")
            buffer.wait()
          }
          //there must be at least one value in the buffer
          val x = buffer.dequeue()
          println(s"[consumer] I have consumed value : $x")
          //hey producer, are you lazy?
          buffer.notify()
        })

        Thread.sleep(random.nextInt(1000))
      }
    })
    val producer = new Thread(() => {
      val random = new Random
      var i = 0
      while(true){
        buffer.synchronized({
          if(buffer.size == capacity){
            println(s"[producer] buffer is full, waiting")
            buffer.wait()
          }

          //there must be at least one empty space in the buffer
          println(s"[producer] producing $i")
          buffer.enqueue(i)
          //hey consumer, new food for you!
          buffer.notify()
          i += 1
        })
        Thread.sleep(random.nextInt(1000))
      }
    })
    consumer.start()
    producer.start()
  }


  prodConsumerProducerWithBigBufferEnergy()
}
