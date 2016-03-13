package me.stanislav_nikolov.meditate.db

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import me.stanislav_nikolov.meditate.toDateTime
import java.util.*

/**
 * Created by stanley on 12.09.15.
 */
@RealmClass
open class DbMeditationSession : RealmObject() {
    @PrimaryKey
    open var uuid: String? = null
    open var startTime: Date? = null
    open var endTime: Date? = null
    open var initialDurationSeconds: Int = 0
    open var comment: String? = null
}

fun DbMeditationSession.getStartDateTime() = startTime!!.toDateTime()
fun DbMeditationSession.getEndDateTime() = endTime!!.toDateTime()
fun DbMeditationSession.getDuration() = getStartDateTime().numSecondsFrom(getEndDateTime())
