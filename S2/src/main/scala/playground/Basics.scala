package playground

import akka.actor.ActorSystem

object Basics extends App{
  val actorSystem = ActorSystem("HelloAkka")
  println(actorSystem.name)
}
