package Infrastucture

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.dispatch.{ControlMessage, PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.{Config, ConfigFactory}

object Mailboxes extends App {


	val system = ActorSystem("MailboxDemo", ConfigFactory.load().getConfig("mailboxesDemo"))

	class SimpleActor extends Actor with ActorLogging {
		override def receive: Receive = {
			case message => log.info(message.toString)
		}
	}

	//interesting case 1
	class SupportTicketPriorityMailbox(settings : ActorSystem.Settings, config: Config)
		extends UnboundedPriorityMailbox(PriorityGenerator {
			case message: String if message.startsWith("[P0]") => 0
			case message: String if message.startsWith("[P1]") => 1
			case message: String if message.startsWith("[P2]") => 2
			case message: String if message.startsWith("[P3]") => 3
			case _ => 4

		})
	//step 2 - make it known in the config
	//step 3 - attach the dispatcher to an actor


	val supportTicketLogger = system.actorOf(Props[SimpleActor].withDispatcher("support-ticket-dispatcher"))
//	supportTicketLogger ! "[P3] this would be nice to have"
//	supportTicketLogger ! "[P0] WTF"
//	supportTicketLogger ! "[P1] please fix this"
//	supportTicketLogger ! "[P0] WTF"

//reordered messages
//		[INFO] [02/09/2020 18:02:59.079] [MailboxDemo-support-ticket-dispatcher-5] [akka://MailboxDemo/user/$a] [P0] WTF
//		[INFO] [02/09/2020 18:02:59.080] [MailboxDemo-support-ticket-dispatcher-5] [akka://MailboxDemo/user/$a] [P0] WTF
//		[INFO] [02/09/2020 18:02:59.080] [MailboxDemo-support-ticket-dispatcher-5] [akka://MailboxDemo/user/$a] [P1] please fix this
//		[INFO] [02/09/2020 18:02:59.080] [MailboxDemo-support-ticket-dispatcher-5] [akka://MailboxDemo/user/$a] [P3] this would be nice to have

	//interesting case 2
	//we will use the UnboundedControlAwareMailbox

	//step 1 - mark important messages as control messages
	case object ManagementTicket extends ControlMessage

	//step 2 - configure who gets the mailbox
	//- make the actor attach to the mailbox

	val controlAwareActor = system.actorOf(Props[SimpleActor].withMailbox("control-mailbox"))

	controlAwareActor ! "[P0] WTF"
	controlAwareActor ! "[P3] this would be nice to have"
	controlAwareActor ! "[P0] WTF"
//	controlAwareActor !  ManagementTicket
}