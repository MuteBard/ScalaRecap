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
	case class InvoiceBulk(invoices : List[Invoice])
	//EVENTS (ACTOR to STORE)
	case class InvoiceRecorded(id : Int, recipient : String, date: Date, amount : Int)
	case object Shutdown
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
				persist(InvoiceRecorded(latestInvoiceID, recipient, date, amount)) /*time gap : all other messages to this actor  are stashed*/
				{e =>
					//safe to access mutable state here, akka persistence guarantees that no other threads are accessing the actor during callback
					latestInvoiceID += 1
					totalAmount += amount


					// correctly identify the sender of the command
//					sender() ! "PersistenceACK"
					log.info(s"Persisted $e \n\tAs info:#${e.id}\n\tfor total amount $totalAmount")
				}
				//act like a normal actor
			case "print" =>
				log.info(s"Latest invoice id: $latestInvoiceID, total amount: $totalAmount")


			case InvoiceBulk(invoices) =>
				/*
				1) create events (plural)
				2) persist all the events
				3) update the actor state when each event is persisted
				 */
			//generate a range of invoiceID starting from my latestInvoiceID written in my current state
			//check what .zip does again
				val invoiceIds = latestInvoiceID to (latestInvoiceID + invoices.size)
				val events = invoices.zip(invoiceIds).map { pair =>
					val id = pair._2
					val invoice = pair._1

					InvoiceRecorded(id, invoice.recipient, invoice.date, invoice.amount)
				}
					//this is a collections of invoice recorded events

				persistAll(events){
					e =>
						latestInvoiceID += 1
						totalAmount += e.amount
						log.info(s"Persisted single $e \n\tAs info:#${e.id}\n\tfor total amount $totalAmount")

				}
			case Shutdown =>
				context.stop(self)

		}
		//this will be put into the normal mailbox Queue

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

	/*
	Persistence failures
		- There are two types of persistence failures
		1)
		the call persist() throws an error
		2)
		the journal implementation actually fails to persist a message

		The persistent actor abstract class has some callbacks in case persisting failed and we have two that are most interesting
    */

        //should persist() fail, onPersistFailure will be called on the throwable with the event that was attempted to be persisted in the sequence number of
		//the event from the Journal's point of view. the actor will be stopped

		//best practice, start the actor again after awhile use backoff supervisor pattern

		override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
			log.error(s"failed to persist $event because of $cause")
			super.onPersistFailure(cause, event, seqNr)
		}

		//Called if the Journal throws an exception while persisting the event
		//The actor is resumed, not stopp
		override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
			log.error(s"failed to persist $event because of $cause")
			super.onPersistRejected(cause, event, seqNr)
		}



		/*
		Persisting multiple events
		Assume in our little accounting case that the domain and the data models, thats the definitions of invoice and invoice recorded were
		written elsewhere by someone else by some library or by some other library or by some programmer that you dont have access to.
		And for some reason, you want to send bulk messages, so a list of invoices to this accountant, so that it can persist multiple
		invoice recorded events at the same time. There is a solution for that and it is called persist all
		 */


	}

	val system = ActorSystem("PersistentActors")
	val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

//	for (i <- 1 to 10) {
//		accountant ! Invoice("NINTENDO", new Date, i * 1000)
//	}

//	val newInvoices = for (i <- 1 to 5) yield Invoice("Headache from this.tm", new Date, i * 2000)
//	accountant ! InvoiceBulk(newInvoices.toList)


//NEVER EVER CALL PERSISTALL OR PERSIST FROM FUTURES
//Otherwise you risk breaking the actor encapsulation because the actor thread is free to process messages while you are persisting.
//And if the normal actor thread also calls persist, you suddenly have two threads persisiting events simultanously, risking corrupting the actor state

/*
Shut down of persist actors is a bit involved.
during the time gap between persisting and the actual call back, all other messages sent to the actor will be stashed
However the poison pill and kill messages are handled seperately in a seperate mailbox so you risk killing the actor before
it's actually done persisting, you will see ever single invoice sent to dead letters

best practice is to define your own shutdown messages
 */




}
