package me.stanislav_nikolov.meditate.db

import java.util.Date

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by stanley on 12.09.15.
 */
public class DbMeditationSession : RealmObject() {
    PrimaryKey
    public var uuid: String? = null
    public var startTime: Date? = null
    public var endTime: Date? = null
    public var initialDurationSeconds: Int = 0
    public var comment: String? = null
}
