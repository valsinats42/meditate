package me.stanislav_nikolov.meditate.db

import hirondelle.date4j.DateTime

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import me.stanislav_nikolov.meditate.toDateTime
import java.util.*

/**
 * Created by stanley on 12.09.15.
 */
@RealmClass
public open class DbMeditationSession : RealmObject() {
    @PrimaryKey
    public open var uuid: String? = null
    public open var startTime: Date? = null
    public open var endTime: Date? = null
    public open var initialDurationSeconds: Int = 0
    public open var comment: String? = null
}

val DAY_START_H = 4
val DAY_START_M = 0

fun DbMeditationSession.getStartDateTime() = startTime!!.toDateTime()
fun DbMeditationSession.getEndDateTime() = endTime!!.toDateTime()
fun DbMeditationSession.getDuration() = getStartDateTime().numSecondsFrom(getEndDateTime())
fun DbMeditationSession.endsTodayAdjusted() = getEndDateTime().isSameDayAs(DateTime.today(TimeZone.getDefault()))
fun DbMeditationSession.getAdjustedEndTime() = getEndDateTime()
        .minus(0, 0, 0, DAY_START_H, DAY_START_M, 0, 0, DateTime.DayOverflow.Spillover)
