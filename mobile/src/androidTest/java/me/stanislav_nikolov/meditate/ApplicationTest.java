package me.stanislav_nikolov.meditate;

import android.app.Application;
import android.test.ApplicationTestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hirondelle.date4j.DateTime;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testEmptyRun() {
        List<DateTime> sessions = new ArrayList<>();
        List<SessionRun> runs = HelpersKt.getRuns(sessions);

        assertEquals(runs.size(), 0);
    }

    public void testOneDayRun() {
        List<DateTime> sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 2)
        );
        List<SessionRun> runs = HelpersKt.getRuns(sessions);

        assertEquals(runs.size(), 1);

        assertEquals(runs.get(0).getRun(), 1);
        assertEquals(runs.get(0).getNumEntries(), 1);
    }

    public void testMultipleSessionPerDay() {
        List<DateTime> sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 2)
        );
        List<SessionRun> runs = HelpersKt.getRuns(sessions);

        assertEquals(runs.size(), 1);

        assertEquals(runs.get(0).getRun(), 1);
        assertEquals(runs.get(0).getNumEntries(), 2);
    }

    public void testMultipleSessionPerDay2() {
        List<DateTime> sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 4),
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 1)
        );
        List<SessionRun> runs = HelpersKt.getRuns(sessions);

        assertEquals(runs.size(), 2);

        assertEquals(runs.get(0).getRun(), 1);
        assertEquals(runs.get(0).getNumEntries(), 1);

        assertEquals(runs.get(1).getRun(), 2);
        assertEquals(runs.get(1).getNumEntries(), 3);
    }

    public void testMultipleDay() {
        List<DateTime> sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 3),
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 1),
                DateTime.forDateOnly(2914, 1, 1)
        );
        List<SessionRun> runs = HelpersKt.getRuns(sessions);

        assertEquals(runs.size(), 2);

        assertEquals(runs.get(0).getRun(), 3);
        assertEquals(runs.get(0).getNumEntries(), 3);

        assertEquals(runs.get(1).getRun(), 1);
        assertEquals(runs.get(1).getNumEntries(), 1);
    }

}