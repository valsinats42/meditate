package me.stanislav_nikolov.meditate.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import me.stanislav_nikolov.meditate.R

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LogFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
public class LogFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    val recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1)
            mParam2 = getArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(me.stanislav_nikolov.meditate.R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View?, Nullable savedInstanceState: Bundle?) {
        val r = Realm.getInstance(getActivity())

        r.beginTransaction()

//        val session = r.createObject(javaClass<MeditationSession>())
//        session.uuid = "${UUID.randomUUID()}"
//        session.startDate = Date()
//        session.endDate = Date(session.startDate!!.getTime() + 30 * DateUtils.MINUTE_IN_MILLIS)


//        val q = r.allObjectsSorted(javaClass<MeditationSession>(), "endDate", false)
//        val adapter = MyAdapter(q.map { "${it.startDate} -- ${it.duration()}" }.toTypedArray())

        r.commitTransaction()

//        recyclerView.setLayoutManager(LinearLayoutManager(getActivity()))
//        recyclerView.setHasFixedSize(true)
//        recyclerView.setAdapter(adapter)
    }

    // TODO: Rename method, update argument and hook method into UI event
    public fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
//            throw ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment LogFragment.
         */
        // TODO: Rename and change types and number of parameters
        public fun newInstance(): LogFragment {
            val fragment = LogFragment()
            val args = Bundle()
            fragment.setArguments(args)
            return fragment
        }
    }

}// Required empty public constructor
