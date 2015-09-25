package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_sit, container, false)

        buttonMinusTime = view.findViewById(R.id.buttonMinusTime) as Button
        buttonPlusTime = view.findViewById(R.id.buttonPlusTime) as Button
        fabStart = view.findViewById(R.id.fabStartStop) as FloatingActionButton
        textViewTime = view.findViewById(R.id.textViewTime) as TextView
        timerView = view.findViewById(R.id.timerView) as CardView

        buttonMinusTime!!.setOnClickListener {
            if (sessionLengthMinutes > 5) sessionLengthMinutes -= 5
            updateUi()
        }

        buttonPlusTime!!.setOnClickListener {
            sessionLengthMinutes += 5
            updateUi()
        }

        fabStart!!.setOnClickListener {
            val preparationLength: Long = 5
            val activity = MeditationSessionActivity.newInstance(activity, sessionLengthMinutes * 60, preparationLength)
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    android.support.v4.util.Pair<View, String>(timerView, getString(R.string.timerTransition)),
                    android.support.v4.util.Pair<View, String>(textViewTime, getString(R.string.timeMinutesTransition))
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
        textViewTime?.text = "$sessionLengthMinutes min"
    }

    companion object {
        public fun newInstance(): SitFragment = SitFragment()
    }
}
