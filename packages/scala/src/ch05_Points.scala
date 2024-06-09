case class Point(x: Int, y: Int)

case class Point3d(x: Int, y: Int, z: Int)

def isInside(point: Point, radius: Int): Boolean = {
  radius * radius >= point.x * point.x + point.y * point.y
}

def insideFilter(point: Point, radius: Int): List[Point] = {
  if (isInside(point, radius)) List(point) else List.empty
}

def validateRaidus(radius: Int): List[Int] = {
  if (radius >= 0) List(radius) else List.empty
}

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

  // 5.23 コーヒーブレイク: フィルタリングの方法
  {
    val points = List(Point(5, 2), Point(1, 1))
    val reskyReadiuses = List(-10, 0, 2)

    val expected = List(
      "Point(1,1) is within a radius of 2"
    )

    // Listのfilter関数
    assert(
      (for {
        r <- reskyReadiuses.filter(r => r >= 0)
        point <- points.filter(p => isInside(p, r))
      } yield s"$point is within a radius of $r")
        == expected
    )
    // ガード式
    assert((for {
      r <- reskyReadiuses if r >= 0
      point <- points if isInside(point, r)
    } yield s"$point is within a radius of $r") == expected)

    // flatMap関数に渡された関数
    assert(
      (for {
        r <- reskyReadiuses
        validRadius <- validateRaidus(r)
        point <- points
        inPoint <- insideFilter(point, r)
      } yield s"$inPoint is within a radius of $validRadius")
        == expected
    )
  }
}
