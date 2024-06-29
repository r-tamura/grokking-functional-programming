import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeError

case class User(name: String)

object ch08_SchedulingMeetings {
  def calendarEntries(name: String): IO[List[MeetingTime]] = {
    IO.delay(ch08_SchedulingMeetingsImpure.calendarEntriesApiCall(name))
  }

  def createMeeting(names: List[String], meeting: MeetingTime): IO[Unit] = {
    IO.delay(ch08_SchedulingMeetingsImpure.createMeetingApiCall(names, meeting))
  }

  def scheduledMeetings(
      person1: String,
      person2: String
  ): IO[List[MeetingTime]] = {
    for {
      person1Meetings <- calendarEntries(person1)
      person2Meetings <- calendarEntries(person2)
    } yield person1Meetings.appendedAll(person2Meetings)
  }
}
import ch08_SchedulingMeetings._

object ch0818 {

  def meetingsOverlap(meeting1: MeetingTime, meeting2: MeetingTime): Boolean = {
    meeting1.startHour < meeting2.endHour && meeting2.startHour < meeting1.endHour
  }

  /** MeetingTimeのリストが与えられたとき
    *   - startHour以降に開始する
    *   - endHour以前に終了する
    *   - lengthHours以上の続く
    */
  def possibleMeetings(
      existingMeetings: List[MeetingTime],
      startHour: Int,
      endHour: Int,
      lengthHours: Int
  ): List[MeetingTime] = {
    List
      .range(startHour, endHour - lengthHours + 1)
      .filter(startHour =>
        !existingMeetings.exists(
          meetingsOverlap(_, MeetingTime(startHour, startHour + lengthHours))
        )
      )
      .map(startHour => MeetingTime(startHour, startHour + lengthHours))
  }

  def schedule(
      person1: String,
      person2: String,
      lengthHours: Int
  ): IO[Unit] = {
    for {
      existingMeetings <- scheduledMeetings(person1, person2)
      meetings = possibleMeetings(
        existingMeetings,
        8,
        16,
        lengthHours
      )
    } yield meetings.headOption match {
      case Some(meeting) =>
        createMeeting(List(person1, person2), meeting)
          .orElse(createMeeting(List(person1, person2), meeting))
          .orElse(IO.unit)
      case None => IO.unit
    }
  }
}
import ch0818._

object ch08_SchedulingMeetingsApp extends App {
  // 8.15 実習: IO型の値の作成と結合
  val _ = calendarEntries("Alice")
  val _ = createMeeting(List("Alice", "Bob"), MeetingTime(10, 11))
  val _ = scheduledMeetings("Alice", "Bob")

  // 8.18 コーヒーブレイク: 値を操作する
  println(
    possibleMeetings(
      List(MeetingTime(9, 10), MeetingTime(11, 12)),
      9,
      12,
      1
    )
  )
  assert(
    possibleMeetings(
      List(MeetingTime(9, 10), MeetingTime(11, 12)),
      9,
      12,
      1
    ) == List(MeetingTime(10, 11)),
    "開始時間と終了時間内で行われるMeetingTimeの一覧を返す"
  )

  assert(
    possibleMeetings(
      List(MeetingTime(9, 10), MeetingTime(11, 12)),
      9,
      12,
      2
    ) == List.empty[MeetingTime],
    "lengthHours以上続くMeetingTimeの一覧を返す"
  )

  println(schedule("Alice", "Bob", 1).unsafeRunSync())

}
