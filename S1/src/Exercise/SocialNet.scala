package Exercise

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object SocialNet extends App{

  case class Profile(id: String, name: String){
    def poke(anotherProfile: Profile) = {
      println(s"${this.name} poking ${anotherProfile.name}")
    }
  }

  object SocialNetwork{
    // database
    val names = Map(
      "twr.id.1-mutinybard" -> "MuteBard",
      "twr.id.1-ayooo123" -> "Spoonz",
      "twr.id.1-sssupermario92" -> "CountBleck",
      "twr.id.1-MidiFreeze" -> "Midnight",
      "twr.id.1-Hibiscus95" -> "Nerine"
    )

    val friends = Map(
      "twr.id.1-Hibiscus95" -> "twr.id.1-mutinybard"
    )

    val random = new Random()

    //API
    def fetchProfile(id : String) : Future[Profile] = Future {
      Thread.sleep(random.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile) : Future[Profile] = Future {
      Thread.sleep(random.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }


  //client: Nerine to poke MuteBard
  val nerine = SocialNetwork.fetchProfile("twr.id.1-Hibiscus95")
  nerine.onComplete{
    case Success(nerineProfile) =>
      val carl = SocialNetwork.fetchBestFriend(nerineProfile)
      carl.onComplete{
        case Success(carlProfile) => nerineProfile poke carlProfile
        case Failure(exception) => exception.printStackTrace()
      }
    case Failure(exception) => exception.printStackTrace()
  }


  //this is up here is ugly and unsustainable for more complicated and scoping issues

  //functional composition of futures
  val nameOnTheWall = nerine.map(profile => profile.name)
  val nerineBestFriend = nerine.flatMap(profile => SocialNetwork.fetchBestFriend(profile))
  val mutebardBestFriendRestricted = nerineBestFriend.filter(profile => profile.name.startsWith("M"))


  //for comprehensions
  for {
    nerine <- SocialNetwork.fetchProfile("twr.id.1-Hibiscus95")
    mutebard <- SocialNetwork.fetchBestFriend(nerine)
  }
    nerine.poke(mutebard)
  Thread.sleep(1000)

  //Fallbacks
  SocialNetwork.fetchProfile("unknown id").recover{
    case _: Throwable => Profile("twr.id.0-dummy", "Nil")
  }


}
