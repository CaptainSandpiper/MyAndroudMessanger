package com.kulikulad.MessCul.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kulikulad.MessCul.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MeetingsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MeetingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class MeetingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_meetings, container, false);

        var calendarEl = view.findViewById<com.jonaswanke.calendar.CalendarView>(R.id.calendar);



        calendarEl.onEventClickListener = { event ->
            // EventWithPlace was clicked
            Toast.makeText(view.context, "${event.title} clicked", Toast.LENGTH_LONG).show()
        }
        calendarEl.onEventLongClickListener = { event ->
            // EventWithPlace was long clicked
            Toast.makeText(view.context, "${event.title} long clicked", Toast.LENGTH_LONG).show()
        }

        calendarEl.onAddEventListener = { addEvent ->
            // User tried to create a new event. addEvent is an event with populated start and end.
            Toast.makeText(view.context, "Add event at ", Toast.LENGTH_SHORT).show()
            // Return true to remove the placeholder
            true
        }

//        var button = view!!.findViewById<Button>(R.id.butt);
//
//        var textView = view!!.findViewById<TextView>(R.id.textV)
//        button.setOnClickListener{
//
//            textView.text = textV.text.toString() + "LOL";
//        }

        return view;
    }
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_meetings, container, false)
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    fun onButtonPressed(uri: Uri) {
//        //listener?.onFragmentInteraction(uri)
//    }
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            //listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//       // listener = null
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     *
//     *
//     * See the Android Training lesson [Communicating with Other Fragments]
//     * (http://developer.android.com/training/basics/fragments/communicating.html)
//     * for more information.
//     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }
//
//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment MeetingsFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            MeetingsFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
//}
