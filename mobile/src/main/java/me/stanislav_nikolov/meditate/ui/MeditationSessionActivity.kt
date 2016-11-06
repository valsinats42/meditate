package me.stanislav_nikolov.meditate.ui

import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.NotificationCompat
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import hirondelle.date4j.DateTime
import me.stanislav_nikolov.meditate.*
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.SessionDb
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class MeditationSessionActivity : AppCompatActivity() {
    var PREPARATION_TIME = 0
    var SESSION_LENGTH = 0

    var startingSnackBar: Snackbar? = null
    var abortSnackbar: Snackbar? = null
    var meditationStartTime: DateTime? = null
    var preparationStartTime: DateTime? = null

    // UI
    lateinit var minutes: TextView
    lateinit var seconds: TextView
    lateinit var layout: CoordinatorLayout
    lateinit var fabStop: FloatingActionButton

    @Inject lateinit var mediaPlayer: MediaPlayer
    @Inject lateinit var db: SessionDb

    val handler: Handler? = Handler()

    var notificationBuilder: android.support.v4.app.NotificationCompat.Builder? = null

    fun setTimerText(time: Int) {
        val (h, m, s) = secondsToHMS(time)
        minutes.text = getString(R.string.x_min, 60 * h + m)
        seconds.text = getString(R.string.x_s, s)

        if (meditationStartTime != null) {
            notificationBuilder?.setContentText("${minutes.text} ${seconds.text}")
            notificationManager.notify(0, notificationBuilder?.build())
        }
    }

    fun buildNotification() {
        val resultIntent = Intent(this, MeditationSessionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_hourglass_empty_white_24dp)
                .setContentTitle(getString(R.string.meditation_session_in_progress))
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
    }

    fun setStartingSnackBarText(timeRemaining: Int) {
        val text = resources.getQuantityString(R.plurals.starting_session, timeRemaining, timeRemaining)
        startingSnackBar?.setText(text)
    }

    var updateUiRunnable: Runnable? = null

    fun setupUpdateUiRunnable() {
        updateUiRunnable = Runnable {
            // Reschedule every second
            handler?.postDelayed(updateUiRunnable, 1 * DateUtils.SECOND_IN_MILLIS)
            Timber.d("Updating UI!")

            val preparationTime = preparationStartTime?.numSecondsFrom(now())?.toInt()
            val meditationTime = meditationStartTime?.numSecondsFrom(now())?.toInt()

            when {
                preparationTime != null && preparationTime < PREPARATION_TIME -> {
                    if (startingSnackBar == null) {
                        startingSnackBar = Snackbar.make(layout, "", Snackbar.LENGTH_INDEFINITE)
                        startingSnackBar!!.show()
                    }

                    setStartingSnackBarText(PREPARATION_TIME - preparationTime)
                }

                preparationTime != null && preparationTime >= PREPARATION_TIME -> {
                    startingSnackBar?.dismiss()

                    preparationStartTime = null

                    mediaPlayer.start()

                    meditationStartTime = now()

                    setupTimer()
                }

                meditationTime != null -> {
                    fabStop.visibility = View.VISIBLE
                    seconds.visibility = View.VISIBLE

                    when {
                        meditationTime < SESSION_LENGTH -> {
                            setTimerText(SESSION_LENGTH - meditationTime)
                        }

                        meditationTime >= SESSION_LENGTH -> {
                            fabStop.setImageResource(R.drawable.ic_check_white_24dp)
                            setTimerText(meditationTime)
                        }
                    }

                }
            }
        }
    }

    class AlarmReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, p1: Intent?) {
            Timber.i("onReceive!!!")
            context!!.graph().mediaPlayer().start()
        }
    }

    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var alarmMgr: AlarmManager

    var alarmIntent: PendingIntent? = null

    private fun setupTimer() {
        val intent = Intent(this, AlarmReceiver::class.java)
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        val meditationTime = meditationStartTime!!.numSecondsFrom(now()).toInt()
        if (meditationTime <= SESSION_LENGTH) {
            val wakeupTime = SystemClock.elapsedRealtime() +
                    (SESSION_LENGTH - meditationTime) * DateUtils.SECOND_IN_MILLIS
            if (BuildConfig.VERSION_CODE >= 23) {
                alarmMgr.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, wakeupTime, alarmIntent)
            } else {
                alarmMgr.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, wakeupTime, alarmIntent)
            }
        }
    }

    val ARG_SAVE_MEDITATION_START_TIME = "meditation_start_time"
    val ARG_SAVE_PREPARATION_START_TIME = "preparation_start_time"

    companion object {
        val ARG_PREPARATION_TIME = "timerLength"
        val ARG_SESSION_LENGTH = "warmUpPeriod"

        fun newInstance(context: Context, timerLength: Int, warmUpPeriod: Int): Intent {
            val result = Intent(context, MeditationSessionActivity::class.java)
            result.putExtra(ARG_PREPARATION_TIME, timerLength)
            result.putExtra(ARG_SESSION_LENGTH, warmUpPeriod)
            return result
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable(ARG_SAVE_PREPARATION_START_TIME, preparationStartTime)
        outState?.putSerializable(ARG_SAVE_MEDITATION_START_TIME, meditationStartTime)
    }

    private fun loadExtras(state: Bundle?) {
        if (intent.extras != null) {
            val extras = intent.extras
            PREPARATION_TIME = extras.getInt(ARG_SESSION_LENGTH)
            SESSION_LENGTH = extras.getInt(ARG_PREPARATION_TIME)
        }

        if (state != null) {
            preparationStartTime = state.getSerializable(ARG_SAVE_PREPARATION_START_TIME) as DateTime?
            meditationStartTime = state.getSerializable(ARG_SAVE_MEDITATION_START_TIME) as DateTime?
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_session)

        graph().inject(this)

        loadExtras(savedInstanceState)
        bindViews()
        bindEvents()

        buildNotification()

        setupUpdateUiRunnable()

        if (meditationStartTime == null) {
            setTimerText(SESSION_LENGTH)

            if (preparationStartTime == null) {
                preparationStartTime = now()
            }
        } else {
            setupTimer()
        }

        handler!!.postDelayed(updateUiRunnable, 200)
    }

    override fun onDestroy() {
        super.onDestroy()

        handler!!.removeCallbacksAndMessages(null)
        notificationManager.cancel(0)
        alarmMgr.cancel(alarmIntent)
    }

    private fun bindEvents() {
        fabStop.setOnClickListener { onBackPressed() }
    }

    private fun bindViews() {
        minutes = findViewById(R.id.textViewMinutes) as TextView
        seconds = findViewById(R.id.textViewSeconds) as TextView
        layout = findViewById(R.id.layout) as CoordinatorLayout
        fabStop = findViewById(R.id.fabStartStop) as FloatingActionButton
    }

    private fun showAbortSnackbar() {
        abortSnackbar = Snackbar.make(layout, R.string.abort_incomplete_session, Snackbar.LENGTH_SHORT)
        with(abortSnackbar!!) {
            setAction(R.string.abort, { saveAndFinish() })

            setCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar?, event: Int) {
                    abortSnackbar = null
                }
            })

            show()
        }
    }

    fun finish(result: Int) {
        setResult(result)
        supportFinishAfterTransition()
    }

    fun saveAndFinish() {
        saveSession()
        finish(Activity.RESULT_OK)
    }

    override fun onBackPressed() {
        val meditationTime = meditationStartTime?.numSecondsFrom(now())?.toInt()
        when {
            meditationTime == null ->
                finish(Activity.RESULT_CANCELED)

            meditationTime < SESSION_LENGTH && abortSnackbar == null ->
                showAbortSnackbar()

            else ->
                saveAndFinish()
        }
    }

    private fun saveSession() {
        val session = DbMeditationSession()
        session.uuid = UUID.randomUUID().toString()
        session.initialDurationSeconds = SESSION_LENGTH
        session.startTime = meditationStartTime!!.toDate()
        session.endTime = now().toDate()

        db.saveSession(session)
    }
}
