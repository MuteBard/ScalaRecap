package Primer

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.Future
import scala.util.Success
import scala.util.Failure
object MaterializingStreams extends App{

//	Lesson2
  Exercise3

}


object Lesson2 {
	//val graph = source.via(flow).to(sink)
	//val result = graph.run()

	/**
	 * INTRO
	 * Components are static until they are run
	 * Calling the run method on a graph
	 * A graph is a blueprint for a stream, it doesnt really do anything until you run it
	 * Running a graph allocates the right resources
	 *      - actors
	 *      - thread pools
	 *      - sockests, connections
	 *      - etc, everything is transparent
	 *
	 * Running a grpah is also called materializing the graph, and the result of materializing a graph is called a materialized value
	 */

	/**
	 * MATERIALIZED VALUES
	 * Materializing a graph = materializing all components
	 *      - each component produces a materialized value when run
	 *      - the graph produces a single materialized value
	 *      [<A>] -> [<B>] -> [<C>] -> <C>
	 *      - You can reuse the same component in different graphs
	 *      - different runs = different materializations
	 *
	 *      - A materialized value can be anything (Int, Future, a connection, an actor, a Unit)
	 *      - The materialized value may or may not have  any connections at all to the actual elements that go through the stream
	 */

	implicit val system = ActorSystem("MaterializingStreams")
	//a materializer is an object that allocates the right resources to running a akka stream
	implicit val materializer = ActorMaterializer()

	//This graph is currently static until I call the run method on it
	val simpleGraph = Source(1 to 10).to(Sink.foreach(println))
	//Not used means Unit, no meaningful value, but we can change that, we can actually return a meaningful value out of a stream
	//val simpleMaterializedValue: NotUsed = simpleGraph.run()


	val source = Source(1 to 10)
	//takes in an Int, and returns a future Int, the future wull be completed at the sum of all these elements
	val sink: Sink[Int, Future[Int]] = Sink.reduce[Int]((a, b) => a + b)

	//The way that we can have access to that materialized value is to start a running graph between the source and the sink
	//RunWith connects the source to the sink and returns a simple graph and it actually executes that graph by calling its run method
	val sumFuture = source.runWith(sink)
	import system.dispatcher //an execution context to execute the callback
	sumFuture.onComplete{
		case Success(value) => println(s"The sum of all elements is : $value")
		case Failure(ex) => println(s"The sum of all elements could not be computed: $ex")
	}
	//The sum of all elements is : 55

	/**
	 * Now, the problem with materialized values is that when you want to materialize a graph all the components in that graph can return a materialized value
	 * but the result of running the graph is a single materialized value so you need to choose which value you want to be returned at the end
	 *
	 * So when we construct a graph with the methods via and to when we connect it with flows and sinks, by default the left most materialzed value
	 * will be kept. The notused pertains to the material value directly given by the source
	 */

	//choosing materialzed values
	val simpleSource = Source(1 to 10)
	val simpleFlow = Flow[Int].map(x => x + 1)
	val simpleSink = Sink.foreach[Int](println)

	//simpleSource.viaMat(simpleFlow)((sourceMat, flowMat) =>  flowMat)
	//simpleSource.viaMat(simpleFlow)(Keep.left)
	//simpleSource.viaMat(simpleFlow)(Keep.none)
	val graph = simpleSource.viaMat(simpleFlow)(Keep.right).toMat(simpleSink)(Keep.right)
	graph.run().onComplete{
		case Success(_) => println("Finished")
		case Failure(ex) => println(s"Failed: $ex")
	}

	//source.to(sink) is leftmost, uses source's materialized value
	//source.runWith(sink) is rightmost, uses sink's materialized value

	//When in doubt, always use viaMat and toMat because it always gives you control over which materialized value you want at the end
	//think of it as a series of branching pipes
}

object Exercise2 {
	/**
	 * - return the last element out of a source (use Sink.last)
	 * - compute the total word count out of a stream of sentences
	 * - could use map, fold or reduce to help
	 */
	import system.dispatcher
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()


	val sentenceSource = Source(List("I am learning about Akka streams", "Its like the 5th lecture im reaching so far", "I cant wait to leave my job"))
	val wordSink = Sink.fold[Int, String](0)((count, newSentence) => count + newSentence.split(" ").length)
	val g1 = sentenceSource.toMat(wordSink)(Keep.right).run()

	g1.onComplete{
		case Success(x) => println(x)
		case Failure(ex) => s"Failed: $ex"
	}
}


object Exercise3 {
	/**
	 * - return the last element out of a source (use Sink.last)
	 * - compute the total word count out of a stream of sentences
	 * - could use map, fold or reduce to help
	 */
	import system.dispatcher
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()


	val sentenceSource = Source(List("I am learning about Akka streams", "Its like the 5th lecture im reaching so far", "I cant wait to leave my job"))
	val wordFlow = Flow[String].fold[Int](0)((count, newSentence) => count + newSentence.split(" ").length)
	val g1 = sentenceSource.via(wordFlow).toMat(Sink.head)(Keep.right).run()
	val g2 = sentenceSource.via(wordFlow).runWith(Sink.head)

	g2.onComplete{
		case Success(x) => println(x)
		case Failure(ex) => s"Failed: $ex"
	}
}