package Infrastucture

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

object AkkaDispatchers extends App {
	LessonDispatchers.method2
}


object LessonDispatchers{

	/*
	A dispatcher controls how messages are sent and handled
	 */
	object method1{


		val system = ActorSystem("DispatcherDemo")//, ConfigFactory.load().getConfig("dispatcherDemo"))
		val actors = for (i <- 1 to 10) yield system.actorOf(Props[Counter].withDispatcher("my-dispatcher"), s"counter_$i")
		val r = new Random()
		for (i <- 1 to 1000){
			actors(r.nextInt(10)) ! i
		}

		class Counter extends Actor with ActorLogging{
			var count = 0
			override def receive: Receive = {
				case message =>
					count += 1
					log.info(s"[$count] $message")
			}
		}
	}


/*sucessful fun of a future on top of context dispatcher
which normally is charge of handleing messages. The running of futures inside of actors is generally discouraged
dont want to starve the context from doing its job


 */
	object method2{

		val system = ActorSystem("DispatcherDemo")
		val dbActor = system.actorOf(Props[DBActor])
		dbActor ! "Hi from DB"

		class DBActor extends Actor with ActorLogging {
			implicit val executionContext: ExecutionContext = context.dispatcher
			override def receive: Receive = {
				case message => Future {
					//wait on resource
					Thread.sleep(5000)
					log.info(s"Success: $message")
				}

			}
		}
	}
}