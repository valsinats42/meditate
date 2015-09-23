package me.stanislav_nikolov.meditate.db;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by stanley on 12.09.15.
 */
public class BakDbMeditationSession extends RealmObject {
    @PrimaryKey
    private String uuid;
    private Date startTime;
    private Date endTime;
    private int initialDurationSeconds;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getInitialDurationSeconds() {
        return initialDurationSeconds;
    }

    public void setInitialDurationSeconds(int initialDurationSeconds) {
        this.initialDurationSeconds = initialDurationSeconds;
    }
}
