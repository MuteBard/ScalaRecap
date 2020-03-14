package high_level_server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route

object HighLevelIntro extends App{

	implicit val system = ActorSystem("system")
	import system.dispatcher

	/**
	  * We are going to explore a completely different way of constructing services using the powerful Akka routing DSL
	  */

	//directives
	import akka.http.scaladsl.server.Directives._

	val simpleRoute: Route =
		path("home"){
			complete(StatusCodes.OK)
		}

	val pathGetRoute: Route =
		path("home"){
			get{
				complete(StatusCodes.OK)
			}
		}


	//chaining directives with ~
	val chainedRoute = {
		path("api"){
			get{
				complete(StatusCodes.OK)
			} ~
			post{
				complete(StatusCodes.Forbidden)
			}
		} ~
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
					  |   <h1 class="textStyle">Thank god for better syntax and less boilerplate</h1>
					  | </body>
					  |</html>
					  |""".stripMargin
				)
			)
		}
	} //routing trees

	//BindAndHandle takes a flow from HTTP request to HTTP response, but thankfully this route over here can be imlicitly converted to a flow

//	Http().bindAndHandle(simpleRoute, "localhost", 8080)
	Http().bindAndHandle(chainedRoute, "localhost", 8080)

	/**
	  * path("home") this is a directive
	  * complete call is also called a directive
	  *
	  * directives are building blocks of high level akka http server logic
	  * So we will build our server logic based on these directives.
	  * Directives specify what happens under which conditions
	  *
	  * Route is the same thing as the request context
	  */
}
