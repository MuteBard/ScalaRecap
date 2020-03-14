package high_level_server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._

import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.language.postfixOps
import spray.json._


case class Person(pin: Int, name: String)

trait PersonJsonProtocol extends DefaultJsonProtocol{
	implicit val personJson = jsonFormat2(Person)
}

object HighlevelExample extends App with PersonJsonProtocol{

	implicit val system = ActorSystem("HighLevelExample")
	import system.dispatcher


	/**
	  * Exercise:
	  * - GET /api/people: retrieve ALL the people you have registered
	  * - GET /api/people/pin retrieve the person with this pin, return as JSON
	  * - GET /api/people?pin=x (same)
	  * - POST /api/people with a JSON payload denoting a person,add that person to your database
	  *
	  * steps:
	  *  1. Add JSON support for person early
	  *  2. Set up the server route early
	  */


	var people = List(
		Person(1, "Alice"),
		Person(2, "Bob"),
		Person(3, "Charlie"),
		Person(4, "Erin"),
		Person(5, "Carl")
	)

	println(people.toJson.prettyPrint)

	/**
	  * First steps is that we add:
	  * import spray.json._
	  * then we will put our case class for person outside this HighlevelExample object. we will need our interface for converting this person
	  * to JSON
	  *
	  *
	  * import spray.json._
	  * case class Person(pin: Int, name: String)
	  *
	  * trait PersonJsonProtocol extends DefaultJsonProtocol{
	  * implicit val personJson = jsonFormat2(Person)
	  * }
	  *
	  * object HighlevelExample extends App with PersonJsonProtocol{...
	  */

	val personServerRoute =
		pathPrefix("api" / "people"){
			get{
				(path(IntNumber) | parameter("pin".as[Int])) {
					pin => //TODO 1: fetch the person with the pin
					complete(
						HttpEntity(
							ContentTypes.`application/json`,
							people.find(_.pin == pin).toJson.prettyPrint

						)
					)
				} ~
				pathEndOrSingleSlash{
					complete(
						HttpEntity(
							ContentTypes.`application/json`,
							people.toJson.prettyPrint

						)
					)
				}
			} ~
			(post & pathEndOrSingleSlash & extractRequest & extractLog){
				(request, log) =>
				val entity = request.entity
				val strictEntityFuture = entity.toStrict(2 seconds)
				val personFuture = strictEntityFuture.map{
					strictEntity => strictEntity.data.utf8String.parseJson.convertTo[Person]
				}

				onComplete(personFuture){
					case Success(person) =>
						log.info(s"Got person: $person")
						people = people :+ person
						complete(StatusCodes.OK)
					case Failure(ex) =>
						log.warning(s"Something failed with fetching with the person from the entity: $ex")
						failWith(ex)
				}
			}
		}

	Http().bindAndHandle(personServerRoute, "localhost", 8080)
}
