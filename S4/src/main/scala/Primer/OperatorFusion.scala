package Primer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

object OperatorFusion extends App {
	OPLesson
}


object OPLesson{
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()

	val simpleSource = Source(1 to 1000)
	val simpleFlow = Flow[Int].map(_ + 1)
	val simpleFlow2 = Flow[Int].map(_ * 10)
	val simpleSink = Sink.foreach[Int](println)

	//this runs on the SAME ACTOR
//	simpleSource.via(simpleFlow).via(simpleFlow2).to(simpleSink).run()

	/**
	 * Akka stream components are based on actors
	 * now the thing is if you  connect components by calling the via, viaMat, toMat methods, the composit akka stream will default
	 * run on the same actor. this is called operator or component fusion.
	 *
	 * its as if you are doing (component).run() inside the receive function of an actor
	 * A single cpu core wil be used for the entire flow.
	 *
	 * This is ok if the operations are fast
	 * This is bad if the operations are time expensive
	 */

	val complexFlow = Flow[Int].map{ x =>
		Thread.sleep(1000)
		x + 1
	}

	val complexFlow2 = Flow[Int].map{ x =>
		Thread.sleep(1000)
		x + 1
	}

	//BAD
//	simpleSource
//		.via(complexFlow)
//		.via(complexFlow2)
//		.to(simpleSink)
//		.run()
	//this waits two seconds just to be able to print since both complex operations take 1 sec since everything runs on one actor

	//GOOD
/*	simpleSource.via(complexFlow).async //runs on one actor
		.via(complexFlow2).async //runs on another actor
		.to(simpleSink) //runs on yet another actor
		.run()*/
	//this has only a one second time difference since complexFlow1 and 2 were able to run on different actors and break
	//operatorFusion using Async boundries. only issue is that some ordering is not guarenteed BUT the relative order of the element inside
	//every step of the stream

//	Source(1 to 3)
//    	.map(elem => {println(s"Flow A: $elem"); elem}).async
//		.map(elem => {println(s"Flow B: $elem"); elem}).async
//		.map(elem => {println(s"Flow C: $elem"); elem}).async
//		.runWith(Sink.ignore)

//	Flow A: 1  #1
//	Flow A: 2  #2
//	Flow B: 1      #1
//	Flow A: 3  #3
//	Flow B: 2      #2
//	Flow C: 1          #1
//	Flow B: 3      #3
//	Flow C: 2          #2
//	Flow C: 3          #3
//  Relative order of the element inside every step of the stream




}