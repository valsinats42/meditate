package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import me.stanislav_nikolov.meditate.MeditateApp
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adapters.LogAdapter
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import javax.inject.Inject

public class LogFragment : Fragment() {
    var recyclerView: RecyclerView? = null

    @Inject lateinit val realm: Realm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MeditateApp.getGraph().inject(this)

        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_log, container, false)

        val results = realm.allObjectsSorted(DbMeditationSession::class.java, "endTime", false)
        val adapter = LogAdapter(realm, results)

        recyclerView = view!!.findViewById(R.id.recyclerView) as RecyclerView
        with(recyclerView!!) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            setAdapter(adapter)
        }

        return view
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
