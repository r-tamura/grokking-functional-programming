case class Point(x: Int, y: Int)

object ch05_Points extends App {
  // 5.12 実習: 入れ子のflatMap
  assert(
    List(1).flatMap(x =>
      List(-2, 7).map(y => {
        Point(x, y)
      })
    ) == List(Point(1, -2), Point(1, 7))
  )
}
