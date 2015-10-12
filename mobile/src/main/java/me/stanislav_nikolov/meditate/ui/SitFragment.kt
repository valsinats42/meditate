package me.stanislav_nikolov.meditate.ui

import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.CardView
import android.view.View
import android.widget.Button
import android.widget.TextView
import me.stanislav_nikolov.meditate.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SitFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SitFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class SitFragment : android.support.v4.app.Fragment() {

    private val DEFAULT_SESSION_LENGTH: Long = 10

    var sessionLengthMinutes = DEFAULT_SESSION_LENGTH

    // UI
    var buttonMinusTime: Button? = null
    var buttonPlusTime: Button? = null
    var fabStart: FloatingActionButton? = null
    var textViewTime: TextView? = null
    var timerView: CardView? = null

    @Inject lateinit var realm: Realm

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        graph().inject(this)

        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_sit, container, false)

        buttonMinusTime = view.findViewById(R.id.buttonMinusTime) as Button
        buttonPlusTime = view.findViewById(R.id.buttonPlusTime) as Button
        fabStart = view.findViewById(R.id.fabStartStop) as FloatingActionButton
        textViewTime = view.findViewById(R.id.textViewTime) as TextView
        timerView = view.findViewById(R.id.timerView) as CardView

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
            val overrideLengths = false
            var preparationLength: Long
            var sessionLength: Long

            if (!overrideLengths) {
                preparationLength = 15L
                sessionLength = sessionLengthMinutes * 60
            } else {
                preparationLength = 5L
                sessionLength = 10L
            }

            val activity = MeditationSessionActivity.newInstance(activity, sessionLength, preparationLength)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    Pair.create(timerView as View, getString(R.string.timerTransition))
            )
            getActivity().startActivityForResult(activity, 0, options.toBundle())
        }

        updateUi()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        buttonMinusTime = null
        buttonPlusTime = null
        fabStart = null
        textViewTime = null
        timerView = null
    }

    private fun updateUi() {
        textViewTime?.text = getString(R.string.x_min, sessionLengthMinutes)
    }

    companion object {
        public fun newInstance(): SitFragment = SitFragment()
    }
}
