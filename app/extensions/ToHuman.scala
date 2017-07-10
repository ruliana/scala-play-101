package extensions

import java.time.format.DateTimeFormatter

object ToHuman {
  implicit class ZonedDateTime(val date: java.time.ZonedDateTime) {
    private[this] val humanPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    def toHuman: String = date.format(humanPattern)
  }
}
