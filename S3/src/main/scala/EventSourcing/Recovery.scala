package EventSourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, Recovery}

object Recovery extends App{

	LessonRecovery.p1
}

object LessonRecovery{

	object p1{


		case class Command(contents : String)
		case class Event(contents : String)


		class RecoveryActor extends PersistentActor with ActorLogging{
			override def persistenceId: String = "recovery-actor"

			override def receiveCommand: Receive = {
				case Command(contents) =>
					persist(Event(contents)){
						event => log.info(s"Successfully persisted event: $event")
					}
			}

			override def onRecoveryFailure(cause: Throwable, event: Option[Any]): Unit = {
				log.error("I failed at recovery")
				super.onRecoveryFailure(cause, event)
			}

			override def receiveRecover: Receive = {
				case Event(contents) =>
					log.info(s"Recovered: $contents")
			}

//			override def recovery: Recovery = Recovery(fromSnapshot = SnapshotsSelectionCriteria.Latest)
//			override def recovery: Recovery = Recovery.none
		}

		val system = ActorSystem("RecoveryDemo")
		val actor = system.actorOf(Props[RecoveryActor], "recoveryActor")
//		for (i <- 1 to 1000){
//			actor ! Command(s"command $i")
//		}

		//all  commands during recovery are stashed
		//If there is a failure during recovery, the actor is stopped becuase it cannot
		//be trusted anymore
	}


}