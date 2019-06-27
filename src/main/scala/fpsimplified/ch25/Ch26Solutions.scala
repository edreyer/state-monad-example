package fpsimplified.ch25

object Ch26Solutions {

  def whilst[A](predicate: => Boolean)(block: => Unit) = {
    while(predicate) {
      block
    }
  }

}
