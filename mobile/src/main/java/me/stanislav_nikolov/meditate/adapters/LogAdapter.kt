package me.stanislav_nikolov.meditate.adapters

import android.app.Application
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmResults
import me.stanislav_nikolov.meditate.*
import me.stanislav_nikolov.meditate.db.*
import java.util.Locale

/**
 * Created by stanley on 05.09.15.
 */

public class LogAdapter(val realm: Realm, val data: RealmResults<DbMeditationSession>): RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    enum class ListPosition { ALONE, FIRST, MIDDLE, LAST }

    private val runs: List<ListPosition>

    init {
        runs = getRuns(data map { it.getAdjustedEndTime() })
                .flatMap {
                    when (it.numEntries) {
                        0 -> emptyList<ListPosition>()
                        1 -> listOf(ListPosition.ALONE)
                        else -> listOf(ListPosition.FIRST, *Array(it.numEntries - 2, { ListPosition.MIDDLE }), ListPosition.LAST)
                    }
                }
    }

    public class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        public var title: TextView
        public var subtitle: TextView
        public var run: ImageView

        init {
            title = v.findViewById(R.id.textViewDate) as TextView
            subtitle = v.findViewById(R.id.textViewDuration) as TextView
            run = v.findViewById(R.id.runIndicator) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.log_adapter_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = data[position]

        val startDate = if (session.endsTodayAdjusted()) {
            "Today"
        } else {
            session.getStartDateTime().format("WWWW, MMMM D", Locale.getDefault())
        }
        var startTime = session.getStartDateTime().format("hh:mm")
        var endTime = session.getEndDateTime().format("hh:mm")

        val (h, m) = session.getDuration().toHMS()
        val duration = when {
            h > 0   -> "$h h $m min"
            else    -> "$m min"
        }

        val runIndicator = when (runs[position]) {
            ListPosition.ALONE -> R.drawable.run_indicator_only
            ListPosition.FIRST -> R.drawable.run_indicator_top
            ListPosition.MIDDLE -> R.drawable.run_indicator_middle
            ListPosition.LAST -> R.drawable.run_indicator_bottom
        }

        holder.title.text = startDate
        holder.subtitle.text = "$duration ($startTime\u2013$endTime)"
        holder.run.setImageResource(runIndicator)
    }

    override fun getItemCount() = data.size()
}