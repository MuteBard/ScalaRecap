package Exercise

object MyList extends App{
  /*
  head  = first element of the list
  tail = remainder of the list
  isEmpty = is this list empty
  add(int) => new list with this element added
  toString => a string representation of this list
   */


  val intList = new Node(1, new Node(2, new Node(3, Empty)))
  val strList = new Node("a", new Node("b", new Node("c", new Node("d", Empty))))
  println(intList.toString)
  println(strList.toString)


//  val list = new Node(1, Empty)
//  val list2 = new Node(1, new Node(2, new Node(3, Empty)))
//  println(list.value)
//  println(list2.next.next.value)
//  println(list.add(5).next.value.toString)
////  println(list.add(5).next.value)
//  println(list2.toString)




}


abstract class LL[+A] {
  def value : A
  def next : LL[A]
  def isEmpty : Boolean
  def add[B >: A](v : B) : LL[B]
  /*
    A = Cat
    B = Dog = Animal
    If to a list of A, I put in a B, which is a supertype of A this list will turn into a B
   */
  def printElements : String
  override def toString: String = s"[$printElements]"
}


//The nothing  type  is a proper substitute for any type
object Empty extends LL[Nothing] {
  def value : Nothing = throw new NoSuchElementException
  def next : LL[Nothing] = throw new NoSuchElementException
  def isEmpty : Boolean = true
  def add[B >: Nothing](v : B) : LL[B] = new Node(v, Empty)
  def printElements : String = ""
}

class Node[+A](h : A, t : LL[A]) extends LL[A] {
  def value: A = h
  def next: LL[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](v : B): LL[B] = new Node(v, this)
  def printElements : String = {
    if(t.isEmpty) s"$h"
    else s"$h ${t.printElements}"
  }



//abstract class LL {
//  def value : Int
//  def next : LL
//  def isEmpty : Boolean
//  def add(num : Int) : LL
//  def printElements : String
//  override def toString: String = s"[$printElements]"
//}
//
//object Empty extends LL {
//  def value : Int = throw new NoSuchElementException
//  def next : LL = throw new NoSuchElementException
//  def isEmpty : Boolean = true
//  def add(num : Int) : LL = new Node(num, Empty)
//  def printElements : String = ""
//}
//
//class Node(h : Int, t : LL) extends LL {
//  def value: Int = h
//  def next: LL = t
//  def isEmpty: Boolean = false
//  def add(num : Int): LL = new Node(num, this)
//  def printElements : String = {
//    if(t.isEmpty) s"$h"
//    else s"$h ${t.printElements}"
//  }
}








