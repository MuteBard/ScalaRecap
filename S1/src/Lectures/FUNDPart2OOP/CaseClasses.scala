package Lectures.FUNDPart2OOP

object CaseClasses extends App {


  case class Person(name: String, age :Int){
    //1. class parameters are fields
    //2. Sensible toString
    //3. built in equals and Hashcode implemented
    //4. case classes have copy method
    //5. case classes have companion objects
    //6. are seralizable
    //7. can be used in pattern matching
    //theres also case objects, they do everything here but #5
  }

  case object Fire{
    def temp : Int = 300
  }

  val jim = new Person("Jim", 34)
  println(jim.toString)

  val jim2 = new Person("Jim", 34)
  val jim3 = new Person("Carl", 27)
  println(jim == jim2)
  println(jim == jim3)

  val jim4 = Person("Erin", 24)

}
