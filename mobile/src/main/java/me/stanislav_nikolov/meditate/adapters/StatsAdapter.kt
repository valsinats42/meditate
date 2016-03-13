/**
 * Created by stanley on 16.09.15.
 */
package me.stanislav_nikolov.meditate.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import hirondelle.date4j.DateTime
import io.realm.RealmChangeListener
import io.realm.RealmResults
import me.leolin.shortcutbadger.ShortcutBadger
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adjustMidnigth
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.SessionDb
import me.stanislav_nikolov.meditate.db.getDuration
import me.stanislav_nikolov.meditate.db.getStartDateTime
import me.stanislav_nikolov.meditate.getRuns
import me.stanislav_nikolov.meditate.today
import timber.log.Timber
import java.util.*

/**
 * Created by stanley on 05.09.15.
 */


class StatsAdapter(val context: Context, val db: SessionDb): RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    data class MeditationStat(val name: String, val value: String)

    val data: RealmResults<DbMeditationSession>
    val stats = ArrayList<MeditationStat>()

    private val changeListener = object : RealmChangeListener {
        override fun onChange() {
            Timber.d("Change detected")

            calculateStats()
            notifyDataSetChanged()
        }
    }

    init {
        data = db.allSessions
        db.addChangeListener(changeListener)

        calculateStats()
    }

    private fun calculateStats() {
        Timber.d("Recalculating stats...")

        val runs = getRuns(data.map { it.getStartDateTime().adjustMidnigth() })

        fun DateTime.isAdjustedToday() = adjustMidnigth().isSameDayAs(today().adjustMidnigth())
        fun DateTime.isAdjustedYesterday() = adjustMidnigth().isSameDayAs(today().minusDays(1).adjustMidnigth())

        var doneToday = false
        val currentRun = when {
            data.isEmpty() -> 0
            data[0].getStartDateTime().isAdjustedToday() -> {
                doneToday = true
                runs[0].run
            }
            data[0].getStartDateTime().isAdjustedYesterday() -> runs[0].run
            else -> 0
        }

        val bestRun = runs.map({ it.run }).max() ?: 0

        val totalTimeMeditatingMinutes = data
                .map { it.getDuration() }
                .sum()
                .toInt() / 60
        val avgSessionDuration = data
                .filter { it.getDuration() >= it.initialDurationSeconds }
                .map { it.getDuration() }
                .average()
                .toInt() / 60

        val res = context.resources
        fun qm(q: Int) = res.getQuantityString(R.plurals.minutes, q, q)
        fun qd(q: Int) = res.getQuantityString(R.plurals.days, q, q)
        fun s(id: Int) = res.getString(id)

        val inclusionSuffix = when {
            currentRun == 0 -> ""
            doneToday -> " (${res.getString(R.string.including_today)})"
            else -> " (${res.getString(R.string.excluding_today)})"
        }

        stats.clear()
        stats.addAll(listOf(
                StatsAdapter.MeditationStat(s(R.string.current_run_streak), qd(currentRun) + inclusionSuffix),
                StatsAdapter.MeditationStat(s(R.string.best_run_streak), qd(bestRun)),
                StatsAdapter.MeditationStat(s(R.string.number_of_sessions), data.size.toString()),
                StatsAdapter.MeditationStat(s(R.string.average_session_length), qm(avgSessionDuration)),
                StatsAdapter.MeditationStat(s(R.string.total_meditation_time), qm(totalTimeMeditatingMinutes))
        ))

        ShortcutBadger.applyCount(context, currentRun)

    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var title: TextView
        var subtitle: TextView

        init {
            title = v.findViewById(R.id.textViewStatName) as TextView
            subtitle = v.findViewById(R.id.textViewStatValue) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.stats_adapter_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stat = stats[position]

        holder.title.text = stat.name
        holder.subtitle.text = stat.value
    }

    override fun getItemCount() = stats.size
}
