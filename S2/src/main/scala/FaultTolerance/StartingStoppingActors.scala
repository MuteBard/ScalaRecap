package FaultTolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Kill, PoisonPill, Props, Terminated}

object StartingStoppingActors extends App {
	val system = ActorSystem("StoppingActorsDemo")


	object Parent{
		case class StartChild(name : String)
		case class StopChild(name : String)
		case object Stop
	}


	class Parent extends Actor with ActorLogging {
		import Parent._
		override def receive: Receive = withChildren(Map())

		def withChildren(children: Map[String, ActorRef]) : Receive = {
			case StartChild(name) =>
				log.info(s"Starting Child $name")
				context.become(withChildren(children + (name -> context.actorOf(Props[Child],name))))

			case StopChild(name) =>
				log.info(s"Stopping Child with name $name")
				val childOption = children.get(name)
				childOption.foreach(childRef => context.stop(childRef))

			case Stop =>
				log.info("Stopping myself")
				context.stop(self)// also recursively stops all its children first and then the parent
			case message => log.info(message.toString)
		}
	}

	class Child extends Actor with ActorLogging {
		override def receive: Receive = {
			case message => log.info(message.toString)
		}
	}

	class Watcher extends Actor with ActorLogging{
		import Parent._
		override def receive: Receive = {
			case StartChild(name) =>
				val child = context.actorOf(Props[Child], name)
				log.info(s"Started and watching child $name")
				context.watch(child) //can watch more than one actor, not necessarily children
			case Terminated(ref) =>
				log.info(s"The reference that I'm watching $ref has been stopped")
		}
	}


	import Parent._
//	val parent = system.actorOf(Props[Parent], "parent")
//	parent ! StartChild("erin")
//	val erin = system.actorSelection("user/parent/erin")
//	erin ! "hiiiii"
//	parent ! StopChild("erin")
////	for (_ <- 1 to 50) erin ! "are you still there"
//	parent ! Stop


//	val looseActor = system.actorOf(Props[Child], "looseActor")
//	looseActor ! "Hello looseActor"
//	looseActor ! PoisonPill
//	looseActor ! "Are you still there looseActor?"
//
//	//more brutal
//	val abruptActor = system.actorOf(Props[Child], "abruptActor")
//	abruptActor ! "Hello abruptActor"
//	abruptActor ! Kill //throws an exception
//	abruptActor ! "Are you still there abruptActor?"


	val watcher = system.actorOf(Props[Watcher], "watcher")
	watcher ! StartChild("carl")
	val watchedChild = system.actorSelection("user/watcher/carl")
	watchedChild ! "hiiiii"
	Thread.sleep(1000)
//	watcher ! StopChild("carl") doesnt trigger terminated case
	watchedChild ! PoisonPill
}
