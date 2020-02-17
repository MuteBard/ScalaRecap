package Lectures.FUNDPart3FuncProgramming

object TuplesAndMaps extends App {

room2

}


object room1
{
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

object room2
{

    case class Vote(citizenPID : String, candidate: String)
    val vote1 = Vote("Carl", "Bernie")
    val vote2 = Vote("Erin", "Bernie")
    val vote3 = Vote("Anti", "Warren")
    val vote4 = Vote("Sam", "Booker")
    val vote5 = Vote("Carla", "Bernie")

    var latestVoteId : Int = 0
    var tally : Map[String, Int] = Map().withDefaultValue(0)



//    println(tally)
//you populate a map by specifying tuples or pairings
    var aVote = (vote1.candidate -> (tally(vote1.candidate) + 1))
    tally = tally + aVote
    println(tally + aVote)
    aVote = (vote2.candidate -> (tally(vote2.candidate) + 1))
    tally = tally + aVote
    println(tally + aVote)
    aVote = (vote3.candidate -> (tally(vote3.candidate) + 1))
    tally = tally + aVote
    println(tally + aVote)
    aVote = (vote4.candidate -> (tally(vote4.candidate) + 1))
    tally = tally + aVote
    println(tally + aVote)
    aVote = (vote5.candidate -> (tally(vote5.candidate) + 1))
    tally = tally + aVote
    println(tally + aVote)

}