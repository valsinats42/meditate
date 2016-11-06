package me.stanislav_nikolov.meditate.db

import hirondelle.date4j.DateTime
import io.realm.Realm
import io.realm.RealmChangeListener
import me.stanislav_nikolov.meditate.now
import me.stanislav_nikolov.meditate.toDate
import timber.log.Timber

/**
 * Created by stanley on 13.10.15.
 */

class SessionDb constructor(val realm: Realm) {
    init {
//        loadDb()
    }

    val allSessions = realm.allObjectsSorted(DbMeditationSession::class.java, "endTime", false)

    fun addChangeListener(changeListener: RealmChangeListener) = realm.addChangeListener(changeListener)
    fun removeChangeListener(changeListener: RealmChangeListener) = realm.removeChangeListener(changeListener)

    fun saveSession(session: DbMeditationSession) {
        realm.beginTransaction()
        realm.copyToRealm(session)
        realm.commitTransaction()

        Timber.d("Saved new session: %s -- %s", session.startTime, session.endTime)
    }

    fun deleteSessions(selectedSessionUuids: List<String?>) {
        realm.beginTransaction()

        var clause = realm.where(DbMeditationSession::class.java)
            .equalTo("uuid", selectedSessionUuids[0])
        for (i in 1..selectedSessionUuids.lastIndex) {
            clause = clause.or().equalTo("uuid", selectedSessionUuids[i])
        }
        clause.findAll().clear()

        realm.commitTransaction()
    }

    private fun loadDb() {
        var dates = arrayOf(
                DateTime("2010-01-19 23:59:59"),
                DateTime("2010-01-20 23:59:59"),
                DateTime("2010-01-21 23:59:59"),
                DateTime("2010-01-23 23:59:59"),
                now()
        )

        realm.beginTransaction()
        dates.forEach {
            val m = realm.createObject(DbMeditationSession::class.java)
            m.uuid = java.util.UUID.randomUUID().toString()
            m.startTime = it.minus(0, 0, 0, 0, 30, 0, 0, DateTime.DayOverflow.Spillover).toDate()
            m.endTime = it.toDate()
            m.initialDurationSeconds = 15 * 60
        }
        realm.commitTransaction()
    }

    fun lastSessionLengthOrDefault(defaultLength: Int): Int {
        return allSessions
                .filter { it.getDuration() >= it.initialDurationSeconds }
                .firstOrNull()?.initialDurationSeconds?.div(60) ?:
                defaultLength
    }
}
