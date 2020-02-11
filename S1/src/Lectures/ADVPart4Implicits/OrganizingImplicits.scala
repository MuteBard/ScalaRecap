package Lectures.ADVPart4Implicits

object OrganizingImplicits extends App{


  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1,4,5,3,2).sorted) //notice that the sorted method takes an implicit ordering value
  //there is already an implicit ordering value for Int
  //Scala.predef


  /*
  Implicits:
    - val/var
    - object
    - accessor methods = defs with no parentheses
   */

  case class Person(name : String, age : Int)

  val persons = List(
    Person("Carl", 27),
    Person("Erin", 24),
    Person("Carla", 32)
  )

  object Person{
    implicit val orderPerson: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  println(persons.sorted)

  /*
  Implicit scope
    - normal scope = Local Scope
    - imported scope (Think about when we got the execution context implicit for future  which was global and visible to the compiler)
    - companions of all types involved
      - List
      - Ordering
      - All the types involved = A or any supertype
   */

  //Best Practices
  /*
  When defining an implicit val
    1#
    - If there is a single possible value for it
    - and you  can edit the code for the type

    Then define the implict in the companion object of that type

   */

  /*
  Exercise
  - totalPrice = most used
  - by unit count = 25%
  - by unit price = 25%
  */

  case class Purchase(nUnits: Int, unitPrice: Double)
  object Purchase{
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a , b) => a.nUnits * a.unitPrice <  b.nUnits * b.unitPrice)
  }

  object UnitCountOrdering{
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.nUnits <  _.nUnits)

  }
  object UnitPriceOrdering{
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(_.unitPrice <  _.unitPrice)
  }

  //import UnitPriceOrdering._
  //println(someList.sorted)

}
