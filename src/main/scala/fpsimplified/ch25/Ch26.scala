package fpsimplified.ch25

import fpsimplified.ch25.Ch26Solutions.whilst

object Ch26 {

  // Typical sum function
  def sum(a: Int, b: Int) = a + b

  // Multiple parameters
  def sum2(a: Int)(b: Int) = a + b

  def main(args: Array[String]): Unit = {


    // Normal 2 arg function
    println( sum(5, 10) )




    // Calling sum2 with two argument lists
    println( sum2(5)(10) )




    // Partially-applied function (see underscore)
    // More in Ch27
    val intermediate: Int => Int = sum2(5)(_)
    val answer: Int = intermediate(10)
    println(answer)




    // You can make 2nd arg look like a method body
    println( sum2(5) {
      10
    })
    println



    // using scala built in 'while' loop
    var i = 1
    while(i <= 5) {
      println(i)
      i += 1
    }
    println




    // designing methods to look like built in language features
    var j = 1
    whilst(j <= 5) {
      println(j)
      j += 1
    }

  }


}
