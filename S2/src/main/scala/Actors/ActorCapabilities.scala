package Actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.util.Random

object ActorCapabilities extends App {

//  class SimpleActor extends Actor{
//    //we must override the receive method because every actor must have one
//    override def receive: Receive = {
//      case message: String => println(s"[simple actor] I have received message: $message ")
//      case number: Int => println(s"[simple actor] I have received number: $number")
//        //Any type at allll
//      case SpecialMessage(contents) => println(s"[simple actor] I have received something special: $contents")
//    }
//  }
//
//
//
//  val system = ActorSystem("actorCapabilitiesDemo")
//  val simpleActor = system.actorOf(Props[SimpleActor], "SimpleActor")
//  simpleActor ! "hello, actor"
//
//  //1 - messages can be of any type
//  simpleActor ! 376
//
//  /*
//  So when you invoke the tell method, Akka retrieves the Receive object. This object will then be invoked on the message type that you sent
//  In this context, Akka receives this object
//    {
//      case message: String => println(s"[simple actor] I have received message: $message ")
//      case number: Int => println(s"[simple actor] I have received number: $number")
//    }
//   */
//
//  //The messages can be of any type at all, even your own
//  case class SpecialMessage(contents: String)
//  simpleActor ! SpecialMessage("some special content")
//
//  //But there are two conditions
//  /* 1)
//  A) - Messages must be immutable
//  B) - Messages must be serializable for context
//
//  Serializable means that the JVM can transform it into a byte stream and send it to another JVM whether its on the same machine or over the network
//  Serializable is a Java interface and theres a number of serialization protocols
//  But overall we often use case classes and case objects for messages. so they serve 99.999% of our message needs
//  the core principles are immutability and serializablity
//
//  Currently there is no way of detecting whether an object is immutable at compile time but its up to us to enforce this principle otherwise we get into very very very nasty
//  problems
//
//  2)
//  Actors have information about their context and about themselves
//  each actor has a member called context
//  This context is a complex data structure that has references to information regarding the environment this actor runs in
//
//  for example it has access to the actorSystem and even has access to its actor's own actor reference
//   */
//
//  class SimpleActorFORDEMOPURPOSES extends Actor{
//
//    context.self //this is the equivalent of this in OOP
//    self // shorthand, this is the equivalent of this in OOP
//    override def receive: Receive = {
//      case "Hi" => context.sender() ! "Hello there!" //this is replying to a message
//      case message: String => println(s"[$self] I have received message: $message ")
//      case number: Int => println(s"[$self] I have received number: $number")
//      case SpecialMessage(contents) => println(s"[$self] I have received something special: $contents")
//      case SendMessagetoYourself(content) => self ! content
//      case SayHiTo(ref) => ref ! "Hi" //   (ref ! "Hi")(self) self is an implicit parameter
//      case WirelessPhoneMessage(content, ref) => ref forward (content + "s") // keep the original sender of the wireless message
//    }
//  }
//
//  val demoActor = system.actorOf(Props[SimpleActorFORDEMOPURPOSES], "DemoActor")
//  case class SendMessagetoYourself(content : String)
//  demoActor ! SendMessagetoYourself("actual garbage")
//
//  /*
//  Bear in mind that all these behaviors that we are seeing here happen in a comletely nonblocking and asynchronous manner
//   */
//
//  //Now I want to show you how actors can reply to messages again using their context
//
//  val erin = system.actorOf(Props[SimpleActorFORDEMOPURPOSES], "erin")
//  val carl = system.actorOf(Props[SimpleActorFORDEMOPURPOSES], "carl")
//
//  case class SayHiTo(ref : ActorRef)
//  erin ! SayHiTo(carl)
//
//  /*
//  context.sender() or simply sender is the actor reference who last sent me this exact message that im handling right now
//   */
//
//  //REVIEW IMPLICITS
//
//  erin ! "rip letters" //dead letters
//  //5
//  /*
//  forwarding messages
//  'context.sender() or simply sender is the actor reference who last sent me this exact message that im handling right now"
//  */
//
//  case class WirelessPhoneMessage(content : String, ref: ActorRef)
//
//  erin ! WirelessPhoneMessage("Hi", carl)
//



  /*
  Questions to ask,
    -what is an actor system?

    -what is the tell method in Akka?
     When you evoke the tell method, aka retrieves the object that will then be invoked on the message type that you sent

    -what is the object in this context?
    {
      case message: String =>  println(s"[$self] message received: $message")
      case number: Int => println(s"[$self] number received: $number")
    }

*/

  class SimpleActor extends Actor {
    var numberInMyHead = 0
    val random = new Random()
    import SimpleActor._
    override def receive : Receive = {


      case "think" =>
        numberInMyHead = random.nextInt(10)
        println(s"[${self.path}] numberInMyHead is: $numberInMyHead")
      case "Hi" => sender() ! s"Hello"
      case Add(ref) => ref ! numberInMyHead

      case message: String =>  println(s"[${self.path}] message received: $message")
      case number: Int => println(s"[${self.path}] number received: $number + $numberInMyHead = ${number+numberInMyHead}")
      case SpecialMessage(message) => println(s"[${self.path}] special message received: $message")
      case SendMessageToYourself(message) => self ! message
      case SayHiTo(ref) => ref ! "Hi"


    }
  }

  object SimpleActor{
    case class SpecialMessage(contents: String)
    case class SendMessageToYourself(content: String)
    case class SayHiTo(ref : ActorRef)
    case class Add(ref : ActorRef)
  }

  import SimpleActor._
  val system = ActorSystem("ActorCapabilities")
  val erin = system.actorOf(Props[SimpleActor], "Erin")
  val carl = system.actorOf(Props[SimpleActor], "Carl")
  erin ! "testing123"
  erin ! 123
  erin ! SpecialMessage("secret")
  erin ! SendMessageToYourself("I can do it")
  erin ! SayHiTo(carl)

  erin ! "think"
  carl ! "think"
  erin ! Add(carl)
  erin ! SayHiTo(carl)
  carl ! SayHiTo(erin)


}
