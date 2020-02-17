package EventSourcing

import EventSourcing.Lesson.withoutSnapshots.Simulation
import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}

import scala.collection.mutable

object Snapshots extends App {

	/*
	Long-lived entities take a long time to recover
	Solution: save check points
	 */
	Lesson.withSnapshots


}


object Lesson{

	object withoutSnapshots{
		//commands
		case class ReceivedMessage(contents: String) //message FROM your contact
		case class SentMessage(contents: String) //message TO your contact
		//events
		case class ReceiveMessageRecord(id : Int, contents: String)
		case class SentMessageRecord(id : Int, contents: String)

		object Chat{
			def props(owner : String, contact : String) = Props(new Chat(owner, contact))
		}

		class Chat(owner : String, contact: String) extends PersistentActor with ActorLogging {
			val MAX_MESSAGES = 10
			var currentMessageId = 0
			var lastMessages = new mutable.Queue[(String, String)]()

			override def persistenceId: String = s"$owner-$contact-chat"

			override def receiveCommand: Receive = {
				case ReceivedMessage(contents) =>
					persist(ReceiveMessageRecord(currentMessageId, contents)){
						_ =>

							log.info(s"Received message: $contents")
							AddQueue(contact, contents)
							currentMessageId += 1
					}
				case SentMessage(contents) =>
					persist(SentMessageRecord(currentMessageId, contents)){
						_ =>
							log.info(s"Received message: $contents")
							AddQueue(owner, contents)
							currentMessageId += 1
					}
			}

			override def receiveRecover: Receive = {
				case ReceiveMessageRecord(id, contents) =>
					log.info(s"Recovered received message $id: $contents")
					AddQueue(contact, contents)
					currentMessageId = id
				case SentMessageRecord(id, contents) =>
					log.info(s"Recovered sent message $id: $contents")
					AddQueue(owner, contents)
					currentMessageId = id
			}

			def AddQueue(member : String, contents : String): Unit ={
				if(lastMessages.size >= MAX_MESSAGES){
					lastMessages.dequeue()
				}
				lastMessages.enqueue((member, contents))

			}


		}

		object Simulation {
			val system = ActorSystem("SnapshotsDemo")
			val chat = system.actorOf(Chat.props("Mutebard", "Nerine"))
			for (i <- 1 to 100000) {
				chat ! ReceivedMessage(s"Akka Rocks $i")
				chat ! SentMessage(s"Akka Rules $i")
			}
		}

		Simulation
	}

	/**
	  * With every 10 received commands, we are going to persist the entire state in a dedicated persistent
	  * store called a snapshot store
	  */
	object withSnapshots{
		//commands
		case class ReceivedMessage(contents: String) //message FROM your contact
		case class SentMessage(contents: String) //message TO your contact
		//events
		case class ReceiveMessageRecord(id : Int, contents: String)
		case class SentMessageRecord(id : Int, contents: String)

		object Chat{
			def props(owner : String, contact : String) = Props(new Chat(owner, contact))
		}

		class Chat(owner : String, contact: String) extends PersistentActor with ActorLogging {
			val MAX_MESSAGES = 10
			var currentMessageId = 0
			var lastMessages = new mutable.Queue[(String, String)]()
			var commandsWithoutCheckpoint = 0

			override def persistenceId: String = s"$owner-$contact-chat"

			override def receiveCommand: Receive = {
				case ReceivedMessage(contents) =>
					persist(ReceiveMessageRecord(currentMessageId, contents)){
						_ =>

							log.info(s"Received message: $contents")
							addQueue(contact, contents)
							currentMessageId += 1
							addCheckpoint()
					}
				case SentMessage(contents) =>
					persist(SentMessageRecord(currentMessageId, contents)){
						_ =>
							log.info(s"Received message: $contents")
							addQueue(owner, contents)
							currentMessageId += 1
							addCheckpoint()
					}
				case "print" =>
					log.info(s"Most recent messages: $lastMessages")

			}

			override def receiveRecover: Receive = {
				case ReceiveMessageRecord(id, contents) =>
					log.info(s"Recovered received message $id: $contents")
					addQueue(contact, contents)
					currentMessageId = id
				case SentMessageRecord(id, contents) =>
					log.info(s"Recovered sent message $id: $contents")
					addQueue(owner, contents)
					currentMessageId = id
				case SnapshotOffer(metadata, contents) => {
					log.info(s"Recovered Snapshot: $metadata")

					/**
					  * Take all the pairs from the contents payload
					  * and enqueue them in lastMessages queur
					  *
					  */
					contents.asInstanceOf[mutable.Queue[(String, String)]].foreach(lastMessages.enqueue(_))
				}
				case SaveSnapshotSuccess(metadata) => log.info(s"Saving Snapshot succeeded: $metadata")
				case SaveSnapshotFailure(metadata, reason) => log.info(s"Saving Snapshot failed: $metadata due to $reason")

			}

			def addQueue(member : String, contents : String): Unit ={
				if(lastMessages.size >= MAX_MESSAGES){
					lastMessages.dequeue()
				}
				lastMessages.enqueue((member, contents))

			}
			def addCheckpoint():Unit = {
				commandsWithoutCheckpoint += 1
				if(commandsWithoutCheckpoint >= MAX_MESSAGES){
					log.info("Saving checkpoint")
					//allows persisting anything that is serializable to a dedicated persistent store
					saveSnapshot(lastMessages)
					commandsWithoutCheckpoint = 0
				}
			}


		}

		object Simulation {
			val system = ActorSystem("SnapshotsDemo")
			val chat = system.actorOf(Chat.props("Mutebard", "Nerine"))
//			for (i <- 1 to 100000) {
//				chat ! ReceivedMessage(s"Akka Rocks $i")
//				chat ! SentMessage(s"Akka Rules $i")
//			}
			chat ! "print"
		}

		Simulation
	}

}
