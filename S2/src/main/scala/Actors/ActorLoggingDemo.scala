package Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLoggingDemo extends App{

	// #1 Explicit logging
	class SimpleActorWithExplictLogger extends Actor{
		val logger = Logging(context.system, this)
		override def receive: Receive = {
		/*
			1 - Debug
			Generally the most verbose and we generally use thses messsages to find out exactly what happend
			2 - Info
			Benign messages, most logs are logged at level 2
			3 - Warning/Warn
			warnings such as dead letters would work, they might signal something wrong with an application but they might not be cause for trouble
			4 - Error
			something that causes the application to crash
		 */
		case message => logger.info(message.toString)
		}
	}

	//#2 Actor Logging
	class ActorWithLogging extends Actor with ActorLogging{
		override def receive: Receive = {
			case (a, b) => log.info("Two things: {} and {}", a, b)
			case message => log.info(message.toString)
		}
	}

	val system = ActorSystem("LoggingDemo")
	val actor = system.actorOf(Props[SimpleActorWithExplictLogger], "Actor")
	val actor2 = system.actorOf(Props[ActorWithLogging], "Actor2")
	actor ! "simple loggin message1"
	actor2 ! "simple loggin message2"

//		[INFO] [02/04/2020 10:47:14.917] [LoggingDemo-akka.actor.default-dispatcher-3] [akka://LoggingDemo/user/Actor] simple loggin message1
//		[INFO] [02/04/2020 10:47:14.917] [LoggingDemo-akka.actor.default-dispatcher-2] [akka://LoggingDemo/user/Actor2] simple loggin message2
	actor2 ! (100, 200)
	//Logging is async!
	//you can add other loggers like SLF4J

}
