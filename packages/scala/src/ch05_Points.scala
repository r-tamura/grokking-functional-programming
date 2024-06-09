case class Point(x: Int, y: Int)

case class Point3d(x: Int, y: Int, z: Int)

object ch05_Points extends App {
  // 5.12 実習: 入れ子のflatMap
  assert(
    List(1).flatMap(x =>
      List(-2, 7).map(y => {
        Point(x, y)
      })
    ) == List(Point(1, -2), Point(1, 7))
  )

  // 5.15 コーヒーブレイク: flatMapとfor内包表記
  assert(
    (for {
      x <- List(1)
      y <- List(-2, 7)
      z <- List(3, 4)
    } yield Point3d(x, y, z)) == List(
      Point3d(1, -2, 3),
      Point3d(1, -2, 4),
      Point3d(1, 7, 3),
      Point3d(1, 7, 4)
    )
  )
}
