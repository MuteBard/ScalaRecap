package Exercise
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
object Banking extends App {

  //blocking future
  /*
  Why would we want to know how to block on a future? Sometimes it makes sense to block a future for critical operations like bank transfers or finacial transactions
  or anything thing that is transaction like, and you want to make sure that your operation is fully complete before you move on to display some results or to compute
  additional values
  */

  //online banking app
  case class User(name: String)
  case class Transaction (sender: String, receiver: String, amount : Double, status: String )
  object BankingApp {
    val name = "Severe Banking"

    def fetchUser(name: String): Future[User] = Future{
      //simulate a long computation
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user: User, merchantName: String, amount: Double): Future[Transaction] = Future {
      //simulate a long computation
      Thread.sleep(1000)
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(username: String, item: String, merchantName: String, cost : Double) : String = {
      //fetch the user from the DB
      // create a transaction
      // WAIT for the transaction to finish

      val transactionStatusFuture = for {
        user <- fetchUser(username)
        transaction <- createTransaction(user, merchantName, cost)
      } yield transaction.status
      Await.result(transactionStatusFuture, 2.seconds)

    }
  }

  println(BankingApp.purchase("Carl", "S9+", "Samsung", 900))

  //promises
  val promise = Promise[Int]
  val future = promise.future

  //thread 1 consumer
  future.onComplete{
    case Success(r) => println(s"[consumer] I've received $r")
  }

  //thread 2 producer
  val producer = new Thread(() => {
    println("[producer] crunching numbers...")
    Thread.sleep(500)
    //"fullfilling" the promise
    promise.success(42)
    println("[producer] done")
  })

  producer.start()
  Thread.sleep(1000)

}
