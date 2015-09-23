package me.stanislav_nikolov.meditate.ui

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

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
    var buttonMinusTime: android.widget.Button? = null
    var buttonPlusTime: android.widget.Button? = null
    var fabStart: android.support.design.widget.FloatingActionButton? = null
    var textViewTime: android.widget.TextView? = null
    var timerView: android.view.View? = null

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: android.os.Bundle?): android.view.View? {
        val view = inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_sit, container, false)

        buttonMinusTime = view.findViewById(me.stanislav_nikolov.meditate.R.id.buttonMinusTime) as android.widget.Button
        buttonPlusTime = view.findViewById(me.stanislav_nikolov.meditate.R.id.buttonPlusTime) as android.widget.Button
        fabStart = view.findViewById(me.stanislav_nikolov.meditate.R.id.fabStartStop) as android.support.design.widget.FloatingActionButton
        textViewTime = view.findViewById(me.stanislav_nikolov.meditate.R.id.textViewTime) as android.widget.TextView
        timerView = view.findViewById(me.stanislav_nikolov.meditate.R.id.timerView)

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
            val activity = MeditationSessionActivity.newInstance(getActivity(), sessionLengthMinutes * 60, preparationLength)
            val options = android.support.v4.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    getActivity(),
                    android.support.v4.util.Pair<android.view.View, String>(fabStart, getString(me.stanislav_nikolov.meditate.R.string.fabTransition)),
                    android.support.v4.util.Pair<android.view.View, String>(textViewTime, getString(me.stanislav_nikolov.meditate.R.string.timeMinutesTransition)),
                    android.support.v4.util.Pair<android.view.View, String>(timerView, getString(me.stanislav_nikolov.meditate.R.string.timerTransition))
            )
            getActivity().startActivity(activity, options.toBundle())
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
        textViewTime?.setText("$sessionLengthMinutes min")
    }

    companion object {
        public fun newInstance(): SitFragment {
            return SitFragment()
        }
    }
}
