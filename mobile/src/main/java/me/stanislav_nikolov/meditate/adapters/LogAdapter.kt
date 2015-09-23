package me.stanislav_nikolov.meditate.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmResults
import me.stanislav_nikolov.meditate.*
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.RealmBaseAdapter
import java.util.Locale

/**
 * Created by stanley on 05.09.15.
 */

public class LogAdapter
(realm: Realm, data: RealmResults<DbMeditationSession>) : RealmBaseAdapter<DbMeditationSession, LogAdapter.ViewHolder>(realm, data) {

    enum class ListPosition { ALONE, FIRST, MIDDLE, LAST }

    private val runs: List<ListPosition>

    init {
        runs = getRuns(data)
                .flatMap {
                    when (it) {
                        1 -> listOf(ListPosition.ALONE)
                        else -> listOf(ListPosition.FIRST, *Array(it - 2, { ListPosition.MIDDLE }), ListPosition.LAST)
                    }
                }
    }

    public class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        public var title: TextView
        public var subtitle: TextView
        public var run: TextView

        init {
            title = v.findViewById(R.id.textViewDate) as TextView
            subtitle = v.findViewById(R.id.textViewDuration) as TextView
            run = v.findViewById(R.id.textViewRun) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meditation_session_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = getItem(position)

        val startTime = session.getStartDateTime().format("WWWW, MMMM D @ hh:mm", Locale.getDefault())
        val (h, m) = session.getDuration().toHMS()
        val duration = when {
            h > 0   -> "$h h $m min"
            else    -> "$m min"
        }

        val runText = when (runs[position]) {
            ListPosition.ALONE -> "O"
            ListPosition.FIRST -> "^"
            ListPosition.MIDDLE -> " | "
            ListPosition.LAST -> "v"
        }

        holder.title.setText(startTime)
        holder.subtitle.setText(duration)
        holder.run.setText(runText)
    }
}