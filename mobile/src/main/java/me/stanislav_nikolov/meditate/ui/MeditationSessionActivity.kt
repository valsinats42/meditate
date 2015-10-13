package me.stanislav_nikolov.meditate.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.SoundPool
import android.os.CountDownTimer
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import hirondelle.date4j.DateTime
import io.realm.Realm
import me.stanislav_nikolov.meditate.*
import me.stanislav_nikolov.meditate.db.DbMeditationSession
import me.stanislav_nikolov.meditate.db.SessionDb
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

public class MeditationSessionActivity : AppCompatActivity() {
    var preparationTime = 0L
    var sessionLength = 0L

    var snackbar: Snackbar? = null
    var preparationTimer: PreparationTimer? = null
    var meditationTimer: MeditationTimer? = null
    var startTime: DateTime? = null
    var originalSessionLength: Long by Delegates.notNull()

    // UI
    lateinit var minutes: TextView
    lateinit var seconds: TextView
    lateinit var layout: CoordinatorLayout
    lateinit var fabStop: FloatingActionButton

    @Inject lateinit var soundPool: SoundPool
    @Inject lateinit var db: SessionDb

    companion object {
        val ARG_TIMER_LENGTH = "timerLength"
        val ARG_WARM_UP_PERIOD = "warmUpPeriod"

        fun newInstance(context: Context, timerLength: Long, warmUpPeriod: Long): Intent {
            val result = Intent(context, MeditationSessionActivity::class.java)
            result.putExtra(ARG_TIMER_LENGTH, timerLength)
            result.putExtra(ARG_WARM_UP_PERIOD, warmUpPeriod)
            return result
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_session)

        graph().inject(this)

        loadExtras()
        bindViews()
        bindEvents()

        minutes.text = getString(R.string.x_min, sessionLength / 60)

        preparationTimer = PreparationTimer(preparationTime)
        preparationTimer?.start()
    }

    private fun bindEvents() {
        fabStop.setOnClickListener { onBackPressed() }
    }

    private fun showAbortSnackbar() {
        snackbar = Snackbar.make(layout, R.string.abort_incomplete_session, Snackbar.LENGTH_SHORT)
        with(snackbar!!) {
            setAction(R.string.abort, {
                saveSession()
                supportFinishAfterTransition()
            })
            show()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)

        when {
            preparationTimer != null -> { setResult(Activity.RESULT_CANCELED); supportFinishAfterTransition() }
            meditationTimer != null -> showAbortSnackbar()
            meditationTimer == null -> { saveSession(); supportFinishAfterTransition() }
            else -> supportFinishAfterTransition()
        }
    }

    private fun saveSession() {
        val session = DbMeditationSession()
        session.uuid = UUID.randomUUID().toString()
        session.initialDurationSeconds = originalSessionLength.toInt()
        session.startTime = startTime!!.toDate()
        session.endTime = DateTime.now(TimeZone.getDefault()).toDate()

        db.saveSession(session)
    }

    private fun showStartingSnackBar() {
        val text = resources.getQuantityString(R.plurals.starting_session, preparationTime.toInt(), preparationTime)
        snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_INDEFINITE)
        snackbar?.let {
            it.setCallback(object : Snackbar.Callback() {
                override fun onDismissed(snackbar: Snackbar?, event: Int) {
                    fabStop.visibility = View.VISIBLE
                }
            })
            layout.postDelayed({ it.show() }, 200)
        }
    }

    private fun bindViews() {
        minutes = findViewById(R.id.textViewMinutes) as TextView
        seconds = findViewById(R.id.textViewSeconds) as TextView
        layout = findViewById(R.id.layout) as CoordinatorLayout
        fabStop = findViewById(R.id.fabStartStop) as FloatingActionButton
    }

    private fun loadExtras() {
        val extras = intent.extras
        preparationTime = extras.getLong(ARG_WARM_UP_PERIOD)
        sessionLength = extras.getLong(ARG_TIMER_LENGTH)
        originalSessionLength = sessionLength
    }

    override fun onDestroy() {
        super.onDestroy()

        snackbar?.dismiss()
        preparationTimer?.cancel()
        meditationTimer?.cancel()
    }

    fun playTone() {
        soundPool.load(this, R.raw.reception_bell, 1)
        soundPool.setOnLoadCompleteListener({ soundPool, sampleId, status ->
            soundPool.play(sampleId, 1.0f, 1.0f, 0, 0, 1.0f)
        })
    }

    inner class PreparationTimer(time: Long) {
        val timer = object : CountDownTimer(time * DateUtils.SECOND_IN_MILLIS, DateUtils.SECOND_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val s = millisUntilFinished.toInt() / 1000
                val text = resources.getQuantityString(R.plurals.starting_session, s, s)
                snackbar?.setText(text)
            }

            override fun onFinish() {
                snackbar?.dismiss()
                preparationTimer = null
                seconds.visibility = android.view.View.VISIBLE

                startTime = DateTime.now(TimeZone.getDefault())
                meditationTimer = MeditationTimer(sessionLength)
                meditationTimer?.start()
            }
        }

        fun start() {
            showStartingSnackBar()
            timer.start()
        }

        fun cancel() = timer.cancel()
    }

    inner class MeditationTimer(time: Long) {
        val timer = object : CountDownTimer(time * DateUtils.SECOND_IN_MILLIS, DateUtils.SECOND_IN_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val (h, m, s) = secondsToHMS(millisUntilFinished / 1000)
                minutes.text = getString(R.string.x_min, 60 * h + m);
                seconds.text = getString(R.string.x_s, s)
            }

            override fun onFinish() {
                meditationTimer = null

                fabStop.setImageResource(R.drawable.ic_check_white_24dp)
                playTone()

                AfterMeditationTimer(originalSessionLength).start()
            }
        }

        init { playTone() }

        fun start() = timer.start()
        fun cancel() = timer.cancel()
    }

    inner class AfterMeditationTimer(var currentTime: Long) {
        val timer = Timer()

        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                currentTime++
                val (h, m, s) = secondsToHMS(currentTime)

                runOnUiThread {
                    minutes.text = getString(R.string.x_min, 60 * h + m);
                    seconds.text = getString(R.string.x_s, s)
                }
            }
        }


        fun start() = timer.scheduleAtFixedRate(timerTask, 0, 1 * DateUtils.SECOND_IN_MILLIS)
    }
}
