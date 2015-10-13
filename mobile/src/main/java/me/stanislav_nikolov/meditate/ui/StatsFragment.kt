package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import io.realm.Realm
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adapters.StatsAdapter
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.graph
import javax.inject.Inject

public class StatsFragment : Fragment() {

    @Inject lateinit var realm: Realm

    var recyclerView: RecyclerView? = null

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_stats, container, false)

        graph().inject(this)

        val data = realm.allObjectsSorted(DbMeditationSession::class.java, "endTime", false)
        val adapter = StatsAdapter(context, realm, data)

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
        public fun newInstance(): StatsFragment = StatsFragment()
    }

}
