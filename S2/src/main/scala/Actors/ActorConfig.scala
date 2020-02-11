package Actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object ActorConfig extends App {
	/*
	The entire configuration is held within an actor system
	we will be talking about
	the configuration syntax
	the kinds of values that we can use for tweaking akka
	and how we can load configurations for an actor system
	 */

	/*
	A configurations is just a piece of text that describes name = value pairs
	 */


	 class SimpleLoggingActor extends Actor with ActorLogging{
		 override def receive: Receive = {
			 case message => log.info(message.toString)
		 }
	 }
	 val configString =
	 """
	    | akka {
		|   loglevel = "DEBUG"
		| }
	 """.stripMargin

	 val config = ConfigFactory.parseString(configString)
	 val system = ActorSystem("ConfigurationDemo", ConfigFactory.load(config))
	 val actor = system.actorOf(Props[SimpleLoggingActor], "actor")
	 actor ! "ayyyy"

//[DEBUG] [02/04/2020 13:07:37.170] [main] [EventStream(akka://ConfigurationDemo)] logger log1-Logging$DefaultLogger started
//[DEBUG] [02/04/2020 13:07:37.179] [main] [EventStream(akka://ConfigurationDemo)] Default Loggers started
//[INFO] [02/04/2020 13:07:37.366] [ConfigurationDemo-akka.actor.default-dispatcher-4] [akka://ConfigurationDemo/user/actor] ayyyy



	val system2 = ActorSystem("ConfigurationDemo2")
	 val actor2 = system2.actorOf(Props[SimpleLoggingActor], "actor2")
	 actor2 ! "lmaoo"

	 /*
	 When you create an actor system with no configuration altogether, akka will automatically looks at the src/main/resources/application.conf file
	  *y6

//[DEBUG] [02/04/2020 13:24:20.928] [main] [EventStream(akka://ConfigurationDemo2)] logger log1-Logging$DefaultLogger started
//[DEBUG] [02/04/2020 13:24:20.929] [main] [EventStream(akka://ConfigurationDemo2)] Default Loggers started
//[INFO] [02/04/2020 13:24:20.948] [ConfigurationDemo2-akka.actor.default-dispatcher-3] [akka://ConfigurationDemo2/user/actor2] lmaoo
*/
val specialConfig = ConfigFactory.load().getConfig("mySpecialConfig")
val system3 = ActorSystem("SpecialConfigDemo", specialConfig)
val actor3 = system3.actorOf(Props[SimpleLoggingActor], "actor3")
	actor3 ! "ok"



val jsonConfig  = ConfigFactory.load("json/jsonConfig.json")
println(s"json config: ${jsonConfig.getString("aJsonProperty")}" )
println(s"json config: ${jsonConfig.getString("akka.loglevel")}" )
}
