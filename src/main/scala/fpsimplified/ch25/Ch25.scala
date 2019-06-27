package fpsimplified.ch25

object Ch25 {

  // Timer with Function parameter
  def timerWithFunc[A](func: () => A) = {
    val start = System.nanoTime()

    val result = func()

    val stop = System.nanoTime()

    val delta = stop - start

    (result, delta / 1000000d)
  }

  // Timer with By Name parameter
  def timerByName[A](block: => A) = {
    val start = System.nanoTime()

    val result = block

    val stop = System.nanoTime()

    val delta = stop - start

    (result, delta / 1000000d)
  }

  // Example usages of each
  def main(args: Array[String]): Unit = {

    val resultWithFunc = timerWithFunc(() => {    // must use () => {...} format
      println("Starting timerWithFunc...")
      Thread.sleep(1000)
      println("Done!")
      true
    })
    println(resultWithFunc + "\n")

    val resultByName = timerByName {      // methods can use (arg) or {arg} for arguments
      println("Starting timerByName...")
      Thread.sleep(1000)
      println("Done!")
      true
    }
    println(resultByName + "\n")

  }

}
