package Lectures.FUNDPart3FuncProgramming

object MapFlatMapFilterFor extends App{

  val list = List(1,2,3)

  list.map(n => n + 1)
  list.map(_  + 1)


  list.filter( n => n == 1)
  list.filter( _ == 1)


  /*
  FlatMap can be defined as a blend of map method and flatten method. The output obtained by running the map method followed by the flatten method
  is the same as flatMap. So we can say flatMap first runs the map method and then the flatten method to generate the desired result.

  FlatMap has a built-in Option class that can be expressed as seq which has at most one element. either its empty or has one element.
  The flatten method is utilized to disintergrate the elements of Scala collection in order to construct a single collection with the elements of
  similar type
   */

  val name = Seq("Carl", "Severe")
  val lowered = name.map(_.toUpperCase())
  val flattend = lowered.flatten

  println(lowered)
  println(flattend)

  val flatLow = name.flatMap(_.toUpperCase)
  println(flatLow)




  val toPair = (x : Int) => List(x, x+1)

  val newList = List(10,20,30)


  var mapped = newList.map(toPair)
  var flattening = mapped.flatten
  println(mapped)
  println(flattening)

  var flatAF = newList.flatMap(toPair)
  println(flatAF)




  //print out all combinations of two lists
  val alpha = List("a","b","c","d")
  val numeric = List(1, 2, 3, 4 )
  val colors = List("blue", "red")






//  val fuse = (x : String , y : String) => List(x + y)
//  val monstrosity = alpha.map(str =>  numeric.map(num => str + num))


//  println(numeric.flatMap(num => alpha.map(str => s"$str$num")))
  //List(a1, b1, c1, d1, a2, b2, c2, d2, a3, b3, c3, d3, a4, b4, c4, d4)





println(
  numeric.map(num => {
    alpha.map(str => {
      colors.map(c =>{ s"$str$num-$c"
      })
    })
  })
)


  println(
    numeric.flatMap(num => {
      alpha.map(str => {
        colors.map(c =>{ s"$str$num-$c"
        })
      })
    })
  )

  println(
    numeric.flatMap(num => {
      alpha.flatMap(str => {
        colors.map(c =>{ s"$str$num-$c"
        })
      })
    })
  )

  println(
    numeric.flatMap(num => {
      alpha.flatMap(str => {
        colors.flatMap(c =>{ s"$str$num-$c"
        })
      })
    })
  )


  println(
    numeric.map(num => {
      alpha.flatMap(str => {
        colors.flatMap(c =>{ s"$str$num-$c"
        })
      })
    })
  )


  println(
    numeric.map(num => {
      alpha.flatMap(str => {
        colors.map(c =>{ s"$str$num-$c"
        })
      })
    })
  )



}