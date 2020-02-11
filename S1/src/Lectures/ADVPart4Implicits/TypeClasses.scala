package Lectures.ADVPart4Implicits

object TypeClasses extends App{
  //implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a , b) => a.nUnits * a.unitPrice <  b.nUnits * b.unitPrice)
  /*
  Ordering trait here is actually a type class
  A type class is a trait that takes a type and describes what operations can be applied to that type


  SO lets imaging that we are the backend developers of a small social network and we have decided to do server side rendering for various elements on the page
  we have decided to implement a small trait called HTMLWritable. This is a general trait for all the various data stuctures. Lets say that tthis trait has a small method
  called toHTML

   */

  //all the classes that we will implement this trait will have to implment a toHTML call method
  trait HTMLWritable{
    def toHTML : String
  }

  case class User(name : String, age : Int, email : String) extends HTMLWritable{
    override def toHTML: String = s"<div>$name ($age yo) <a href=$email/> </div>"
  }

  //Type Class
  trait HTMLSerializer[T]{
    def serialize(value: T): String
  }

  object UserSerializer extends HTMLSerializer[User]{
    def serialize(user: User): String = s"<div>${user.name} (${user.age} yo) <a href=${user.email}/> </div>"
  }

  import java.util.Date
  object DateSerializer extends HTMLSerializer[Date]{
    def serialize(date: Date): String = s"<div>${date.toString}/> </div>"

  }

  val erin = User("Erin", 24, "Erin@gmail.com")
  println(UserSerializer.serialize(erin))
}
