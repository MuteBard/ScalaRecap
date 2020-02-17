package EventSourcing

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.PersistentActor

object MultiplePersists extends App {
	/*
	Diligent accountant: with every invoice, will persist TWO events
	- a tax record for the fiscal authority
	- an invoice record for personal logs or auditing authority
	*/

	Simulation

	object Simulation {
		val system = ActorSystem("System")
		val taxAuthority = system.actorOf(Props[TaxAuthority], "IRS")
		val accountant = system.actorOf(DiligentAccountant.props("IRXJ24C", taxAuthority))
		accountant ! Invoice("The Sofa Company", new Date, 2450)
	}

	//COMMAND
	case class Invoice(recipient : String, date : Date, amount : Int)

	//EVENT
	case class TaxRecord(taxId : String, recordId : Int, date : Date, totalAmount: Int)
	case class InvoiceRecord(invoiceRecordId: Int, recipient : String, date : Date, amount : Int)

	object DiligentAccountant{
		def props(taxId : String, taxAuthority : ActorRef) = Props(new DiligentAccountant(taxId, taxAuthority))
	}



	//Tax Accountant
	class DiligentAccountant(taxId : String, taxAuthority : ActorRef) extends PersistentActor with ActorLogging{

		var latestTaxRecordId = 0
		var latestInvoiceRecordId = 0
		override def persistenceId : String = "diligent-accountant"
		override def receiveRecover: Receive = {
			case event => log.info(s"Recovered: $event")
		}
		override def receiveCommand: Receive = {
			case Invoice(recipient, date, amount) =>

				//journal ! TaxRecord (very rough approximation) EVENT A
				persist(TaxRecord(taxId, latestTaxRecordId, date, amount / 3)){
					taxRecord =>
						taxAuthority ! taxRecord
						latestTaxRecordId += 1
						persist("Tax record transfer complete and verified: SIGNATURE") {
							declaration =>
								taxAuthority ! declaration
						}
				}
				//journal ! InvoiceRecord (very rough approximation) EVENT B
				persist(InvoiceRecord(latestInvoiceRecordId, recipient, date, amount)){
					invoiceRecord =>
						taxAuthority ! invoiceRecord
						latestInvoiceRecordId += 1
						persist("Invoice record transfer complete and verified: SIGNATURE") {
							declaration =>
								taxAuthority ! declaration
						}
				}

			// Persistance is also based on message passing. Journals are actually implemented using actors
			/**
			  * Because you know that messages are ordered if the sender and the destination are the same.
			  * That means that this event A is being persisted before this event B is persisted. Even tho the calls to persist are
			  * asynchornous, the ordering of the events is guarenteed. Persistence is based on messages. Handelers for subsequent
			  * persist() calls are executed in order. A nested persist is executed after the enclosing persist
			  *
			  * [INFO] [02/14/2020 15:17:43.656] [System-akka.actor.default-dispatcher-4] [akka://System/user/IRS] Received TaxRecord(IRXJ24C,0,Fri Feb 14 15:17:43 EST 2020,816)
			  * [INFO] [02/14/2020 15:17:43.656] [System-akka.actor.default-dispatcher-4] [akka://System/user/IRS] Received InvoiceRecord(0,The Sofa Company,Fri Feb 14 15:17:43 EST 2020,2450)
			  * [INFO] [02/14/2020 15:17:43.657] [System-akka.actor.default-dispatcher-3] [akka://System/user/IRS] Received Tax record transfer complete and verified: SIGNATURE
			  * [INFO] [02/14/2020 15:17:43.659] [System-akka.actor.default-dispatcher-3] [akka://System/user/IRS] Received Invoice record transfer complete and verified: SIGNATURE
			  */

		}
	}

	//Tax Authority
	class TaxAuthority extends Actor with ActorLogging{
		override def receive : Receive = {
			case message => log.info(s"Received $message")
		}
	}


}
