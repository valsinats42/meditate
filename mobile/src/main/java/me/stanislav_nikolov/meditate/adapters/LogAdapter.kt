package me.stanislav_nikolov.meditate.adapters

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback
import com.bignerdranch.android.multiselector.MultiSelector
import com.bignerdranch.android.multiselector.SwappingHolder
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import me.stanislav_nikolov.meditate.*
import me.stanislav_nikolov.meditate.db.*
import timber.log.Timber
import java.text.DateFormat
import java.util.*

/**
 * Created by stanley on 05.09.15.
 */

public class LogAdapter(val context: Context, val db: SessionDb): RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    enum class ListPosition { ALONE, FIRST, MIDDLE, LAST }

    private val data: RealmResults<DbMeditationSession>
    private var runs: List<ListPosition> = emptyList()

    private val changeListener = object : RealmChangeListener {
        override fun onChange() {
            updateRuns()
            notifyDataSetChanged()
        }
    }

    init {
        data = db.allSessions
        db.addChangeListener(changeListener)

        updateRuns()
    }

    private fun updateRuns() {
        runs = getRuns(data map { it.startTime!!.toDateTime().adjustMidnigth() })
                .flatMap {
                    when (it.numEntries) {
                        0 -> emptyList<ListPosition>()
                        1 -> listOf(ListPosition.ALONE)
                        else -> listOf(ListPosition.FIRST, *Array(it.numEntries - 2, { ListPosition.MIDDLE }), ListPosition.LAST)
                    }
                }

        Timber.d("Updated runs: data size: %d, runs size: %d", data.size(), runs.size())
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
            val selectedSessionuuids = multiSelector.selectedPositions.map { data[it].uuid }

            db.deleteSessions(selectedSessionuuids)

            notifyDataSetChanged()

            multiSelector.clearSelections()
            mode?.finish()
            actionMode = null

            return true
        }
    }

    fun updateTitle() {
        val n = multiSelector.selectedPositions.size()
        actionMode?.title = context.resources.getQuantityString(R.plurals.sessions_selected, n, n)
    }

    inner public class ViewHolder(v: View) : SwappingHolder(v, multiSelector) {
        public var title: TextView
        public var subtitle: TextView
        public var run: ImageView

        init {
            title = v.findViewById(R.id.textViewDate) as TextView
            subtitle = v.findViewById(R.id.textViewDuration) as TextView
            run = v.findViewById(R.id.runIndicator) as ImageView

            if (BuildConfig.VERSION_CODE >= 21) {
                selectionModeStateListAnimator = null
            }

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
        val sessionStart = session.getStartDateTime()

        val df = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())

        var startDateString = when {
            sessionStart.adjustMidnigth().isSameDayAs(today().adjustMidnigth()) -> context.getString(R.string.today)
            sessionStart.adjustMidnigth().isSameDayAs(today().minusDays(1).adjustMidnigth()) -> context.getString(R.string.yesterday)
            else -> df.format(session.endTime)
        }
        if (!sessionStart.isSameDayAs(sessionStart.adjustMidnigth())) {
            startDateString += " (+)"
        }

        var startTime = session.getStartDateTime().format("hh:mm")
        var endTime = session.getEndDateTime().format("hh:mm")

        val (h, m) = secondsToHMS(session.getDuration())
        val duration = when {
            h > 0   -> context.getString(R.string.x_h_y_min, h, m)
            else    -> context.getString(R.string.x_min, m)
        }

        val runIndicator = when (runs[position]) {
            ListPosition.ALONE -> R.drawable.run_indicator_only
            ListPosition.FIRST -> R.drawable.run_indicator_top
            ListPosition.MIDDLE -> R.drawable.run_indicator_middle
            ListPosition.LAST -> R.drawable.run_indicator_bottom
        }

        holder.title.text = startDateString
        holder.subtitle.text = "$duration ($startTime\u2013$endTime)"
        holder.run.setImageResource(runIndicator)
    }

    override fun getItemCount() = data.size()
}