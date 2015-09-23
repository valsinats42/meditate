package me.stanislav_nikolov.meditate.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

public class StatsFragment : android.support.v4.app.Fragment() {

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        return inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_stats, container, false)
    }

    companion object {
        public fun newInstance(): StatsFragment {
            return StatsFragment()
        }
    }

}
