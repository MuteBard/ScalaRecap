package low_level_server

import akka.pattern.ask
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.util.Timeout
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps


object LowLevelRest extends App {
	LLRLesson
}


case class Bug(name: String, bells: Int)

trait BugStoreJsonProtocol extends DefaultJsonProtocol{
	implicit val bugFormat: RootJsonFormat[Bug] = jsonFormat2(Bug) //two parameters Bug(name: String, bells: Int) therefore jsonFormat2
}

object BugDB {
	case class CreateBug(bug: Bug)
	case class BugCreated(id: Int)
	case class FindBug(id: Int)
	case object FindAllBugs

}
class BugDB extends Actor with ActorLogging{
	import BugDB._
	var bugs: Map[Int, Bug] = Map()
	var currentBugId = 0

	override def receive: Receive = {
		case FindAllBugs =>
			log.info("searching for all bugs")
			sender() ! bugs.values.toList
		case FindBug(id)=>
			log.info(s"Searching bug by id:")
			sender() ! bugs.get(id)
		case CreateBug(bug) =>
			log.info(s"Adding bug $bug with id $currentBugId")
			bugs = bugs + (currentBugId -> bug)
			sender() ! BugCreated(currentBugId)
			currentBugId += 1
	}
}

object LLRLesson extends BugStoreJsonProtocol {
	import BugDB._
	implicit val system = ActorSystem("LowLevelRestAPI")
	val bugDb = system.actorOf(Props[BugDB], "LowLevelBugDB")
	val bugList = List(
		Bug("Common Butterfly", 90),
		Bug("Tiger Butterfly", 160),
		Bug("Migratory Locust", 3000)
	)

	bugList.foreach { bug =>
		bugDb ! CreateBug(bug)
	}
	import system.dispatcher

	/**
	  * GET on localhost:8080/api/bug => ALL the bugs in the store
	  * POST on localhost:8080/api/bug => insert the bug into the store
	  */

	val simpleBug = Bug("Common Butterfly", 200)
	println(simpleBug.toJson.prettyPrint)

//	{
//		"bells": 200,
//		"name": "Common Butterfly"
//	}

	val simpleBugString =
		"""
		  |{
		  |	    "bells": 200,
		  |	    "name": "Common Butterfly"
		  |}
		  |""".stripMargin

	println(simpleBugString.parseJson.convertTo[Bug])
	//Bug(Common Butterfly,200)

	/**
	  * Server code
	  * Whenever you need to interact with an external resource, ude futures because otherwise the response times will be bad
	  */

	//partial function
	implicit val defaultTimeout = Timeout(2 seconds)
	val requestHandler : HttpRequest => Future[HttpResponse] = {
		case HttpRequest(HttpMethods.GET, Uri.Path("/api/bug"), _, _, _) =>
			val bugsFuture: Future[List[Bug]] = (bugDb ? FindAllBugs).mapTo[List[Bug]]
			bugsFuture.map {
				bugs =>
					HttpResponse(
						entity = HttpEntity(
							ContentTypes.`application/json`,
							bugs.toJson.prettyPrint
						)
					)
			}
		case HttpRequest(HttpMethods.POST, Uri.Path("/api/bug"), _, entity, _) =>
			val strictEntityFuture = entity.toStrict(3 seconds)
			strictEntityFuture.flatMap{
				strictEntity =>
					val bugJsonString = strictEntity.data.utf8String
					val bug = bugJsonString.parseJson.convertTo[Bug]
					val bugCreatedFuture = (bugDb ? CreateBug(bug)).mapTo[BugCreated]
					bugCreatedFuture.map {
						bugCreate =>
							HttpResponse(StatusCodes.OK)
					}
			}
			val bugsFuture: Future[List[Bug]] = (bugDb ? FindAllBugs).mapTo[List[Bug]]
			bugsFuture.map {
				bugs =>
					HttpResponse(
						entity = HttpEntity(
							ContentTypes.`application/json`,
							bugs.toJson.prettyPrint
						)
					)
			}

		/**
		  * Important because if you dont reply to an existing request that will be interpreted as back pressure
		  */
		case request: HttpRequest =>
			request.discardEntityBytes()
				Future{
					HttpResponse(status = StatusCodes.NotFound)
				}

	}

	Http().bindAndHandleAsync(requestHandler, "localhost", 8080)
}

