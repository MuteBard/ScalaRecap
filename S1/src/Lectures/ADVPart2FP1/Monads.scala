package Lectures.ADVPart2FP1

object Monads extends App{
  /*
    Monads are a kind of types which have some fundamental ops, like a list
    List, Option, Try, Future, Stream, Set are all monads


    Monads : Anything that has a type parameter, a constructor that takes and element of that type and a flatMap method, is a Monad.
    A monad is a mechanism for sequencing computations, Which is what methods like map and flatMap do. For example, with an Option, you can use them to chain
    several functions together, which do not need to handle an absent value. With a List, you chain operations together, that work on single elements.

    A type that has a map method is called a Functor, it chains together functions that work on elements of the generic type of Functor
    A type that has a flatMap method is the Monad, which is more powerful, as flatMap allows for functions, that return and instance of the monad, there by
    changing the effect the monad has

    For example, map on Option can only convert a present value to another present value. FlatMap can convert it to None.
    For Lists, flatMap allows to change the length of the list by returning a different number of elements from the function given to it

    Think of Monads as a container with operations (map/flatMap,filter,foreach)
    A monad is a mechanism for sequencing computations

    A monad's flatMap method allows us to specify what happens next, taking into account an intermediate complication
    flatMap method of Options takes intermediate Options into account
    flatMap method of Lists handles intermediate Lists




*/
    //Operations must satisfy the monad laws, there are three

    //1) Left Identity : If you build a basic monad out of an element and you flat map it with a function, it should give you the function applied to that element
    //unit(x).flatMap(f) == f(x)
    //unit(x).flatMap(f) == (x => f(x))


    //2) Right Identity : If you have a monad instance and you flat map it with the unit function then is should give you the same monad
    //aMonadInstance.flatMap(unit) == aMonadInstance


    //SO FAR, WHAT YOU  -


    //3) associativity




  trait MonadTemplate[A]{
    def unit(value: A) : MonadTemplate[A]  //also called a pure or apply, constructs a monad out of a value or many values
    def flatMap[B](f: A => MonadTemplate[B]) : MonadTemplate[B] //flatMap or bind, flatMap transforms a monad of a certain type of parameter into a monad of another type of parameter
  }



  //our own try monad

  //////PROTOTYPE OF ATTEMPT MONAD///////
  trait Attempt[+A]{
    def flatMap[B](f: A => Attempt[B]): Attempt[B]//bind method
  }

  object Attempt{
    def apply[A](a: => A): Attempt[A] = {
      try{
        Success(a)
      }catch{
        case e: Throwable => Failure(e)
      }
    } // unit method
  }
  ////////////////////////////////////////


  case class Success[+A](value : A) extends Attempt[A]{
    def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try{
        f(value)
      }catch{
        case e: Throwable => Failure(e)
      }
    }
  }

  case class Failure(e: Throwable) extends Attempt[Nothing]{
    def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
  }


}
