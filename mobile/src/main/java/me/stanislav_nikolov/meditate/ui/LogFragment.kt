package me.stanislav_nikolov.meditate.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.RealmResults
import me.stanislav_nikolov.meditate.MeditateApp
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adapters.LogAdapter
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.getEndDateTime
import me.stanislav_nikolov.meditate.db.getStartDateTime
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

public class LogFragment : Fragment() {
    var recyclerView: RecyclerView? = null

    @Inject lateinit var realm: Realm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity.application as MeditateApp).graph.inject(this)

        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_log, container, false)

        val results = realm.allObjectsSorted(DbMeditationSession::class.java, "endTime", false)
        val adapter = LogAdapter(context, realm, results)

//        exportData(results)

        recyclerView = view!!.findViewById(R.id.recyclerView) as RecyclerView
        with(recyclerView!!) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            setAdapter(adapter)
        }

        return view
    }

    private fun exportData(results: RealmResults<DbMeditationSession>) {
        val dateFormat = "YYYY-MM-DD hh:mm:ss"
        val root = JSONObject()

        val list = JSONArray()

        for (r in results) {
            val session = JSONObject()
            session.put("uuid", r.uuid)
            session.put("start", r.getStartDateTime().format(dateFormat, Locale.getDefault()))
            session.put("end", r.getEndDateTime().format(dateFormat, Locale.getDefault()))
            list.put(session)
        }

        root.put("sessions", list)

        val sharedData = root.toString()

        val sendIntent = Intent()
        sendIntent.setAction(Intent.ACTION_SEND)
        sendIntent.putExtra(Intent.EXTRA_TEXT, sharedData)
        sendIntent.setType("application/json")
        startActivity(sendIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerView = null
    }

    companion object {
        public fun newInstance(): LogFragment {
            return LogFragment()
        }
    }
}
