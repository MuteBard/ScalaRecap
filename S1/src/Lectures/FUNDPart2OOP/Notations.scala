package Lectures.FUNDPart2OOP

object Notations extends App{

  class Person(val name : String, val movie: String){
    def likes(movie : String) : Boolean = movie == this.movie
  }

  val mary = new Person("Mary", "Inception")
  println(mary.likes("Inception"))
  println(mary likes "Inception") // infix notation
  println(mary likes "Endgame ") // only works with methods that have only one parameter

  val number = new Math(7)
  println(number + 5)  // methods can be signs, all operators are methods
  println(number())


}

class Math(val num : Int = 0){
  def + (num : Int) : Int = { this.num + num }
  def - (num : Int) : Int = { this.num - num }
  def * (num : Int) : Int = { this.num * num }
  def / (num : Int) : Int = { this.num / num }
  def apply(): Int = this.num
}

