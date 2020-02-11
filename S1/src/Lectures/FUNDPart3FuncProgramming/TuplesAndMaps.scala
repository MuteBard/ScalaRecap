package Lectures.FUNDPart3FuncProgramming

object TuplesAndMaps extends App {

    //TUPLES
    //Tuples are finite ordered, kinda like lists
    val aTuple = new Tuple2(2, "hi")
    //basically they group things together
    println(aTuple._1)
    println(aTuple._2)
    println(aTuple.copy(_2 = "bye"))
    println(aTuple.swap)

    //MAPS
    //we associate keys to values
    val aMap: Map[Int, String] = Map()

    //you populate a map by specifying tuples or pairings
    val phonebook1 = Map(("Erin", 555), ("Carl", 857)).withDefaultValue(-1)
    val phonebook2 = Map("x" -> 555, "y"-> 857)

    println(phonebook1.contains("Erin"))
    println(phonebook1("Erin"))
    println(phonebook1("Erin2")) //will cause crash without default value

    //Add a pairing
    val newPairing = "Ted" -> 343
    val phonebook3 = phonebook1 + newPairing

    //functionals on maps
    val phonebook4 = phonebook3.map(pair => pair._1.toUpperCase() -> pair._2)
    println(phonebook4)

    val phonebook5 = phonebook4.filterKeys(_.length > 3)
    println(phonebook5)

    val numbers = phonebook4.mapValues(_*10)
    println(numbers)


    println(phonebook4.toList)

    object Circle{
      val round = true
      def apply (color : String, speed : Int) : Circle = { new Circle (color, speed)} // its doing this  in the background
    }
    class Circle(val color : String , val speed : Int){
      def apply() : Vector[String] = Vector(s"$color", s"$speed") //but interfacing with you over here
    }
    println(List("Type 1", Circle("Green", 25)()), List("Type 2", Circle("Red", 25)()))

    val fruits = List("Oranges", "Apples", "Pears", "Banana", "Apricot", "Pinapple")
    println(fruits.groupBy(_.charAt(0)))
    println(fruits.groupBy(_.length >= 6))
    println(fruits.groupBy(_.contains("e")))
    //look up more group by tricks

}
