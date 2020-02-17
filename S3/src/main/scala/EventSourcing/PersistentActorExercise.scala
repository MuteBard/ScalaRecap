package EventSourcing

import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.PersistentActor

import scala.collection.mutable
import scala.util.Random

object PersistentActorExercise extends App{
	/*
	Persistent actor for a voting station
	This voting station will receive commands in the form of a case class called vote
	Keep:
		- The citizen who voted
		- The poll: mapping between a canidate and number of received votes
		actor must be able to recover ist state
	 */
//	Solo
	Together
}

object Solo{


	val system = ActorSystem("VotingMachine")
	val votingMachine: ActorRef = system.actorOf(Props[VotingMachine], "votingMachine")

	object Simulation {
		for (_ <- 1 to 10000){
			val random = new Random
			val citizenPID = random.alphanumeric take 10 mkString
			val x = random.nextInt(4)
			val canidate = x match {
				case 0 => "Jared A."
				case 1 => "Erin H."
				case 2 => "Josslyn L."
				case 3 => "Carl S."
			}
			votingMachine ! Vote(citizenPID, canidate)
		}
	}

	case class Vote(citizenPID : String, candidate: String)
	case class VoteEventRecorded(recordedVoteID : Int, citizenPID : String, candidate: String)
	case object Results
	case object Shutdown

	class VotingMachine extends PersistentActor with ActorLogging{

		var latestVoteId : Int = 0
		var tally : Map[String, Int] = Map().withDefaultValue(0)

		override def persistenceId : String = "voting-machine"
		override def receiveCommand: Receive = {
			case Results =>
				log.info(s"Vote Breakdown: $tally")
			case Shutdown =>
				context.stop(self)
			case Vote(citizenPID, candidate) =>
				log.info(s"$citizenPID, you have casted your vote for $candidate, vote# $latestVoteId")
				val event = VoteEventRecorded(latestVoteId, citizenPID, candidate)
				persist(event){ e => //Event sourcing pattern
					val aVote = e.candidate -> (tally(e.candidate) + 1)
					tally = tally + aVote
					latestVoteId += 1
					log.info(s"Persisted $e \n\tAs info:#${e.citizenPID}. Preserved vote for : ${e.candidate}")
				}
		}

		override def receiveRecover: Receive = {
			case VoteEventRecorded(id, citizenPID, candidate) =>
				log.info(s"Recovered invoice #$id of $citizenPID who casted vote for $candidate")
				latestVoteId = id
				val aVote = candidate -> (tally(candidate) + 1)
				tally = tally + aVote
		}
	}

//	votingMachine ! Results
//	Simulation

}
object Together{


	val system = ActorSystem("VotingMachine")
	val votingStation: ActorRef = system.actorOf(Props[VotingStation], "votingstation")

	object Simulation {
		import VotingStation._
		for (_ <- 1 to 1000){
			val random = new Random
			val citizenPID = random.alphanumeric take 2 mkString
			val x = random.nextInt(4)
			val canidate = x match {
				case 0 => "Jared A."
				case 1 => "Erin H."
				case 2 => "Josslyn L."
				case 3 => "Carl S."
			}
			votingStation ! Vote(citizenPID, canidate)
		}
	}



	object VotingStation{
		case class Vote(citizenPID: String, candidate: String)
		case object Print
	}

	class VotingStation extends PersistentActor with ActorLogging{
		import VotingStation._


		val citizens: mutable.Set[String] = new mutable.HashSet[String]()
		val poll : mutable.Map[String, Int] = new mutable.HashMap[String, Int]()

		override def persistenceId: String = "voting-station"

		override def receiveCommand: Receive = {
			case vote @ Vote(_,_) =>

			if (!citizens.contains(vote.citizenPID)){
				persist(vote) { _ => //Command sourcing pattern
					log.info(s"Persisted $vote")
					handleInternalStateChange(vote)
				}
			}
			case Print =>
				log.info(s"Current State:\nCitizens:\t$citizens\nVotes counted:\t${citizens.toList.size}\nPoll:\t$poll")
		}

		override def receiveRecover: Receive = {
			case vote @ Vote(_,_) =>
				log.info(s"Recovered $vote")
				handleInternalStateChange(vote)
		}

		def handleInternalStateChange(vote : Vote): Unit ={
			citizens.add(vote.citizenPID)
			val votes = poll.getOrElse(vote.candidate, 0)
			poll.put(vote.candidate, votes + 1)
		}

	}

	import VotingStation._
	votingStation ! Print
//	Simulation
} 