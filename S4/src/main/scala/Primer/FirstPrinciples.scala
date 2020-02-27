package Primer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future // only get this source, be very careful

object FirstPrinciples extends App{
	Exercise
}


object Lesson {
	//	val system = ActorSystem("FirstPrinciples")
	//	val materializer = ActorMaterializer()(system)

	implicit val system = ActorSystem("FirstPrinciples")
	implicit val materializer = ActorMaterializer()

	//sources
	val source = Source(1 to 10)
	/**
	 * This source will emit all the numbers from 1 to 10
	 */

	//sinks
	val sink = Sink.foreach[Int](println)
	/**
	 * What ever element ends up being received in the sink, the sink will call this function on it
	 */

	//streams
	val graph = source.to(sink)
	//	graph.run(materializer)
	graph.run
	/**
	 * In order to create an akka stream, we need to connect both the source and sink
	 */

	//flows
	/**
	 * transforms elements
	 */

	val flow = Flow[Int].map(x => x + 1)
	val sourceWithFlow = source.via(flow)
	val flowWithSink = flow.to(sink)


	//	sourceWithFlow.to(sink).run()
	//	source.to(flowWithSink).run()
	//	source.via(flow).to(sink).run()

	//nulls are not allowed



	// various kinds of sources
	val finiteSource = Source.single(1)
	val anotherFiniteSource = Source(List(1,2,3))
	val emptySource = Source.empty[Int]
	val infiniteSource = Source(LazyList.from(1)) //do not confuse an Akka stream with a collection stream

	import scala.concurrent.ExecutionContext.Implicits.global
	val futureSource = Source.fromFuture(Future(750))


	//various kinds of sinks
	val theMostboringSink = Sink.ignore
	val foreachSink = Sink.foreach[String](println)
	val headSink = Sink.head[Int] //sensitive topic, gives you the head most item
	val foldSink = Sink.fold[Int, Int](0)((a, b) => a + b) // reduce?


	//various kinds of flows, usually mapped to collection operators
	val mapFlow = Flow[Int].map(x => 2 * x)
	val takeFlow = Flow[Int].take(5) //turns the stream into a finite stream
	//we have drop, filter
	//we do not have flatMap

	//	source -> flow -> flow -> flow -> sink
	val doubleFlowGraph = source.via(mapFlow).via(takeFlow).to(sink)
	doubleFlowGraph.run()

	//syntactic sugars
	val mapSource = Source(1 to 10).map(x => x * 2) // Source(1 to 10).via(Flow[Int].map(x=> x * 2))
	mapSource.runForeach(println) // mapSource.to(Sink.foreach[Int](println)).run()



}

/**
 * Exercsie: create a stream that takes the names of persons, then you will keep the first 2 names with length > 5 characters
 */
object Exercise {
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()
	val source = Source(List("Pokemon", "Erin", "Banette", "Metagross", "Carl"))
	val flow = Flow[String].filter(_.length > 5).take(2)
    val sink = Sink.foreach[String](println)
	source.via(flow).to(sink).run()




}