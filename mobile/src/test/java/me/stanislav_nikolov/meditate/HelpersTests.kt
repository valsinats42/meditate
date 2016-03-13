package me.stanislav_nikolov.meditate

import hirondelle.date4j.DateTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Created by stanley on 13.03.16.
 */
class HelpersTests {
    @Test
    fun testEmptyRun() {
        val sessions = ArrayList<DateTime>()
        val runs = getRuns(sessions)

        assertEquals(runs.size.toLong(), 0)
    }

    @Test
    fun testOneDayRun() {
        val sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 2))
        val runs = getRuns(sessions)

        assertEquals(runs.size.toLong(), 1)

        assertEquals(runs[0].run.toLong(), 1)
        assertEquals(runs[0].numEntries.toLong(), 1)
    }

    @Test
    fun testMultipleSessionPerDay() {
        val sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 2))
        val runs = getRuns(sessions)

        assertEquals(runs.size.toLong(), 1)

        assertEquals(runs[0].run.toLong(), 1)
        assertEquals(runs[0].numEntries.toLong(), 2)
    }

    @Test
    fun testMultipleSessionPerDay2() {
        val sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 4),
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 1))
        val runs = getRuns(sessions)

        assertEquals(runs.size.toLong(), 2)

        assertEquals(runs[0].run.toLong(), 1)
        assertEquals(runs[0].numEntries.toLong(), 1)

        assertEquals(runs[1].run.toLong(), 2)
        assertEquals(runs[1].numEntries.toLong(), 3)
    }

    @Test
    fun testMultipleDay() {
        val sessions = Arrays.asList(
                DateTime.forDateOnly(2015, 1, 3),
                DateTime.forDateOnly(2015, 1, 2),
                DateTime.forDateOnly(2015, 1, 1),
                DateTime.forDateOnly(2914, 1, 1))
        val runs = getRuns(sessions)

        assertEquals(runs.size.toLong(), 2)

        assertEquals(runs[0].run.toLong(), 3)
        assertEquals(runs[0].numEntries.toLong(), 3)

        assertEquals(runs[1].run.toLong(), 1)
        assertEquals(runs[1].numEntries.toLong(), 1)
    }

}
