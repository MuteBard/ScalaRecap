package high_level_server

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, StatusCodes}

object DirectivesBreakdown extends App{
	implicit val system = ActorSystem("System")
	import system.dispatcher
	import akka.http.scaladsl.server.Directives._


	/**
	  * Type #1: filtering directives
	  */

	val simpleHttpMethodRoute =
		post { //get, put, patch, delete, head, options
			complete(StatusCodes.Forbidden)
		}

	val simplePathRoute =
		path("about"){
			complete(
				HttpEntity(
					ContentTypes.`application/json`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">ABOUT</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		}

	val complexPathRoute =
		path("api" / "home"){
			complete(
				HttpEntity(
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">HOME</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		} ~
		path("api" / "about"){
			complete(
				HttpEntity(
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">ABOUT</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		}


	val pathEndRoute =
		pathEndOrSingleSlash{
			complete(
				HttpEntity(
					ContentTypes.`text/html(UTF-8)`,
					"""
					  |<html>
					  | <style>
					  | .textStyle {
					  |     color : blue;
					  | }
					  | </style>
					  | <body>
					  |   <h1 class="textStyle">UWU</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		}

	/**
	  * Type #2: extracting directives
	  */


	val pathExtractionRoute =
		path("api" / "item" / IntNumber){
			itemNumber : Int =>
				println(s"Ive got a number in my path: $itemNumber")
				complete(StatusCodes.OK)
		}

	val pathMultiExtractRoute =
		path("api" / "order" / IntNumber / IntNumber){
			(id, inventory) =>
				println(s"Ive got two numbers in my path:\nid : $id\ninventory : $inventory")
				complete(StatusCodes.OK)

		}

	//   api/item?id=45
	val queryParamsExtractionRoute =
		path("api" / "item") {
			parameter("id".as[Int]) {
				itemId: Int =>
					println(s"I have extracted the query parameter Id: $itemId")
					complete(StatusCodes.OK)
			}
		}

	val extractRequestRoute =
		path("controlEndpoint"){
			extractRequest { httpRequest : HttpRequest =>
				extractLog { log : LoggingAdapter =>
					log.info(s"I got the http: request: $httpRequest")
					complete(StatusCodes.OK)
				}

			}
		}
	/**
	  * Type #3: composite directives
	  */

	val simpleNestedRoute =
		path("api" / "item")
			get {
				complete(StatusCodes.OK)
			}

	/*
	Because the path api/item is a filtering directive and the get is also a filtering directive, this composite directive below is also
	a filtering directive and any HTTP request that goes through this route has to match both conditions. You can chain as many directives as you
	want with this & and actually include extraction directives.
	 */
	val compactSimpleNestedRoute = (path("api" / "item") & get) {
		complete(StatusCodes.OK)
	}

	val compactExtractRequestRoute =
		(path("controlEndpoint") & extractRequest & extractLog){
			(request, log) =>
				log.info(s"I got the http: request: $request")
				complete(StatusCodes.OK)
		}

	// /about and /aboutUs
	val repeatedRoute =
		path("about") {
			complete(StatusCodes.OK)
		} ~
		path("aboutUs"){
			complete(StatusCodes.OK)
		}

	val dryRoute =
		(path("about") | path("aboutUs")){
			complete(StatusCodes.OK)
		}



	val blogByIdRoute =
		path(IntNumber) {
			blogpostId =>
				complete(StatusCodes.OK)
		}

	val blogByQueryParameters =
		parameter("postId".as[Int]) {
			blogpostId =>
				complete(StatusCodes.OK)
		}

	//same type extracted
	val combinedBlogByIdRoute =
		(path(IntNumber) | parameter("postId".as[Int])){
			blogpostId =>
				complete(StatusCodes.OK)
		}

	/**
	  * Type #4 : actionable directives
	  */

	val completeOkRoute = complete(StatusCodes.OK)

	val failedRoute =
		path("notSupported"){
			failWith(new RuntimeException("Unsupported"))
		}

	//reject
	/**
	  * rejecting a request means handling it over to the next possible handler in the routing tree. rejection happens automatically when a request doesnt
	  * match a directive but we can also reject a request manually
	  */

	val routeWithRejection =
		path("home"){
			reject
		} ~
		path("index"){
			completeOkRoute
		}

	Http().bindAndHandle(extractRequestRoute, "localhost", 8080)

}
