package StoresAndSerialization

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, RecoveryCompleted, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}
import com.typesafe.config.ConfigFactory

object LocalStores extends App{
	LessonLocalStores.simulation
}


object LessonLocalStores{

	object simulation{
		import persistenceActor._
		val localStoresActorSystem = ActorSystem("localStoresSystem", ConfigFactory.load().getConfig("localStores"))
		val actor = localStoresActorSystem.actorOf(Props[SimplePeristentActor], "spActor" )
		for (i <- 1 to 10){
			actor ! s"I love Akka [$i]"
		}
		actor ! "print"
		actor ! "snap"

		for(i <- 11 to 20){
			actor ! s"I love Akka [$i]"
		}
	}
	object persistenceActor {

		class SimplePeristentActor extends PersistentActor with ActorLogging{
			override def persistenceId: String = "simple-persistent-actor"

			//mutable state
			var nMessages = 0

			override def receiveCommand: Receive = {
				case "print" =>
					log.info(s"I have persisted $nMessages so far")
				case "snap" =>
					saveSnapshot(nMessages)
				case SaveSnapshotSuccess(metadata) =>
					log.info(s"Snapshot successful; $metadata")
				case SaveSnapshotFailure(_, cause) =>
					log.warning(s"Snapshot failed: $cause")
				case message => persist(message){ _ =>
					log.info(s"Persisting $message")
					nMessages += 1
				}
			}

			override def receiveRecover: Receive = {
				case SnapshotOffer(metadata, payload: Int) =>
					log.info(s"Recovered snapshot: $payload")
					nMessages = payload
				case message =>
					log.info(s"Recovered $message")
					nMessages += 1
				case RecoveryCompleted =>
					log.info("Recovery Done")
			}
		}



	}
}