package Lectures.FUNDPart3FuncProgramming

object Sequences extends App{


  trait Seq[+A]{
    def head: A
    def tail: Seq[A]
  }
  //SEQUENCES
  //Sequences are a very general interface for data structures that
  //  -have a well defined order
  //  -can be indexed

  //Supports various operations
  //apply, iterator, length, reverse for indexing and iterating
  //concatenation, appending, prepending
  //alot of others: grouping, sorting, zipping, searching, slicing


  val aSequence = Seq(1,2,4,3)
  println(aSequence)
  println(aSequence.reverse)
  println(aSequence(2))
  println(aSequence ++ Seq(5, 6, 7))
  println(aSequence.sorted)


  //The Sequence companion object actually has an apply factory method that can construct subclasses of
  //Sequence.

  //RANGES
  val aRange = 1 until 10
  aRange.foreach(println)
  (1 to 10).foreach(x => println("Hello"))


  //LISTS
  sealed abstract class List[+A]
  case object Nil extends List[Nothing]
//  case class ::[A](val hd: A, val tl: List[A]) extends List[A]

  /*
  A LinearSeq immutable linked list
  - head, tail, isEmpty methods are fast
  - most operations are O(n): length, reverse

  Lists are sealed - has two subtypes
  - object Nil(empty)
  - class :: (Node)
   */

  val aList = List(1,2,3)
  val prepended = 42 +: aList :+ 89
  println(prepended)

  val apples = List.fill(5)("apple")
  println(apples)
  println(apples.mkString("🍎"))

  //ARRAYS
  //There are arrays but i will not be using them so skip

  //VECTOR
  /*
  The default implementation for immutable sequences
  - effectively contant index read and write: O(log(n))
  - fast element addition: append/prepend
  - implemented as a fixed-branched trie with a branch factor  of 32 elements  at  any one level
  - good performance for larger sizes
   */

  val vector: Vector[Int] = Vector(1,2,3)
  println(vector)

  //they offer the same perks as lists
  //faster than lists

}
