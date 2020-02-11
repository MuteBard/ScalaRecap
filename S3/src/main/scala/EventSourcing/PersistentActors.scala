package EventSourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistentActors extends App {

	/*
	Business:
		- An accountant which keeps track of our invoices
	 */

	//COMMANDS (USER/ACTOR to ACTOR)
	case class Invoice(recipient : String, date: Date, amount : Int)
	//EVENTS (ACTOR to STORE)
	case class InvoiceRecorded(id : Int, recipient : String, date: Date, amount : Int)
	class Accountant extends PersistentActor with ActorLogging{
		//Persistent actors are special from Actors in that they have not one but three essential methods

		/*
		PersistenceId:
		How the events persisted by this actor will be identified
		In the persistent store we'll have lots and lots of events grouped in one place so we will have to know which actor wrote what event
		The Persistence Id should be unique ofc for each actor
		 */

		var latestInvoiceID = 0
		var totalAmount = 0

		override def persistenceId: String = "simple-accountant" // best practice to make it unique

		/*
		This is the "normal" receive method that you would work with in normal Actors
		 */
		override def receiveCommand: Receive = {
			case Invoice(recipient, date, amount) =>
				/*
				When you receive a command
				1) you create an EVENT to persist into the store
				2) you persist that EVENT, then pass in a callback that will get triggered oncethe event is written
				3) update the actor state when the event has persisted
				 */
				log.info(s"Receive invoice for amount: $amount")

				//1
//				val event = InvoiceRecorded(latestInvoiceID, recipient, date, amount)

				//2 takes two parameters: the event and a handler that will be called after the persist has succeeded
//				persist(event) {e =>
//					latestInvoiceID += 1
//					totalAmount += amount
//					log.info(s"Persisted $e \n\t As info:#${e.id}\n\tfor total amount $totalAmount")
//				}
				persist(InvoiceRecorded(latestInvoiceID, recipient, date, amount)) /*time gap : all other messages to this actor  are stashed*/ {e =>
					//safe to access mutable state here, akka persistence guarentees that no other threads are accessing the actor during callback
					latestInvoiceID += 1
					totalAmount += amount


					// correctly identify the sender of the command
					sender() ! "PersistenceACK"
					log.info(s"Persisted $e \n\tAs info:#${e.id}\n\tfor total amount $totalAmount")
				}

				//12:20
		}

		/*
		Handler that will be called on recovery.
		On recovery, the actor will query the persistent store for all the events associated to this persistence ID and all theevents will be
		replayed by the actor. This method is where these messages will be sent, as simple messages.
		 */
		override def receiveRecover: Receive = {
			/*
			best practice: follow the logic in the persist steps of receiveCommand
			As a reaction to commands, this actor will only record or persist invoice recorded messages
			 */
			case InvoiceRecorded(id, _, _, amount) =>
				log.info(s"Recovered invoice #$id for amount $amount, total amount: $totalAmount")
				latestInvoiceID = id
				totalAmount += amount
		}

	}

	val system = ActorSystem("PersistentActors")
	val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

//	for (i <- 1 to 10) {
//		accountant ! Invoice("NINTENDO", new Date, i * 1000)
//	}



}
