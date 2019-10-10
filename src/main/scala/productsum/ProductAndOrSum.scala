package productsum

import java.time.LocalDate

sealed trait Animal
object Tiger extends Animal
object Penguin extends Animal

class ProductAndOrSum() {

  // productType
  case class Person(first: String, last: String, dob: LocalDate)

  // sum type
  val maybe: Option[String] = Some("exists")

  val person = Person("erik", "dreyer", LocalDate.now())

  val x = person match {
    case Person(first, last, dob) => first
  }
  println(x)

  val y = maybe match {
    case Some(str) => str.toUpperCase()
      case None => "Nada"
  }
  println(y)

}

object ProductAndOrSum extends App {
  override def main(args: Array[String]) = {
    val foo = new ProductAndOrSum()
  }
}
