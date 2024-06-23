import cats.effect.IO

case class MeetingTime(startHour: Int, endHour: Int)

object ch08_SchedulingMeetingsImpure {

  /** PREREQUISITE 2: Impure, unsafe and side-effectful API calls Defined in
    * Java code to simulate an imperative client library we don't control.
    *
    * See [[ch08_SchedulingMeetingsAPI.calendarEntriesApiCall()]] and
    * [[ch08_SchedulingMeetingsAPI.createMeetingApiCall()]]
    *
    * We wrap them here to be able to use Scala immutable collections. They
    * randomly fail.
    */
  def calendarEntriesApiCall(name: String): List[MeetingTime] = {
    import scala.jdk.CollectionConverters._
    ch08_SchedulingMeetingsAPI.calendarEntriesApiCall(name).asScala.toList
  }

  def createMeetingApiCall(
      names: List[String],
      meetingTime: MeetingTime
  ): Unit = {
    import scala.jdk.CollectionConverters._
    ch08_SchedulingMeetingsAPI.createMeetingApiCall(names.asJava, meetingTime)
  }
}
