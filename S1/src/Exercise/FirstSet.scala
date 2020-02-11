package Exercise

object FirstSet extends App {

    def greeting (name : String, age: Int) : String = {
        s"Hello, my name is $name and I am $age years old"
    }








    def factorial (number : Int) : Int = {
        if (number < 2) number
        else number * factorial(number - 1)
    }












    def fibonacci (number : Int) : Int =
        if (number <= 2 ) 1
        else  {
          fibonacci(number - 1) + fibonacci(number - 2)
        }

  def isPrime (number : Int)  : String = {
    @scala.annotation.tailrec
    def inner(number : Int, divisor : Int): String = {
      if(divisor >= number ) {
        "true"
      }else if (number % divisor == 0 ){
        "false"
      }
      else {
        inner(number , divisor + 1)
      }
    }
    inner(number, 2)

  }
  println(greeting("Carl", 27))
  println(factorial(5))
  println(fibonacci(8))
  println(isPrime(10))


}


