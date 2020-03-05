package Graphs

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source, Zip}

import scala.util.Random

object GraphBasics extends App{

//	GBLesson
//	reducedComments
//	GBEx1
	GBEx2
}

object GBLesson{
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()

	/**
	 * I would like to create a stream that deals with numbers so that it has some kind of numbers source as input
	 */

	val input = Source(1 to 1000)

	/**
	 * For everyone of these numbers, two hard computations should be evaluated in parallel and they should be tuples paired
	 * together
	 */

	val incrementer = Flow[Int].map(x => x + 1)
	val multiplier = Flow[Int].map(x => x * 10)

	/**
	 * I would like to execute both of these flows in parallel somehow and merge back the results in a tuple or a pair
	 */

	val output = Sink.foreach[(Int, Int)](println)

	/**
	 * I would like to feed all the number from the source to both of these flows and to merge them back in the output. The problem with
	 * that is that we cannot do that with what we currently know. we need to create new components. We need to construct a complex graph with
	 * a fan out operator which feed this input into multiple outputs and a fan in operator which feed theses things into the same output
	 */

	val graph = RunnableGraph.fromGraph(
		GraphDSL.create(){
			implicit builder: GraphDSL.Builder[NotUsed] =>
				import GraphDSL.Implicits._ //makes ~> work

				//add the necessary components of this graph
				/**
				 * input source will broadcast its elements to both incremental and multiplier, and for that we will need a special component that
				 * will just for out every single element to two outputs, and each one of those outputs will feed into incremental and multiplier
				 * respectively and then the outputs of these two flows will be merged back into single output and paired as a tuple and fed into this
				 * output
				 */
				val fanOut = builder.add(Broadcast[Int](2)) //fan out operator
				val fanIn = builder.add(Zip[Int, Int]) // has two inputs

				//tying up the components
				input ~> fanOut
				fanOut.out(0) ~> incrementer ~> fanIn.in0
				fanOut.out(1) ~> multiplier ~> fanIn.in1
				fanIn.out ~> output

				//return a closed shape
				ClosedShape
		}
	)

	/**
	 * graph is a type graph which is a kind of object that can be executed and materialized
	 * it is the same kind of object that allows this to work: graph.run()
	 * but this graph is different it takes
	 *
	 * GraphDSL.create(){
	 *      implicit builder: GraphDSL.Builder[NotUsed] =>
	 * }
	 *
	 * as and argument, which is a graph, an inert, static graph. the way wer are building a graph of any kind of shape is
	 * by calling GraphDSL.create with this weird signature.
	 *
	 * this function, implicit builder: GraphDSL.Builder[NotUsed] =>, must return a shape object.
	 *
	 *
	 * val graph = RunnableGraph.fromGraph(
	 * 		GraphDSL.create(){
	 *          implicit builder: GraphDSL.Builder[NotUsed] =>
	 *          //shape
	 *      }//graph
	 * )//runnable graph
	 *
	 * graph.run() run the graph and materialize it
	 *
	 *
	 *
	 *
	 */



}

object reducedComments{
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()
	val input = Source(1 to 1000) // have numbers 1 to 1000 (A)
	val incrementer = Flow[Int].map(x => x + 1) // increment values by 1 (B1)
	val multiplier = Flow[Int].map(x => x * 10) // multiply values from source by 10  (B2)
	val output = Sink.foreach[(Int, Int)](println) // print tuples (C)

	val graph = RunnableGraph.fromGraph(
		GraphDSL.create(){
			implicit builder: GraphDSL.Builder[NotUsed] => //Mutating the builder starts HERE
				import GraphDSL.Implicits._

				val fanOut = builder.add(Broadcast[Int](2)) // emits two ints (E)
				val fanIn = builder.add(Zip[Int, Int]) // takes in two ints (D)

				input ~> fanOut // A === E
				fanOut.out(0) ~> incrementer ~> fanIn.in0 // E === B1 === D
				fanOut.out(1) ~> multiplier ~> fanIn.in1 // E === B2 === D
				fanIn.out ~> output // D === C

				ClosedShape //Freeze the Builder's shape, builder is now immutable
		}
	)

	graph.run()
//	(2,10)
//	(3,20)
//	(4,30)
//	(5,40)
//	(6,50)
//	(7,60)
//	(8,70)
//    	....
//    	....
//	(996,9950)
//	(997,9960)
//	(998,9970)
//	(999,9980)
//	(1000,9990)
//	(1001,10000)

}

object GBEx1 {
	/**
	 * exersise 1: feed a source into 2 sinks at the same time
	 */

	implicit val system = ActorSystem("system")
	implicit val materializer = ActorMaterializer()

	val input = Source(1 to 100)
	val even = Flow[Int].filter(_ % 2 == 0)
	val odd = Flow[Int].filter(_ % 2 != 0)
	val evenOutput = Sink.foreach{println}
	val oddOutput = Sink.foreach[Int](x => println(s"\t$x"))

	def createShape(): Unit ={

	}

	val graph = RunnableGraph.fromGraph(
		GraphDSL.create(){
			implicit builder : GraphDSL.Builder[NotUsed] =>
				import GraphDSL.Implicits._

				val fanOut = builder.add(Broadcast[Int](2))

				input ~> fanOut
				fanOut.out(0) ~> even ~> evenOutput
				fanOut.out(1) ~> odd ~> oddOutput

				ClosedShape
		}
	)
	println("sink1\tsink2")
	graph.run()
}

object GBEx2 {
	import scala.concurrent.duration._
	import scala.language.postfixOps

	implicit val system = ActorSystem("system")
	implicit val materializer = ActorMaterializer()

	val fastinput = Source(for(x <-  1 to 100) yield { x.toString }).throttle(5, 1 second)
	val slowInput = Source(for(_ <- 1 to 100) yield {
		val random = new Random
		random.alphanumeric take 4 mkString
	}).throttle(10, 5 second)

//	val merge = Flow[(Int,String)].map(elem => elem._2.take(2)+elem._1+elem._2.takeRight(2))
//	val balance = Flow[String].map(elem => elem)

	val sink1 = Sink.foreach(println)
	val sink2 = Sink.foreach[String](x => println(s"\t\t$x"))

	val balanceGraph = RunnableGraph.fromGraph(
		GraphDSL.create(){
			implicit builder : GraphDSL.Builder[NotUsed] =>
				import GraphDSL.Implicits._

				val merge = builder.add(Merge[String](2))
				val balance = builder.add(Balance[String](2))

				fastinput ~> merge ~> balance ~> sink1
				slowInput ~> merge;   balance ~> sink2

				ClosedShape
		}
	)


	println("sink1\tsink2")
	balanceGraph.run()

	//this can balance out rate of production of values. The amount of data given to sync 1 and 2 is distributed equally






}