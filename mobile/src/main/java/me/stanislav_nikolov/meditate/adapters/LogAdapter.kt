package me.stanislav_nikolov.meditate.adapters

import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import io.realm.Realm
import io.realm.RealmResults
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.db.*
import me.stanislav_nikolov.meditate.getRuns
import me.stanislav_nikolov.meditate.toHMS
import timber.log.Timber
import java.util.*

/**
 * Created by stanley on 05.09.15.
 */

public class LogAdapter(val realm: Realm, val data: RealmResults<DbMeditationSession>): RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    enum class ListPosition { ALONE, FIRST, MIDDLE, LAST }

    private var runs: List<ListPosition> = emptyList()

    private val changeListener = {
        updateRuns()
        notifyDataSetChanged()
    }

    private fun updateRuns() {
        runs = getRuns(data map { it.getAdjustedEndTime() })
                .flatMap {
                    when (it.numEntries) {
                        0 -> emptyList<ListPosition>()
                        1 -> listOf(ListPosition.ALONE)
                        else -> listOf(ListPosition.FIRST, *Array(it.numEntries - 2, { ListPosition.MIDDLE }), ListPosition.LAST)
                    }
                }

        Timber.d("Updated runs: data size: %d, runs size: %d", data.size(), runs.size())
    }

    init {
        realm.addChangeListener(changeListener)

        updateRuns()
    }

    val multiSelector = MultiSelector()

    private var actionMode: ActionMode? = null

    private val actionModeCallback = object : ModalMultiSelectorCallback(multiSelector) {

        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            super.onCreateActionMode(actionMode, menu)
            actionMode?.menuInflater?.inflate(R.menu.session_log_menu, menu)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            realm.beginTransaction()

            val selectedSessionuuids = multiSelector.selectedPositions.map { data[it].uuid }
            // TODO fix this when there's a sane way to remove multiple results
            for (s in selectedSessionuuids) {
                for (i in 0 .. data.lastIndex) {
                    if (data[i].uuid == s) {
                        data.remove(i)
                        break
                    }
                }
            }

            realm.commitTransaction()

            notifyDataSetChanged()

            multiSelector.clearSelections()
            mode?.finish()
            actionMode = null

            return true
        }
    }

    fun updateTitle() {
        val n = multiSelector.selectedPositions.size()
        actionMode?.title = "$n session${if (n != 1) "s" else ""} selected"
    }

    inner public class ViewHolder(v: View) : SwappingHolder(v, multiSelector) {
        public var title: TextView
        public var subtitle: TextView
        public var run: ImageView

        init {
            title = v.findViewById(R.id.textViewDate) as TextView
            subtitle = v.findViewById(R.id.textViewDuration) as TextView
            run = v.findViewById(R.id.runIndicator) as ImageView

            selectionModeStateListAnimator = null

            v.setOnClickListener {
                if (multiSelector.tapSelection(this)) {
                    if (multiSelector.selectedPositions.size() == 0) {
                        actionMode?.finish()
                        actionMode = null
                    } else {
                        updateTitle()
                    }
                }
            }

            v.isLongClickable = true
            v.setOnLongClickListener {
                actionMode = (v.context as AppCompatActivity).startSupportActionMode(actionModeCallback)
                multiSelector.setSelected(this, true)

                updateTitle()

                true
            }
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