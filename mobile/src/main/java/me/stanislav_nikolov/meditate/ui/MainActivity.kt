package me.stanislav_nikolov.meditate.ui

import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import hirondelle.date4j.DateTime
import io.realm.Realm
import me.leolin.shortcutbadger.ShortcutBadger
import me.stanislav_nikolov.meditate.toDate
import java.util.*

public class MainActivity : android.support.v7.app.AppCompatActivity() {

    var toolbar: android.support.v7.widget.Toolbar? = null
    var viewPager: android.support.v4.view.ViewPager? = null
    var tabLayout: android.support.design.widget.TabLayout? = null

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)

        setContentView(me.stanislav_nikolov.meditate.R.layout.activity_main)

        toolbar = findViewById(me.stanislav_nikolov.meditate.R.id.toolbar) as android.support.v7.widget.Toolbar
        viewPager = findViewById(me.stanislav_nikolov.meditate.R.id.view_pager) as android.support.v4.view.ViewPager
        tabLayout = findViewById(me.stanislav_nikolov.meditate.R.id.tab_layout) as android.support.design.widget.TabLayout

        setSupportActionBar(toolbar)
        viewPager!!.setAdapter(ViewPagerAdapter(getSupportFragmentManager()))
        tabLayout!!.setupWithViewPager(viewPager);

        me.leolin.shortcutbadger.ShortcutBadger.setBadge(this, 12);

        // loadDb()
    }

    private fun loadDb() {
        var dates = arrayOf(
                hirondelle.date4j.DateTime("2010-01-19 23:59:59"),
                hirondelle.date4j.DateTime("2010-01-20 23:59:59"),
                hirondelle.date4j.DateTime("2010-01-21 23:59:59"),
                hirondelle.date4j.DateTime("2010-01-23 23:59:59"),
                hirondelle.date4j.DateTime.now(java.util.TimeZone.getDefault())
        )

        val realm = io.realm.Realm.getInstance(getApplication())
        realm.beginTransaction()
        dates.forEach {
            val m = realm.createObject(javaClass<me.stanislav_nikolov.meditate.DbMeditationSession>())
            m.setUuid(java.util.UUID.randomUUID().toString())
            m.setStartTime(it.minus(0, 0, 0, 0, 30, 0, 0, hirondelle.date4j.DateTime.DayOverflow.Spillover).toDate())
            m.setEndTime(it.toDate())
        }
        realm.commitTransaction()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(me.stanislav_nikolov.meditate.R.menu.menu_main, menu)
        return true
    }

    fun playTone() {
        val uri = android.net.Uri.parse("android.resource://%s/%d".format(getPackageName(), me.stanislav_nikolov.meditate.R.raw.reception_bell))
        android.media.RingtoneManager.getRingtone(this, uri).play()
    }

    class ViewPagerAdapter(fragmentManager: android.support.v4.app.FragmentManager) : android.support.v4.app.FragmentPagerAdapter(fragmentManager) {
        data class FragmentWithTitle(val fragment: android.support.v4.app.Fragment, val title: String)

        val fragments = listOf(
                FragmentWithTitle(me.stanislav_nikolov.meditate.SitFragment.Companion.newInstance(), "New Session"),
                FragmentWithTitle(me.stanislav_nikolov.meditate.StatsFragment.Companion.newInstance(), "Statistics"),
                FragmentWithTitle(LogFragment.newInstance(), "Session Log")
        )

        override fun getCount(): Int {
            return fragments.size()
        }

        override fun getItem(position: Int): android.support.v4.app.Fragment? {
            return fragments[position].fragment
        }

        override fun getPageTitle(position: Int): CharSequence {
            return fragments[position].title
        }
    }
}
