package me.stanislav_nikolov.meditate.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import java.lang.ref.WeakReference
import kotlin.properties.Delegates

public class MeditationSessionActivity : android.support.v7.app.AppCompatActivity() {
    var preparationTime: Long = 0
    var sessionLength: Long = 0

    var snackbar: android.support.design.widget.Snackbar? = null
    var preparationTimer: PreparationTimer? = null

    class PreparationTimer(val activity: java.lang.ref.WeakReference<MeditationSessionActivity>, val time: Long):
            android.os.CountDownTimer(time * android.text.format.DateUtils.SECOND_IN_MILLIS, 1 * android.text.format.DateUtils.SECOND_IN_MILLIS) {

        override fun onFinish() {
            activity.get()?.snackbar?.dismiss()
            activity.get()?.onPreparationTimerFinished()
        }

        override fun onTick(millisUntilFinished: Long) {
            activity.get()?.snackbar?.setText("Starting session in ${millisUntilFinished/1000}s...")
        }
    }

    class MeditaionTimer(val minutesText: android.widget.TextView, val secondsText: android.widget.TextView, time: Long):
            android.os.CountDownTimer(time * android.text.format.DateUtils.SECOND_IN_MILLIS, 1 * android.text.format.DateUtils.SECOND_IN_MILLIS) {

        override fun onFinish() {
            // change FAB to a tick
            // play a sound
        }

        override fun onTick(millisUntilFinished: Long) {
            val (h, m, s) = me.stanislav_nikolov.meditate.secondsToHMS(millisUntilFinished / 1000)
            minutesText.setText("${60 * h + m} min")
            secondsText.setText("$s s")
        }
    }

    var minutes: android.widget.TextView by kotlin.properties.Delegates.notNull()
    var seconds: android.widget.TextView by kotlin.properties.Delegates.notNull()
    var layout: android.view.View by kotlin.properties.Delegates.notNull()

    private fun onPreparationTimerFinished() {
        preparationTimer = null
        seconds.setVisibility(android.view.View.VISIBLE)

        MeditaionTimer(minutes, seconds, sessionLength).start()
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(me.stanislav_nikolov.meditate.R.layout.activity_meditation_session)

        minutes = findViewById(me.stanislav_nikolov.meditate.R.id.textViewMinutes) as android.widget.TextView
        seconds = findViewById(me.stanislav_nikolov.meditate.R.id.textViewSeconds) as android.widget.TextView
        layout = findViewById(me.stanislav_nikolov.meditate.R.id.layout)

        val extras = getIntent().getExtras()
        preparationTime = extras.getLong(ARG_WARM_UP_PERIOD)
        sessionLength = extras.getLong(ARG_TIMER_LENGTH)

        minutes.setText("${sessionLength/60} min")
        seconds.setVisibility(android.view.View.GONE)

//        fabStartStop.setOnClickListener { supportFinishAfterTransition() }

        snackbar = android.support.design.widget.Snackbar.make(layout, "Starting session in $preparationTime seconds...", android.support.design.widget.Snackbar.LENGTH_INDEFINITE)
        snackbar?.show()

        preparationTimer = PreparationTimer(java.lang.ref.WeakReference(this), preparationTime)
        preparationTimer?.start()

        updateUi()
    }

    private fun updateUi() {
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(me.stanislav_nikolov.meditate.R.menu.menu_meditation_session, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()

        //noinspection SimplifiableIfStatement
        if (id == me.stanislav_nikolov.meditate.R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        val ARG_TIMER_LENGTH = "timerLength"
        val ARG_WARM_UP_PERIOD = "warmUpPeriod"

        fun newInstance(context: android.content.Context, timerLength: Long, warmUpPeriod: Long): android.content.Intent {
            val result = android.content.Intent(context, javaClass<MeditationSessionActivity>())
            result.putExtra(ARG_TIMER_LENGTH, timerLength)
            result.putExtra(ARG_WARM_UP_PERIOD, warmUpPeriod)
            return result
        }
    }
}
