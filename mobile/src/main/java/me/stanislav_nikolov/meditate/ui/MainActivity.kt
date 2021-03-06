package me.stanislav_nikolov.meditate.ui

import android.app.Activity
import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import me.stanislav_nikolov.meditate.R

class MainActivity : android.support.v7.app.AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout

    data class TitledFragment(val fragment: Fragment, val title: String)

    class ViewPagerAdapter(fragmentManager: FragmentManager, val fragments: List<TitledFragment>) :
            FragmentPagerAdapter(fragmentManager) {
        override fun getCount() = fragments.size

        override fun getItem(position: Int) = fragments[position].fragment

        override fun getPageTitle(position: Int) = fragments[position].title
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        viewPager = findViewById(R.id.view_pager) as ViewPager
        tabLayout = findViewById(R.id.tab_layout) as TabLayout

        setSupportActionBar(toolbar)

        setupTabs()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            viewPager.postDelayed({ viewPager.currentItem = 1 }, 1000)
        }
    }

    fun setupTabs() {
        val fragments = listOf(
                TitledFragment(SitFragment.newInstance(), getString(R.string.new_session)),
                TitledFragment(StatsFragment.newInstance(), getString(R.string.statistics)),
                TitledFragment(LogFragment.newInstance(), getString(R.string.session_log))
        )

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        tabLayout.setupWithViewPager(viewPager);
    }
}
