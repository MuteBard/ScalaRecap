package Actors

import Actors.ChildActors.Parent.CreateChild
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChildActors extends App{
	/*
	Actors can create other actors
	 */

	object Parent {
		case class CreateChild(name : String)
		case class TellChild(message : String)
	}

	//uses var, bad
	class ParentOldVersion extends Actor{
		import Parent._

		var child: ActorRef = null
		override def receive: Receive = {
			case CreateChild(name) =>
				println(s"${self.path} creating child")
				//create a new actor right HERE
				val childRef = context.actorOf(Props[Child], name)
				child = childRef
			case TellChild(message) =>
				if(child != null) child forward message
		}
	}

	class Parent extends Actor {

		import Parent._

		override def receive: Receive = {
			case CreateChild(name) =>
				println(s"${self.path} creating child")
				//create a new actor right HERE
				val childRef = context.actorOf(Props[Child], name)
				context.become(withChild(childRef))
			/*
			context.become replaces the current message handler with a new
			message handler which will then be used for all future messages
			context.become can have one or two parameters
			*/
		}

		//a stateless state
		def withChild(childRef: ActorRef): Receive = {
			case TellChild(message) =>
				if (childRef != null) childRef forward message
		}

	}

	class Child extends Actor{
		override def receive: Receive = {
			case message => println(s"${self.path} I got $message")
		}
	}

	import Parent._
	val system = ActorSystem("ParentChildDemo")
	val parent = system.actorOf(Props[Parent], "parent")
	parent ! CreateChild("Kendall")
	parent ! TellChild("Guu guu")
	parent ! CreateChild("Kendall2")

	/*
	Actor hierarchies
	parent -> child -> grandchild
		   -> child2->

	Child is owned by parent. but who owns parent? is parent some kind of top level actor? no
	We have Guardian actors, or top level actors. Every Akka Actor system has three guardian actors
	- /system = system guardian ever akka actor system gots it own actors for managin  various things like logging
	- /user = user level guardian for example :
	akka://ParentChildDemo/***user***/parent creating child
	akka://ParentChildDemo/***user***/parent/Kendall I got Guu guu
	- / = the root guardian, manages both the system and the user level guardian
	 */

	/*
	Actor Selection, finding an actor by path
	*/
	val childSelection = system.actorSelection("/user/parent/Kendall")
	//If this childSelection doesnt have any actor Ref under the hood then this message  will be sent to dead letters
	childSelection ! "I found you"
	//use Actor selection when you want to locate an actor deeper into your hierarchy



	/*
	Danger
	Never pass mutable actor state or the this reference, to child actors
	this has the danger of breaking actor encapsulation becauase the child actor suddenly has access to the internals of the parent actor.
	It can mutate the state or directly call methods of the parent actor without sending a message and this breaks our very sacred actor principles
	lopk up closing over
	never close over mutable state or "this"
	alway use  messages to communicate to actors only


	 */

}

