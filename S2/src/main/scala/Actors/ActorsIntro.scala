package Actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorsIntro extends App {
  //part1 - actor systems
  /*
  Every Akka application will have to start with what is called an actor system
  The actor system is a heavyweight data structure that controls a number of threads under the hood which then allocates
  to running actors.

  Now it is recommended to have one of these actor systems per application instance unless we have a good reasons to create more.
  Also the name of this actor system has restrictions so it must contain only alphanumeric characters.
  */

  val actorSystem = ActorSystem("FirstActorSystem")
  println(actorSystem.name)



  //part2 - create actors
  /*
    Actors are kind of like humans talking to each other
    1) Actors are uniquely identified
    2) Messages are asynchronous
    3) Each actor may respond differently
    4) Actors are (really) encapsulated
   */

  //word count actor

  class WordCountActor extends Actor {
    //internal data
    var totalWords = 0

    //behavior
    def receive : PartialFunction[Any, Unit] = {
      case message: String =>
        println(s"[word counter] i have received message: ${message.toString}")
        totalWords += message.split("").length
      case msg => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }



  //part3 - instantiate our actor
  /*
  An ActorRef is the data structure Akka exposes to you as a programmer so that you cannot call or otherwise poke into
  the actual word count actor instance akka creates. you can only communicate to the WordCountActor via this actor reference wordCounter
   */
  val wordCounter = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val wordCounter2 = actorSystem.actorOf(Props[WordCountActor], "wordCounter2")


  //part4 - communicate!
  /*
  the ! is the method that we are invoking, also known as tell
  Now it may not be apparent from the simple example but sending the message here is completely async
   */
  wordCounter ! "I am learning Akka and its chill"
  wordCounter2 ! "we sleep"

//  [word counter] i have received message: we sleep
//  [word counter] i have received message: I am learning Akka and its chill



  //How does this work?
  class Person(name : String ) extends Actor{
    override def receive: Receive = {
      case "Hi" => println(s"Im $name")
    }
  }

  object Person{
    def props(name: String) = Props(new Person(name))
  }

  //this is legal but also discouraged, it would be better if we declared a companion object
  val person = actorSystem.actorOf(Props(new Person("Bob")))
  //this is better
  val erin = actorSystem.actorOf(Person.props("Erin"))


  person ! "Hi"
  erin ! "Hi "
}
