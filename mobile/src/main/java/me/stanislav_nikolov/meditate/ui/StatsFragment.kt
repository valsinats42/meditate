package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import io.realm.Realm
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adapters.StatsAdapter
import me.stanislav_nikolov.meditate.MeditateApp
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import javax.inject.Inject
import kotlin.properties.Delegates

public class StatsFragment : Fragment() {

    var recyclerView: RecyclerView by Delegates.notNull()

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        return inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_stats, container, false)
    }

    @Inject lateinit val realm: Realm

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MeditateApp.getGraph().inject(this)

        val data = realm.allObjectsSorted(DbMeditationSession::class.java, "endTime", false)
        val adapter = StatsAdapter(context, realm, data)

        recyclerView = view!!.findViewById(R.id.recyclerView) as RecyclerView
        with(recyclerView) {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            setAdapter(adapter)
        }
    }

    companion object {
        public fun newInstance(): StatsFragment = StatsFragment()
    }

}
