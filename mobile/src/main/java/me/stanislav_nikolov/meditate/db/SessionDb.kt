package me.stanislav_nikolov.meditate.db

import hirondelle.date4j.DateTime
import io.realm.Realm
import me.stanislav_nikolov.meditate.toDate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by stanley on 13.10.15.
 */

class SessionDb @Singleton @Inject constructor(val realm: Realm) {
    public val allSessions = realm.allObjectsSorted(DbMeditationSession::class.java, "endTime", false)

    public fun addChangeListener(changeListener: Function0<Unit>) = realm.addChangeListener(changeListener)

    public fun saveSession(session: DbMeditationSession) {
        realm.beginTransaction()
        realm.copyToRealm(session)
        realm.commitTransaction()

        Timber.d("Saved new session: %s -- %s", session.startTime, session.endTime)
    }

    fun deleteSessions(selectedSessionuuids: List<String?>) {
        realm.beginTransaction()

        // TODO fix this when there's a sane way to remove multiple results
        for (s in selectedSessionuuids) {
            for (i in 0 .. allSessions.lastIndex) {
                if (allSessions[i].uuid == s) {
                    allSessions.remove(i)
                    break
                }
            }
        }

        realm.commitTransaction()
    }

    private fun loadDb() {
        var dates = arrayOf(
                DateTime("2010-01-19 23:59:59"),
                DateTime("2010-01-20 23:59:59"),
                DateTime("2010-01-21 23:59:59"),
                DateTime("2010-01-23 23:59:59"),
                DateTime.now(java.util.TimeZone.getDefault())
        )

        realm.beginTransaction()
        dates.forEach {
            val m = realm.createObject(DbMeditationSession::class.java)
            m.uuid = java.util.UUID.randomUUID().toString()
            m.startTime = it.minus(0, 0, 0, 0, 30, 0, 0, DateTime.DayOverflow.Spillover).toDate()
            m.endTime = it.toDate()
        }
        realm.commitTransaction()
    }
}
