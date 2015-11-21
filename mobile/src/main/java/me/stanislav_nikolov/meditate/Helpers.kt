package me.stanislav_nikolov.meditate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.SoundPool
import android.support.v4.app.Fragment
import hirondelle.date4j.DateTime
import me.stanislav_nikolov.meditate.db.SessionDb
import me.stanislav_nikolov.meditate.db.getEndDateTime
import me.stanislav_nikolov.meditate.db.getStartDateTime
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * Created by stanley on 10.09.15.
 */

data class HMS(val h: Long, val m: Long, val s: Long)

fun secondsToHMS(seconds: Int) = secondsToHMS(seconds.toLong())
fun secondsToHMS(seconds: Long): HMS {
    val h = seconds / 3600
    val m = (seconds / 60) % 60
    val s = seconds % 60

    return HMS(h, m, s)
}

data class SessionRun(val numEntries: Int, val run: Int)

fun getRuns(sessions: List<DateTime>): List<SessionRun> {
    val endDays = sessions.map { it.modifiedJulianDayNumber }

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

    val i = endDays.size
    result.add(SessionRun(currentSessionCount, 1 + Math.abs(endDays[i-1] - endDays[i - currentSessionCount])))

    return result
}

val DAY_START_H = 4
val DAY_START_M = 0

fun now() = DateTime.now(TimeZone.getDefault())
fun today() = DateTime.now(TimeZone.getDefault())
fun DateTime.adjustMidnigth() = minus(0, 0, 0, DAY_START_H, DAY_START_M, 0, 0, DateTime.DayOverflow.Spillover)
fun DateTime.toDate() = Date(getMilliseconds(TimeZone.getDefault()))
fun Date.toDateTime() = DateTime.forInstant(this.time, TimeZone.getDefault())

fun Fragment.graph() = (activity.application as MeditateApp).graph
fun Context.graph() = (applicationContext as MeditateApp).graph

fun exportData(context: Context, db: SessionDb) {
    val dateFormat = "YYYY-MM-DD hh:mm:ss"
    val root = JSONObject()

    val list = JSONArray()

    for (r in db.allSessions) {
        val session = JSONObject()
        session.put("uuid", r.uuid)
        session.put("start", r.getStartDateTime().format(dateFormat, Locale.getDefault()))
        session.put("end", r.getEndDateTime().format(dateFormat, Locale.getDefault()))
        list.put(session)
    }

    root.put("sessions", list)

    val sharedData = root.toString()

    val sendIntent = Intent()
    sendIntent.setAction(Intent.ACTION_SEND)
    sendIntent.putExtra(Intent.EXTRA_TEXT, sharedData)
    sendIntent.setType("application/json")
    context.startActivity(sendIntent)
}
