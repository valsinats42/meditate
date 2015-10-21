package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adapters.StatsAdapter
import me.stanislav_nikolov.meditate.db.SessionDb
import me.stanislav_nikolov.meditate.graph
import javax.inject.Inject

public class StatsFragment : Fragment() {

    @Inject lateinit var db: SessionDb

    var recyclerView: RecyclerView? = null
    lateinit var adapter: StatsAdapter

    companion object {
        public fun newInstance(): StatsFragment = StatsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        graph().inject(this)

        adapter = StatsAdapter(context, db)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

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
}
