package Infrastucture

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.io.Udp.SO.Broadcast
import akka.routing.{ActorRefRoutee, FromConfig, RoundRobinGroup, RoundRobinPool, RoundRobinRoutingLogic, Router}
import com.typesafe.config.ConfigFactory

object AkkaRouters extends App {

	//Lesson.method1
	//Lesson.method2_Programmatic
    //Lesson.method2_Configuration
	//Lesson.method3_Programmatic
	Lesson.method3_Configuration
}

object Lesson {

	/*
	Supported options for routing logic
	- round robin : cycle between rootees
	- random : not smart
	- smallest mailbox : load balancing heuristic. It always sends the next message to the actor with the fewst messages in the queue
	- broadcast : sends the same message to all the routines
	- scatter-gather-first : broadcasts and waits for the first reply and all the other replies are discarded (GOOD)
	- tail-chopping : forwards the next message to each actor sequentially untill the first reply is received and all other replies are discarded
	- consistent-hashing : all messaged with the same hash get to the same actor


	RoutingLogic or Pool are suffixes for these
	*/

	//Most complicated and least in practice - Manual Router
	object method1 {
		val system = ActorSystem("System")
		val master = system.actorOf(Props[Master], "master")
		for (i <- 1 to 10){
			master ! s"Hellos #$i"
		}

		/*
		Routers are extremely usefule when you want to delegate or Spread work between multiple actors of the same kind.
		Routers are usually middle level actors that forward messages to to other actors
		 */

		//This master will route all the requests to 5 slaves

		class Master extends Actor {
			//step 1 create routees
			private val slaves = for(i <- 1 to 5) yield {
				val slave = context.actorOf(Props[Slave], s"ID$i")
				context.watch(slave)
				ActorRefRoutee(slave)
			}
			/*
			Router receives two arguments:
				1 - router logic which tells how to route the messages to each of the children
				2 - a routee
			*/

			//step 2 define router
			private var router = Router(RoundRobinRoutingLogic(), slaves)


			override def receive: Receive = {
				//step 3 route the messages
				case message =>
					router.route(message, sender())
				//step 4 handle lifecycle of routees
				case Terminated(ref) =>
					router = router.removeRoutee(ref)
					val newSlave = context.actorOf(Props[Slave])
					context.watch(newSlave)
					router = router.addRoutee(newSlave)
			}
		}



		class Slave extends Actor with ActorLogging {
			override def receive: Receive = {
				case message => log.info(message.toString)
			}
		}
	}

	//Simpler method - Pool routers - router actor with its own children
	object method2_Programmatic{
		import method1.Slave
		val system = ActorSystem("System")
		//Round robin Pool will create 5 actors of type Slave
		val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]), "poolMaster")
		//Pool means that the routingActors has its own children, meaning poolMaster has created 5 slaves under itself like we didi in method 1
		for (i <- 1 to 10) {
			poolMaster ! s"[$i] Hello!"
		}

	}
	object method2_Configuration{
		import method1.Slave
		val system = ActorSystem("AkkaRouters", ConfigFactory.load().getConfig("demo"))
		//name is very important here
		val poolMaster2 = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")
		for (i <- 1 to 10) {
			poolMaster2 ! s"[$i] Hello!"
		}
	}

	//group router - router with actors created elsewhere -
	object method3_Programmatic{
		import Lesson.method1.Slave
		val system = ActorSystem("System")

		//...in another part of my application
		val  slaveList = (1 to 5).map(i => system.actorOf(Props[Slave], s"ID$i")).toList

		//need their paths
		val slavePaths = slaveList.map(ref => ref.path.toString)

		//create a group master
		val groupMaster = system.actorOf(RoundRobinGroup(slavePaths).props())
		for(i <- 1 to 10){
			groupMaster ! s"[$i] Hello again"
		}
	}

	object method3_Configuration{
		import Lesson.method1.Slave
		val system = ActorSystem("AkkaRouters", ConfigFactory.load().getConfig("demo"))
		val slaveList = (1 to 5).map(i => system.actorOf(Props[Slave], s"ID$i")).toList
		val slavePaths = slaveList.map(ref => ref.path.toString)
		val groupMaster2 = system.actorOf(FromConfig.props(),"groupMaster2")
		for(i <- 1 to 10){
			groupMaster2 ! s"[$i] Hello again"
		}

		//1groupMaster2 ! Broadcast("Must have been deprecated")
		//also poisonpill and kill are not distributed
	}
}
