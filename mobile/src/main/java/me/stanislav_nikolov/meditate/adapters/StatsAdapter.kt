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
import io.realm.Realm
import io.realm.RealmResults
import me.leolin.shortcutbadger.ShortcutBadger
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.endsTodayAdjusted
import me.stanislav_nikolov.meditate.db.getAdjustedEndTime
import me.stanislav_nikolov.meditate.db.getDuration
import me.stanislav_nikolov.meditate.getRuns
import timber.log.Timber
import java.util.*

/**
 * Created by stanley on 05.09.15.
 */


public class StatsAdapter(val context: Context, val realm: Realm, val data: RealmResults<DbMeditationSession>): RecyclerView.Adapter<StatsAdapter.ViewHolder>() {

    data class MeditationStat(val name: String, val value: String)

    val stats = ArrayList<MeditationStat>()

    private val changeListener = {
        Timber.d("Change detected")

        calculateStats()
        notifyDataSetChanged()
    }

    init {
        calculateStats()

        realm.addChangeListener(changeListener)
    }

    private fun calculateStats() {
        Timber.d("Recalculating stats...")

        val runs = getRuns(data map { it.getAdjustedEndTime() })

        val currentRun = if (!data.isEmpty() && data[0].endsTodayAdjusted()) { runs[0].run } else { 0 }

        val bestRun = runs.map({ it.run }).max() ?: 0

        val durationsSeconds = data map { it.getDuration() }
        val totalTimeMeditatingMinutes = durationsSeconds.sum().toInt() / 60
        val avgSessionDuration = durationsSeconds.average().toInt() / 60

        fun qm(q: Int) = context.resources.getQuantityString(R.plurals.minutes, q, q)
        fun qd(q: Int) = context.resources.getQuantityString(R.plurals.days, q, q)

        stats.clear()
        stats.addAll(listOf(
                StatsAdapter.MeditationStat("Current Run Streak", qd(currentRun)),
                StatsAdapter.MeditationStat("Best Run Streak", qd(bestRun)),
                StatsAdapter.MeditationStat("Number of Sessions", data.size().toString()),
                StatsAdapter.MeditationStat("Average Session Length", qm(avgSessionDuration)),
                StatsAdapter.MeditationStat("Total Meditation Time", qm(totalTimeMeditatingMinutes))
        ))

        ShortcutBadger.setBadge(context, currentRun)
    }

    public class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        public var title: TextView
        public var subtitle: TextView

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

    override fun getItemCount() = stats.size()
}
