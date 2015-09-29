package me.stanislav_nikolov.meditate.ui

import android.app.Activity
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import hirondelle.date4j.DateTime
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.toDate

public class MainActivity : android.support.v7.app.AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        viewPager = findViewById(R.id.view_pager) as ViewPager
        tabLayout = findViewById(R.id.tab_layout) as TabLayout

        setSupportActionBar(toolbar)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager);

//        loadDb()
    }

    private fun loadDb() {
        var dates = arrayOf(
                DateTime("2010-01-19 23:59:59"),
                DateTime("2010-01-20 23:59:59"),
                DateTime("2010-01-21 23:59:59"),
                DateTime("2010-01-23 23:59:59"),
                DateTime.now(java.util.TimeZone.getDefault())
        )

        val realm = io.realm.Realm.getInstance(application)
        realm.beginTransaction()
        dates.forEach {
            val m = realm.createObject(DbMeditationSession::class.java)
            m.uuid = java.util.UUID.randomUUID().toString()
            m.startTime = it.minus(0, 0, 0, 0, 30, 0, 0, hirondelle.date4j.DateTime.DayOverflow.Spillover).toDate()
            m.endTime = it.toDate()
        }
        realm.commitTransaction()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(me.stanislav_nikolov.meditate.R.menu.menu_main, menu)
        return true
    }

    class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
        data class F(val fragment: Fragment, val title: String)

        val fragments = listOf(
                F(SitFragment.newInstance(), "New Session"),
                F(StatsFragment.newInstance(), "Statistics"),
                F(LogFragment.newInstance(), "Session Log")
        )

        override fun getCount() = fragments.size()

        override fun getItem(position: Int) = fragments[position].fragment

        override fun getPageTitle(position: Int) = fragments[position].title
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            viewPager.postDelayed({ viewPager.currentItem = 1 }, 1000)
        }
    }
}
