package Lectures.FUNDPart3FuncProgramming

import scala.util.Random

object Optionss extends App {
/*
  Null pointers are the bane of our existence and they lead to all sorts of things such as
  spagetti code in order to avoid them. So we would need a kind of datatype that could
  encapsulate the possible absence of a value. So these are options. An option is a wrapper
  for a value that might be absent.

  Map uses options on its basic get operation; prefers it over apply
 */

  // The whole point of options is that we shouldnt have to do null checks ourselves
  def unsafeMethod(): String = null
  def backupMethod(): String = "Valid result"
  val chainedResult = Option(unsafeMethod()).orElse(Option(backupMethod()))
  println(chainedResult)


  def betterUnsafeMethod(): Option[String] = None
  def betterBackupMethod(): Option[String] = Some("Vaild Result")
  val chainedResult2 = betterUnsafeMethod() orElse betterBackupMethod()
  println(chainedResult2)


  //map, filter, flatmap
  println(chainedResult2.map(_+"!"))


  val config: Map[String, String] = Map(
    "host" -> "176.45.36.1",
    "port" -> "80"
  )

  class Connection {
    def connect = "Connected" //connect to some server
  }
  //companion object
  object Connection{
    val random = new Random (System.nanoTime())
    def apply(host: String, port: String): Option[Connection] = {
      if (random.nextBoolean()) Some(new Connection)
      else None
    }
  }



  println(Connection(config("host"), config("port")))
  val host = config.get("host")
  val port = config.get("port")
  /*
  if(h != null)
    if(p != null)
      return Connection.apply(h, p)
  return null
  */
  val connection = host.flatMap(h => port.flatMap(p => Connection(h,p)))
  /*
  if (c != null)
    return c.connect
  return null
  */
  val connectionStatus = connection.map(c => c.connect)
  //if (connectionStatus == null) print(None) else print(Some(connectionStatus.get))
  println(connectionStatus)
  /*
  if (status != null)
    println(status)
  */
  connectionStatus.foreach(println)

  //Chaining
  config.get("host")
    .flatMap(h => config.get("port")
      .flatMap(p => Connection(h, p)))
    .map(connection => connection.connect)
    .foreach(println)

}
