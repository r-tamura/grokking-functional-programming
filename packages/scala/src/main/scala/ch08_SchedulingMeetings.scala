import cats.effect.IO

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

object ch08_SchedulingMeetingsApp extends App {
  // 8.15 実習: IO型の値の作成と結合
  val _ = calendarEntries("Alice")
  val _ = createMeeting(List("Alice", "Bob"), MeetingTime(10, 11))
  val _ = scheduledMeetings("Alice", "Bob")
}
