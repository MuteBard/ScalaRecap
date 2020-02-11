package Lectures.ADVPart2FP1

object FunctionalCollections extends App {

  val set = Set(1,2,3)
  //Sets are a collections of elements that do not contain
  //duplicates
  //Set instances are callable, (They have apply)
  set(2) //true
  set(42) // false

  //for all we care, set instances are callable like functions and the app

  //sets behave like actual functions
  //SETS ARE FUNCTIONS


  //why helper objects are great
  val s1 = MySet(1,2,3,4)
  s1 foreach println

  //no helper object
  val empty = new EmptySet[Int]
  val s2 = new NonEmptySet[Int](750, empty)
  s2 + 65 ++ s1 foreach println
}

trait MySet[A] extends(A => Boolean) {
  /*
  Exercise - implement a functional set
   */
  def apply (elem: A): Boolean =
    contains(elem)
  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit
  def -(elem : A): MySet[A]


}

class EmptySet[A] extends MySet[A]{
  def contains(elem: A): Boolean = false
  def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this )
  def ++(anotherSet: MySet[A]): MySet[A] = anotherSet
  def -(elem : A): MySet[A] = this

  def map[B](f: A => B): MySet[B] = new EmptySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  def filter(predicate: A => Boolean): MySet[A] = this
  def foreach(f: A => Unit): Unit = ()
}

case class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A]{

  def contains(elem: A): Boolean =
    elem == head || tail.contains(elem) // pretty cool use of recursion
/*
  contains(750)
  <- [HEAD | TAIL]
  750 == head  FALSE
  tail.contains(750)
  [23 |D] <- [89 |C] <- [750 |B] <- [2 |A] <- [|]
  750 == head  FALSE
  tail.contains(750)
  [89 |C] <- [750 |B] <- [2 |A] <- [|]
  750 == head  FALSE
  tail.contains(750)
  [750 |B] <- [2 |A] <- [|]
  750 == head TRUE
  TRUE
 */
  def +(elem: A): MySet[A] =
    if( this contains elem) this
    else new NonEmptySet[A](elem, this)

  /*
  +(376)
  <- [HEAD | TAIL]
  if 376 == head FALSE
  tail.contains(376)
  [23 |D] <- [89 |C] <- [750 |B] <- [2 |A] <- [|]
  if 376 == head FALSE
  tail.contains(376)
  [89 |C] <- [750 |B] <- [2 |A] <- [|]
  If 376 == head FALSE
  tail.contains(376)
  [750 |B] <- [2 |A] <- [|]
  if 376 == head FALSE
  tail.contains(376)
  [2 |A] <- [|]
  if 376 == head FALSE
  tail.contains(376)
  [|]
  if 376 == head FALSE
  tail.contains(376) FALSE
  this contains 376 FALSE
  new NonEmptySet[A](376, this)
  [376 |D] <- [23 |D] <- [89 |C] <- [750 |B] <- [2 |A] <- [|]

   */

  def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head
  //tail.++(anotherSet)).+(head)

  /*
  ++([354 |B] <- [353 |A] <- [|])
  <- [HEAD | TAIL]
  this = [376 |D] <- [23 |D] <- [89 |C] <- [750 |B] <- [2 |A] <- [|]
  anotherSet = [354 |B] <- [353 |A] <- [|]


  [23 |D] <- [89 |C] <- [750|B] <- [2 |A] <- [|]    [354 |B] <- [353 |A] <- [|]  376

  [89 |C] <- [750|B] <- [2 |A] <- [|]    [354 |B] <- [353 |A] <- [|]  376 23

  [750|B] <- [2 |A] <- [|]    [354 |B] <- [353 |A] <- [|]  376 23 89

  [2  |A] <- [|]    [354 |B] <- [353 |A] <- [|]  376 23 89 750

  [|]    [354 |B] <- [353 |A] <- [|]  376 23 89 750 2

  go to + method logic
  [|] [2 |C] <- [354 |B] <- [353 |A] <- [|]  376 23 89 750
  go to + method logic
  [|] [750 |D] <-  [2 |C] <- [354 |B] <- [353 |A] <- [|]  376 23 89
  go to + method logic
  [|] [89 |E] <- [750 |D] <-  [2 |C] <- [354 |B] <- [353 |A] <- [|]  376 23
  go to + method logic
  [|] [23 |F] <- [89 |E] <- [750 |D] <-  [2 |C] <- [354 |B] <- [353 |A] <- [|]  376
  go to + method logic
  [|] [376 |G] <- [23 |F] <- [89 |E] <- [750 |D] <-  [2 |C] <- [354 |B] <- [353 |A] <- [|]
   */

  /*
  [1 2 3] ++ [4 5]
  [2 3] ++ [4, 5] + 1
  [3] ++ [4, 5] + 1 + 2
  [] ++ [4, 5] + 1 + 2 + 3
  [4, 5] + 1 + 2 + 3
  [4, 5, 1, 2, 3]
   */
  def map[B](f: A => B): MySet[B] = (tail map f) + f(head)
  def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)
  def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }
  def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }


  def -(elem : A): MySet[A] = {
    if(head == elem) tail
    else tail - elem + head
  }
}

object MySet{

  /*
  val s = MySet(1,2,3) = buildSet(seq(1,2,3), [])
  buildSet(seq(2,3), [] + 1)
  buildSet(seq(3), [1] + 2)
  buildSet(seq(), [1, 2] + 3)
  [1, 2, 3]
   */
  def apply[A](values: A*): MySet[A] = {
    @scala.annotation.tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if(valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }
    buildSet(values.toSeq, new EmptySet[A])
  }
}