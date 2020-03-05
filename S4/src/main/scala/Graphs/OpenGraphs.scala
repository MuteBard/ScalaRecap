package Graphs

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, FlowShape, SinkShape, SourceShape}
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, RunnableGraph, Sink, Source}

import scala.util.Random

object OpenGraphs extends App {
	OGLesson
}

object OGLesson {
	implicit val system = ActorSystem("System")
	implicit val materializer = ActorMaterializer()

	/**
	 * A composit source that concatenates 2 sources
	 *  -  emits ALL the elements from the first source
	 *  -  then all the elements from the second
	 */


	val firstSource = Source(for (_ <- 1 to 10) yield {
		val random = new Random
		random.alphanumeric take 1 mkString
	})

	val secondSource = Source(for (x <- 1 to 10) yield {
		x.toString
	})


	val sourceGraph = Source.fromGraph(
		GraphDSL.create() {
			implicit builder =>
				import GraphDSL.Implicits._

				//concat takes all the element from the first input and pushes them out, then all elements from the second input and pushes them out
				val fanIn = builder.add(Concat[String](2))

				firstSource ~> fanIn
				secondSource ~> fanIn

				SourceShape(fanIn.out)

		}
	)

	//	sourceGraph.to(Sink.foreach(println)).run()

	//	5
	//	M
	//	j
	//	m
	//	e
	//	W
	//	5
	//	X
	//	S
	//	F
	//	1
	//	2
	//	3
	//	4
	//	5
	//	6
	//	7
	//	8
	//	9
	//	10


	val sink1 = Sink.foreach[String](x => println(s"SINK 1 | $x"))
	val sink2 = Sink.foreach[String](x => println(s"SINK 2 | $x"))

	val sinkGraph = Sink.fromGraph(
		GraphDSL.create() {
			implicit builder =>
				import GraphDSL.Implicits._
				val fanOut = builder.add(Broadcast[String](2))
				fanOut ~> sink1
				fanOut ~> sink2

				SinkShape(fanOut.in)

				/*
				If everything is a shape, how are we able to connect the fanOut shape to a sink1 component?
				the ~> operator is an infix operator

				The fanOut is implicitly converted to something
				 */

		}
	)


//	sourceGraph.runWith(sinkGraph)

	//SINK 1 | 1
	//SINK 2 | 1
	//SINK 1 | a
	//SINK 2 | a
	//SINK 1 | 3
	//SINK 2 | 3
	//SINK 1 | 6
	//SINK 2 | 6
	//SINK 1 | 3
	//SINK 2 | 3
	//SINK 1 | 6
	//SINK 2 | 6
	//SINK 1 | p
	//SINK 2 | p
	//SINK 1 | G
	//SINK 2 | G
	//SINK 1 | r
	//SINK 2 | r
	//SINK 1 | O
	//SINK 2 | O
	//SINK 1 | 1
	//SINK 2 | 1
	//SINK 1 | 2
	//SINK 2 | 2
	//SINK 1 | 3
	//SINK 2 | 3
	//SINK 1 | 4
	//SINK 2 | 4
	//SINK 1 | 5
	//SINK 2 | 5
	//SINK 1 | 6
	//SINK 2 | 6
	//SINK 1 | 7
	//SINK 2 | 7
	//SINK 1 | 8
	//SINK 2 | 8
	//SINK 1 | 9
	//SINK 2 | 9
	//SINK 1 | 10
	//SINK 2 | 10

	/**
	 * Challenge - complex flow?
	 * Write your own flow that is composed of two other flows
	 * - one that adds 1 to a number
	 * - one that doesn number * 10
	 */


	val incrementer = Flow[String].map(x => x + 1)
	val multiplier = Flow[String].map(x => x * 1)

	val flowGraph = Flow.fromGraph(
		GraphDSL.create(){
			implicit builder =>
				//everything operates on shapes
				import GraphDSL.Implicits._
				val incrementerShape = builder.add(incrementer)
				val multiplierShape = builder.add(multiplier)

				incrementerShape ~> multiplierShape

				//flow shape has an import port and an output port
				FlowShape(incrementerShape.in, multiplierShape.out)

			/**
			 * builder.add(x) takes in a component and outputs a copy of that component as a shape.
			 * All the operations that happen inside the function with the implicit builder, operate on shapes,
			 * not components.
			 *
			 *
			 */
		}
	)

	secondSource.via(flowGraph).to(Sink.foreach(println)).run()

//	11
//	21
//	31
//	41
//	51
//	61
//	71
//	81
//	91
//	101


	def fromSinkAndSource[A, B](sink: Sink[A, _], source: Source[B, _]): Flow[A, B, _] =
		Flow.fromGraph(
			GraphDSL.create(){
				implicit builder =>
					val sourceShape = builder.add(source)
					val sinkShape = builder.add(sink)

					//no connections since they are not tied together

					FlowShape(sinkShape.in, sourceShape.out)
			}
		)

	//this is dangerous because if the elements going into the sink are finished, the source has no way of stopping the stream if
	//this flow ends up connecting parts of your graph
	val f = Flow.fromSinkAndSource(Sink.foreach[String](println), Source(1 to 10))

	//this is why we use this instead, sends termination and backpressure signals between these unconnected components
	val f = Flow.fromSinkAndSourceCoupled(Sink.foreach[String](println), Source(1 to 10))
}