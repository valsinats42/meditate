package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.adapters.LogAdapter
import me.stanislav_nikolov.meditate.db.SessionDb
import me.stanislav_nikolov.meditate.graph
import javax.inject.Inject

public class LogFragment : Fragment() {
    var recyclerView: RecyclerView? = null

    @Inject lateinit var db: SessionDb

    companion object {
        public fun newInstance(): LogFragment = LogFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        graph().inject(this)

        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_log, container, false)

        val adapter = LogAdapter(context, db)

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
