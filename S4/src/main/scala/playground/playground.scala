package playground

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

object playground extends App {
	implicit val actorSystem = ActorSystem("playground")
	implicit val materializer = ActorMaterializer()

	Source.single("Hello, Streams!").to(Sink.foreach(println)).run()
	/**
	 * Source = "publisher"
	 *      []->
	 *      - emits elements asynchronously
	 *      - may or may not terminate
	 * Sink = "subscriber"
	 *      ->[]
	 *      - receives elements
	 *      - terminates when the publisher terminates
	 * Flow = "processor"
	 *      ->[]->
	 *      transforms elements
	 * We build streams by connecting components
	 *
	 * Directions
	 *      upstream
	 *          []<=
	 *          - to the source
	 *
	 *      downstream
	 *          =>[]
	 *          - to the sink
	 */

}
