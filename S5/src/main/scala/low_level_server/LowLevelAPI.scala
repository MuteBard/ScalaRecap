package low_level_server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.stream.scaladsl.{Flow, Sink}

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

object LowLevelAPI extends App {

	LLAPILesson
}

object LLAPILesson {
	implicit val system = ActorSystem("LowLevelServerAPI")
	import system.dispatcher

	/**
	  * source of incoming connections and the materialized value of that source is a future of what is called a server binding,
	  * which will allow us to unbind or shutdown the server
	  */
	val port = 3000
	val inner = 8080
	val name = "localhost"
	val serverSource = Http().bind(name, inner)
	val connectionSink = Sink.foreach[IncomingConnection]{
		connection => println(s"Accepted incoming connection from ${connection.remoteAddress}")
	}

	val serverBindingFuture: Future[Http.ServerBinding] = serverSource.to(connectionSink).run()
	serverBindingFuture.onComplete{
		case Success(binding) =>
			println(s"Server binding successful.")
			binding.terminate(2 seconds)

		case Failure(ex) => println(s"Server binding failed: $ex")
	}


	/**
	  * Method 1: synchronously serve HTTP responses
	  */
//	sync_

	/**
	  * Method 2: asynchronously serve HTTP responses, useful when it takes awhile to respond to http request with http response
	  */
//	async_

	/**
	  * Method 3: asynchronosly via Akka Streams to serve HTTP responses
	  */
	async_streams


}

object sync_ {
	import LLAPILesson.system
	import LLAPILesson.name
	val port = 3000

	/**
	  * We defined a partial function that turns an HttpRequest into an HttpResponse. And any HttpRequest that is fed through this function
	  * will be subject to pattern matching inside. Atm there is only one single case which tried to match the incoming HttpRequest with
	  * the structure of an HttpRequest with the method GET
	  */
	val requesthandler: HttpRequest => HttpResponse = {
		//case HttpRequest(method, uri, HTTP headers, content, protocol)
		case HttpRequest(HttpMethods.GET, _, _, _, _) =>
			HttpResponse(
				StatusCodes.OK, //HTTP 200
				entity = HttpEntity( //Payload
					ContentTypes.`text/html(UTF-8)`, //backticks allow us to use variable names with arbitrary characters including slashes and normally forbidden characters
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">Hello from Akka Http!!!</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		case request : HttpRequest =>
			request.discardEntityBytes()
			HttpResponse(
				StatusCodes.NotFound,
				entity = HttpEntity(
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : red;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">404 resource cant be found!</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
	}
	val httpSyncConnectionHandler = Sink.foreach[IncomingConnection](connection =>
		connection.handleWithSyncHandler(requesthandler)
	)

	/**
	  * This start and runs a runnable graph,  an akka stream
	  * the source : Http().bind(name, port), a source of incoming connections
	  * the sink : httpSyncConnectionHandler, a sink of incoming connections
	  */
	//	Http().bind(name, port).runWith(httpSyncConnectionHandler)

	/**
	  * Shorthand
	  */
	Http().bindAndHandleSync(requesthandler, name, port)
}


object async_{
	import LLAPILesson.system
	import LLAPILesson.name
	import system.dispatcher
	val port = 3001

	/**
	  * We defined a partial function that turns an HttpRequest into an HttpResponse. And any HttpRequest that is fed through this function
	  * will be subject to pattern matching inside. Atm there is only one single case which tried to match the incoming HttpRequest with
	  * the structure of an HttpRequest with the method GET
	  */
	val asyncRequesthandler: HttpRequest => Future[HttpResponse] = {
		//case HttpRequest(method, uri, HTTP headers, content, protocol)
		case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) =>
			Future(HttpResponse(
				StatusCodes.OK, //HTTP 200
				entity = HttpEntity( //Payload
					ContentTypes.`text/html(UTF-8)`, //backticks allow us to use variable names with arbitrary characters including slashes and normally forbidden characters
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : green;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">Home UWU</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			))//need a dispatcher but we dont want to use system.dispatcher unless we might starve threads on the actor system
		case HttpRequest(HttpMethods.GET, _, _, _, _) =>
			Future(HttpResponse(
				StatusCodes.OK, //HTTP 200
				entity = HttpEntity( //Payload
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">Hello from Akka Http!!!</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			))
		case request : HttpRequest =>
			request.discardEntityBytes()
			Future(HttpResponse(
				StatusCodes.NotFound,
				entity = HttpEntity(
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : red;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">404 resource cant be found!</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			))
	}
	val httpAsyncConnectionHandler = Sink.foreach[IncomingConnection](connection =>
		connection.handleWithAsyncHandler(asyncRequesthandler)
	)

	//streams based version
	Http().bind(name, port).runWith(httpAsyncConnectionHandler)

	//shorthand version
	Http().bindAndHandleAsync(asyncRequesthandler, name, port)
}

object async_streams {
	import LLAPILesson.system
	import LLAPILesson.name
	import system.dispatcher
	val port = 3002

	/**
	  * We defined a partial function that turns an HttpRequest into an HttpResponse. And any HttpRequest that is fed through this function
	  * will be subject to pattern matching inside. Atm there is only one single case which tried to match the incoming HttpRequest with
	  * the structure of an HttpRequest with the method GET
	  */
	val streamsBasedRequesthandler: Flow[HttpRequest, HttpResponse, _] = Flow[HttpRequest].map{
		//case HttpRequest(method, uri, HTTP headers, content, protocol)
		case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) =>
			HttpResponse(
				StatusCodes.OK, //HTTP 200
				entity = HttpEntity( //Payload
					ContentTypes.`text/html(UTF-8)`, //backticks allow us to use variable names with arbitrary characters including slashes and normally forbidden characters
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : green;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">Home UWU</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		case HttpRequest(HttpMethods.GET, _, _, _, _) =>
			HttpResponse(
				StatusCodes.OK, //HTTP 200
				entity = HttpEntity( //Payload
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">Hello from Akka Http!!!</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		case request : HttpRequest =>
			request.discardEntityBytes()
			HttpResponse(
				StatusCodes.NotFound,
				entity = HttpEntity(
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : red;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">404 resource cant be found!</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
	}

	//longhand
//	Http().bind(name, port).runForeach {
//		connection => connection.handleWith(streamsBasedRequesthandler)
//	}

	//shorthand
	Http().bindAndHandle(streamsBasedRequesthandler, name, port)

}