package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.CardView
import android.view.View
import android.widget.Button
import android.widget.TextView
import me.stanislav_nikolov.meditate.BuildConfig
import me.stanislav_nikolov.meditate.R
import me.stanislav_nikolov.meditate.db.SessionDb
import me.stanislav_nikolov.meditate.db.getDuration
import me.stanislav_nikolov.meditate.graph
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SitFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class SitFragment : Fragment() {

    var sessionLengthMinutes = 0

    // UI
    var buttonMinusTime: Button? = null
    var buttonPlusTime: Button? = null
    var fabStart: FloatingActionButton? = null
    var textViewTime: TextView? = null
    var timerView: CardView? = null

    @Inject lateinit var db: SessionDb

    companion object {
        public fun newInstance(): SitFragment = SitFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        graph().inject(this)
    }

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater.inflate(R.layout.fragment_sit, container, false)

        bindViews(view)

        bindEvents()

        retrieveLastSessionLength()

        updateUi()

        return view
    }

    private fun bindEvents() {
        with(buttonMinusTime!!) {
            text = getString(R.string.minus_x_min, 5)
            setOnClickListener {
                if (sessionLengthMinutes > 5) sessionLengthMinutes -= 5
                updateUi()
            }
        }

        with(buttonPlusTime!!) {
            text = getString(R.string.plus_x_min, 5)
            setOnClickListener {
                sessionLengthMinutes += 5
                updateUi()
            }
        }

        fabStart!!.setOnClickListener {
            var preparationLength = 15
            var sessionLength = 60 * sessionLengthMinutes

            if (BuildConfig.DEBUG) {
                preparationLength = 5
                sessionLength = 10
            }

            val activity = MeditationSessionActivity.newInstance(activity, sessionLength, preparationLength)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    Pair.create(timerView as View, getString(R.string.timerTransition))
            )
            getActivity().startActivityForResult(activity, 0, options.toBundle())
        }
    }

    private fun bindViews(view: View) {
        buttonMinusTime = view.findViewById(R.id.buttonMinusTime) as Button
        buttonPlusTime = view.findViewById(R.id.buttonPlusTime) as Button
        fabStart = view.findViewById(R.id.fabStartStop) as FloatingActionButton
        textViewTime = view.findViewById(R.id.textViewTime) as TextView
        timerView = view.findViewById(R.id.timerView) as CardView
    }

    override fun onDestroyView() {
        super.onDestroyView()

        buttonMinusTime = null
        buttonPlusTime = null
        fabStart = null
        textViewTime = null
        timerView = null
    }

    private fun retrieveLastSessionLength() {
        val DEFAULT_SESSION_LENGTH = 10

        sessionLengthMinutes = db.allSessions
                .filter { it.getDuration() >= it.initialDurationSeconds }
                .firstOrNull()?.initialDurationSeconds?.div(60) ?:
                DEFAULT_SESSION_LENGTH
    }

    private fun updateUi() {
        textViewTime?.text = getString(R.string.x_min, sessionLengthMinutes)
    }
}
