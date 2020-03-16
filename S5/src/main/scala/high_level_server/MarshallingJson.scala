package high_level_server

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import spray.json._

import scala.concurrent.duration._


case class Player(nickname: String, characterClass : String, level : Int)



trait PlayerJsonProtocol extends DefaultJsonProtocol {
	implicit val playerFormat = jsonFormat3(Player)
}

object GameAreaMap{
	case object GetAllPlayers
	case class GetPlayer(nickname : String)
	case class GetPlayersByClass(characterClass : String)
	case class AddPlayer(player: Player)
	case class RemovePlayer(player : Player)
	case object OperationSuccess
}


class GameAreaMap extends Actor with ActorLogging {
	import GameAreaMap._

	var players = Map[String, Player]()

	override def receive: Receive = {
		case GetAllPlayers =>
			log.info("Getting all players")
			sender() ! players.values.toList

		case GetPlayer(nickname) =>
			log.info(s"Getting player with nickname $nickname")
			sender() ! players.get(nickname) //an option

		case GetPlayersByClass(characterClass) =>
			log.info(s"Getting all players with the character class $characterClass")
			sender() ! players.values.toList.filter(_.characterClass == characterClass)

		case AddPlayer(player) =>
			log.info(s"Trying to add player $player")
			players = players + (player.nickname -> player)
			sender() ! OperationSuccess

		case RemovePlayer(player) =>
			log.info(s"Trying to remove $player")
			players = players - player.nickname
			sender() ! OperationSuccess
		case "hi" => sender() ! OperationSuccess
	}
}

object MarshallingJson extends App with PlayerJsonProtocol with SprayJsonSupport {
	implicit val system = ActorSystem("MarshallingJson")
	import system.dispatcher
	import GameAreaMap._


	val playerManager = system.actorOf(Props[GameAreaMap], "playerManager")
	val playerList = List(
		Player("Mu Ba'Gamnan", "BlackMage", 80),
		Player("Nerine Indrys", "Summoner", 80),
		Player("Specter Saru", "DarkKnight", 80)
	)


	playerList.foreach { player =>
		playerManager ! AddPlayer(player)
	}


	/**
	  * GET /api/player, returns all the players in the map, as JSON
	  * GET /api/player/nickname, returns the player witht he given nickname as JSON
	  * GET /api/player?nickname=X does the same
	  * GET /api/player/class/CharClass returns all the players with given character class
	  * POST /api/player with JSON payload, adds the player to the map
	  * DELETE /api/player with JSON payload, removes the player from the map
	  */

	implicit val timeout = Timeout(2 seconds)
	val playerManagementRoute =
		pathPrefix("api" / "player"){
			get{
				//GET /api/player/class/CharClass
				path("class" / Segment) {
					characterClass =>
					val playersByClassFuture = (playerManager ? GetPlayersByClass(characterClass)).mapTo[List[Player]]
					complete(playersByClassFuture)
				} ~
				// GET /api/player/nickname OR GET /api/player?nickname=X
				(path(Segment) | parameter("nickname")){
					nickname =>
					val playersByNickNameFuture = (playerManager ? GetPlayer(nickname)).mapTo[List[Player]]
					complete(playersByNickNameFuture)
				} ~
				//GET /api/player
				pathEndOrSingleSlash {
					complete((playerManager ? GetAllPlayers).mapTo[List[Player]])

				}
			} ~
			post{
				//converts  payload to player Datastructure

				entity(as[Player]) {
					player =>
						complete((playerManager ? AddPlayer(player)).map(_ => StatusCodes.OK))
				}
			}~
			delete{
				entity(as[Player]){
					player =>
						complete((playerManager ? RemovePlayer(player)).map(_ => StatusCodes.OK))
				}
			}
		}

	Http().bindAndHandle(playerManagementRoute, "localhost", 3000)
}

