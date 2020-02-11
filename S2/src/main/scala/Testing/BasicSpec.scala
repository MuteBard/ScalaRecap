package Testing

import Testing.BasicSpec.SimpleActor
import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import scala.concurrent.duration._
import scala.language.postfixOps

//name our testing classes with the suffix spec
class BasicSpec extends TestKit(ActorSystem("BasicSpec"))
	with ImplicitSender
	with AnyWordSpecLike
	with BeforeAndAfterAll {
	/* Interfaces/traits
	ImplicitSender : used for send rely scenarios in actors which are lot.
	AnyWordSpecLike : allows the description of tests in a very  natural language style behavior during testing
	BeforeAndAfterAll : supplies some hooks so that when you run your test suite, this set of hooks will be called   .
	 */

	/*
	used for destroying and tering downthe test suite
	and when the test suite is being instansiated, a new actor system will be created, so we need this to tear down the actor system
	 */
	def afterall(): Unit = {
		TestKit.shutdownActorSystem(system)
	}

	import BasicSpec._

	"A simple actor" should { //the test suite
		val echoActor = system.actorOf(Props[SimpleActor], "simpleActor")
		"send back the same message" in { //tests

			val message = "hello test"
			echoActor ! message
			expectMsg(message)

		}
	}
	"A blackhole actor" should {
		"send back the same message" in {
			val voidActor = system.actorOf(Props[BlackHoleActor], "blackHoleActor")
			val message = "hello test"
			voidActor ! message
			expectNoMessage(1 second) //  to change duration before timeout; expectMsg  -> remainingOrDefault -> SingleExpectDefaultTimeout -> "akka.test.single-expect-default"

		}
	}
	"A simple actor" should { //the test suite
		val echoActor = system.actorOf(Props[SimpleActor], "simpleActor2")
		"send back the same message 2" in { //tests


			val message = "I love akka".toUpperCase()
			echoActor ! message
			val reply = expectMsgType[String]
			assert(reply == "I LOVE AKKA")

		}

		"give either 1 or 2" in {
			echoActor ! 1
			expectMsgAnyOf(1, 2)
		}
	}
}

/*
the sender of these messages is test actor which is a member of testkit. test actor in particular for our tests is passed implicitly as the sender of ever single message
that we send because we mixed in the implicit sender trait
 */

/*
Create a companion object for your specs
 */
object BasicSpec {
	class SimpleActor extends Actor {
		override def receive: Receive = {
			case message => sender() ! message // basically an echo actor
			case number  => sender ! number
		}
	}

	class BlackHoleActor extends Actor{
		override def receive: Receive = Actor.emptyBehavior
	}
}