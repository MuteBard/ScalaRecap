package Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorBehavior extends App {
    /*
    The  problem that we want to solve is that we'd like to be able to change our actor's behavior with time and the only way to
    do that currently is by somehow keeping track of the current state and do a whole bunch of if else checks in the message handler
    to react to messages differently depending on that state


    imagin we have two actors:
    a mother
    a kid
    mother feeds her kid
    kid changes their state
    if the kid gets veggies they are sad
    if the kid gets chocolate they are happy
     */



	object Kid{

		case object kidAccept
		case object kidReject
		val HAPPY = "happy"
		val SAD = "sad"
	}
	class Kid extends Actor{
		import Kid._
		import Mom._
		var state = HAPPY
		override def receive : Receive = {
			case Food(VEGGIES) => state = SAD
			case Food(CHOCO) => state = HAPPY
			case Ask(_) =>
				if (state == HAPPY)
					sender() ! kidAccept
				else
					sender() ! kidReject
		}
	}
//this better no var
    /*
    context.become replaces the current message handler with a new
    message handler which will then be used for all future messages
    context.become can have one or two parameters
    true adds handler to a stack, use  unbecome  to pop and use old handler
    false discards old handler review lecture 13
     */
	class statelessKid extends Actor{

		import Kid._
		import Mom._
		override def receive: Receive = happyReceive

		//a stateless state
		def happyReceive : Receive = {
			case Food(VEGGIES) => context.become(sadReceive)
			case Food(CHOCO) =>
			case Ask(_) => sender() ! kidAccept
		}
		//a stateless state
		def sadReceive : Receive = {
			case Food(VEGGIES) =>
			case Food(CHOCO) => context.become(happyReceive)
			case Ask(_) => sender() ! kidReject
		}
	}

	object Mom{
		case class Start(kidRef :  ActorRef)
		case class Food(food : String)
		case class Ask(message : String)
		val VEGGIES = "veggies"
		val CHOCO = "chocolate"
	}
	class Mom extends Actor{
		import Mom._
		import Kid._

		override def receive : Receive = {
			case Start(kidRef) =>
				kidRef ! Food(VEGGIES)
				kidRef ! Ask("Do you want to play")

			case kidReject => println("im sad, but at least hes healthy")
			case kidAccept => println("Yay")
		}
	}
	import Mom._
	val system = ActorSystem("ActorBehavior")
	val kid  = system.actorOf(Props[Kid], "Kid")
	val sKid  = system.actorOf(Props[statelessKid], "sKid")
	val mom = system.actorOf(Props[Mom], "Mom")
	mom ! Start(sKid)


}
