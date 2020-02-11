package Infrastucture
import akka.actor.{Actor, ActorLogging, ActorSystem, Cancellable, Props, Terminated, Timers}

import scala.concurrent.duration._
import scala.language.postfixOps

object SchedulersTimers extends App {
	//lesson
	//exerciseUnreccomended
	Recommended
}

object lesson{
	class SimpleActor extends Actor with ActorLogging{
		override def receive: Receive = {
			case message => log.info(message.toString)
		}
	}

	val system = ActorSystem("SchedulersTimersDemo")
	val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

	system.log.info("Scheduling reminder for simpleActor")

	//the scheduling of messages/ the scheduling of code has to happen on some kind of thread much like futures.
	//we need an execution context and for that we will use system.dispatcher. System.dispatcher implements the
	//execution context interface


	//One time messages
	//#1 way of how to do this
	system.scheduler.scheduleOnce(5 second){
		simpleActor ! "Hello"
	}(system.dispatcher)

	//#2
	import system.dispatcher
	system.scheduler.scheduleOnce(5 second){
		simpleActor ! "Hello"
	}

	//#3
	//	implicit val executionContext = system.dispatcher
	system.scheduler.scheduleOnce(5 second){
		simpleActor ! "Hello"
	}


	//Repeated messages (initial delay, interval)
	val startTime = 10
	val routine = system.scheduler.schedule(startTime  second, 1 seconds){
		simpleActor ! "Boop"
	}

	//routine is something of type Cancellable
	val routineDemo : Cancellable = system.scheduler.schedule(1 second, 1 second){
		//just a demo
	}


	//how to cancel routine
	def duration(startTime : Int, endTime : Int): Int = {
		startTime + endTime
	}
	val endTime = 5
	system.scheduler.scheduleOnce(duration(startTime, endTime) seconds){
		routine.cancel()
	}

	/*
	Things to bear in mind:
		- don't use unstable references inside scheduled actions
		- all schdeuled tasks execute when the system is terminated
		- schedulers are not the best at precision and long term planning
	 */
}

object exerciseUnreccomended{

	/*
Exercise : Impliment a self closing actor
	- if the actor receives a message, you have one second to send it another message
	- if the time expires, the actor will stop itself
	- if you send another message, the time window is reset
 */
	import system.dispatcher
	val system = ActorSystem("System")
	val selfClosingActor = system.actorOf(Props[SelfClosingActor], "selfClosingActor")

	system.scheduler.scheduleOnce(250 milli){
		selfClosingActor ! "ping"
	}

	system.scheduler.scheduleOnce(2 seconds){
		system.log.info("sending pong to the self closing actor")
		selfClosingActor ! "pong"
	}

	class SelfClosingActor extends Actor with ActorLogging{

		var schedule = createTimeoutWindow()

		def createTimeoutWindow(): Cancellable = {
			context.system.scheduler.scheduleOnce(1 second){
				self ! "timeout"
			}
		}

		override def receive: Receive = {
			case "timeout" =>
				log.info("Stopping myself")
				context.stop(self)
			case message =>
				log.info(s"Received message $message, staying alive")
				schedule.cancel()
				schedule = createTimeoutWindow()
		}
	}

    	/*
    	Thankfully, akka provides a library called timers to manage all of this. The rational is that the lifecycle of scheduled messages is ver difficult to maintain
    	if the actor is killed or restarted, and the timers a re a simpler and safer way to schedule messages from within an actor
    	 */
}

object Recommended {

	val system = ActorSystem("TimerSystemDemo")
	val actor = system.actorOf(Props[TimerBasedHeartBeatActor], "Actor")





	object TimerBasedHeartBeatActor{
		case object TimerKey
		case object Start
		case object Reminder
		case object Stop
	}

	class TimerBasedHeartBeatActor extends Actor with ActorLogging with Timers {
		import TimerBasedHeartBeatActor._
		import system.dispatcher
		/*
		paramerters:
			1 - An object that will be the key for the timer, this will be for the maintenance of the timers
			This key is just used for the comparison of timers. There should be only one timer for a given TimerKey
			TimerKeys are strange in that they are not String or integers or anyKind of identifiers.They can be any object
			so therefore we create case objects for them

			2 - The message that you want to send to yourself

		 */

		timers.startSingleTimer(TimerKey, Start, 500 millis)
		context.system.scheduler.scheduleOnce(5 second){
			self ! Stop
		}
		override def receive: Receive = {
			case Start =>
				log.info("BootStrapping")
				timers.startPeriodicTimer(TimerKey, Reminder, 1 second) //using the same timerkey later on cancels its former us
			case Reminder =>
				log.info("I am alive")
			case Stop =>
				log.warning("Stopping!")
				timers.cancel(TimerKey)
				context.stop(self)
		}
	}
}
