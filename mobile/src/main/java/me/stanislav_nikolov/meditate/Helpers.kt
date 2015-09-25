package me.stanislav_nikolov.meditate

import hirondelle.date4j.DateTime
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.getAdjustedEndTime
import java.util.Date
import java.util.TimeZone

/**
 * Created by stanley on 10.09.15.
 */

data class HMS(val h: Long, val m: Long, val s: Long)

fun Long.toHMS() = secondsToHMS(this)

fun secondsToHMS(seconds: Long): HMS {
    val h = seconds / 3600
    val m = (seconds / 60) % 60
    val s = seconds % 60

    return HMS(h, m, s)
}

data class SessionRun(val numEntries: Int, val run: Int)

fun getRuns(sessions: List<DateTime>): List<SessionRun> {
    val endDays = sessions map { it.modifiedJulianDayNumber }

    if (sessions.count() == 0) return emptyList()

    var result = arrayListOf<SessionRun>()
    var currentSessionCount = 1

    for (i in 1..endDays.lastIndex) {
        when (endDays[i] - endDays[i-1]) {
            0, 1, -1 -> currentSessionCount++
            else -> {
                result.add(SessionRun(currentSessionCount, 1 + Math.abs(endDays[i-1] - endDays[i - currentSessionCount])))
                currentSessionCount = 1
            }
        }
    }

    val i = endDays.size()
    result.add(SessionRun(currentSessionCount, 1 + Math.abs(endDays[i-1] - endDays[i - currentSessionCount])))

    return result
}

fun Date.toDateTime() = DateTime.forInstant(this.time, TimeZone.getDefault())
fun DateTime.toDate() = Date(getMilliseconds(TimeZone.getDefault()))
