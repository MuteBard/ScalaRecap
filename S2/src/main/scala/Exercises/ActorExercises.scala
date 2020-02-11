package Exercises

import Exercises.ActorExercises.Account.Withdraw
import Exercises.ActorExercises.Person.LiveTheLife
import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorExercises extends App{

    //1)
    //Because these classes do not really hold any additional information, they are better suited to be case objects
//    case class ++()
//    case class --()
//    case class print()

    //the Domain of the Counter
    object Counter{
        case object Inc
        case object Dec
        case object Print
    }

    //Also as a best practice, I want you to get int the habit of creating these objects inside a companion object of
    //the actor that supports them

    class Counter extends Actor{
        import Counter._
        var count = 0
        //Receive is an alias for a partial function PartialFunction[Any, Unit]
        override def receive: Receive = {
            case Inc => count += 1
            case Dec => count -= 1
            case Print => println(s"[counter] My current count is : $count")
        }
    }

    import Counter._
    val system = ActorSystem("ActorSystem")
    val counter = system.actorOf(Props[Counter], "counter")
    counter ! Inc
    counter ! Inc
    counter ! Print
    (1 to 5).foreach(_ => counter ! Inc)
    counter ! Print
    (1 to 3).foreach(_ => counter ! Dec)
    counter ! Print
//    [counter] My current count is : 2
//    [counter] My current count is : 7
//    [counter] My current count is : 4

    //2)


    object Account {
        case class Deposit(amount: Int)
        case class Withdraw(amount: Int)
        case object Statement
        case class TransactionSuccess(message : String)
        case class TransactionFailure(message : String)
    }

    /**/
    class Account extends Actor{
        import Account._
        var funds = 0
        override def receive: Receive = {
            case Deposit(amount) =>
                if(amount < 0) sender() ! TransactionFailure("Invalid deposit amount")
                else {
                    funds += amount
                    sender() ! TransactionSuccess(s"Successfully deposited : $amount")
                }
            case Withdraw(amount) =>
                if(amount < 0) sender() ! TransactionFailure("Invalid withdraw amount")
                else if(amount > funds) sender() ! TransactionFailure("Insufficient funds")
                else{
                    funds -= amount
                    sender() ! TransactionSuccess(s"Successfully withdrew : $amount")
                }
            case Statement => sender() ! s"Your balance is $funds"
        }
    }

    object Person{
        case class LiveTheLife(account: ActorRef)
    }
    class Person extends Actor{
        import Person._
        import Account._
        override def receive : Receive = {
            case LiveTheLife(account) =>
                account ! Deposit(1000)
                account ! Withdraw(9000)
                account ! Withdraw(11)
                account ! Deposit(-1)
                account ! Statement
            case message => println(message.toString)
        }
    }


    val account = system.actorOf(Props[Account], "BankAccount")
    val person = system.actorOf(Props[Person], "Carl")

    person ! LiveTheLife(account)


}
