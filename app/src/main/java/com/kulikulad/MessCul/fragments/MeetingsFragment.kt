package com.kulikulad.MessCul.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jonaswanke.calendar.BaseEvent
import com.jonaswanke.calendar.Event
import com.jonaswanke.calendar.utils.Week
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.EventInfo
import kotlinx.android.synthetic.main.activity_events.*
import java.util.*

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

    var mFirebaseDatabaseRef: DatabaseReference? = null;
    var mFirebaseUser: FirebaseUser? = null;

    var meetingList: MutableList<EventInfo>? = null;
    private val random = Random();

    //private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view = inflater!!.inflate(R.layout.fragment_meetings, container, false);

        var calendarEl = view.findViewById<com.jonaswanke.calendar.CalendarView>(R.id.calendar);

        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference;
        mFirebaseUser = FirebaseAuth.getInstance().currentUser;
        meetingList = mutableListOf();

        mFirebaseDatabaseRef!!.child("Users").child(mFirebaseUser!!.uid).child("Meetings")
            .addValueEventListener(object:
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot)
                {
                    loadMeetings(dataSnapshot);
                    //populate(meetingList!!);
                   // Toast.makeText(this@EventsActivity, "AAAA",Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(p0: DatabaseError)
                {

                }
            })



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

        return view;
    }

    fun loadMeetings(dataSnapshot:DataSnapshot)
    {
        val meetings = dataSnapshot.children.iterator();

        while(meetings.hasNext())
        {
            val itemlist = meetings.next();
            val map = itemlist.getValue() as HashMap<String, Any?>;
            var year = map.get("meetingYear") as String;
            var month = map.get("meetingMonth") as String;
            var day = map.get("meetingDay")as String;
            var hour = map.get("meetingHour")as String;
            var minute = map.get("meetingMinute")as String;
            var meetLat = map.get("meetingLat");
            var meetLng = map.get("meetingLng");
            var dayOfWeek = map.get("meetingDayOfWeek")as String;
            var weeOfYear = map.get("meetingWeekOfYear") as String;
            var meetingSubj = map.get("subjectMeet") as String;
            var meetingDescr = map.get("descriptionMeet") as String;

            var meetingInfo = EventInfo(year, month, day,hour, minute, meetLat.toString().toDouble(), meetLng.toString().toDouble(), dayOfWeek, weeOfYear, meetingSubj, meetingDescr)

            meetingList!!.add(meetingInfo);

        }

        populate(meetingList!!);
    }

    fun populate(meetingsInfoList:MutableList<EventInfo>) {

        var oldWeek: Week? = null;
        var newWeek: Week? = null;
        var times: Int = 0;
        var count = meetingsInfoList.count();
        var events = mutableListOf<Event>()


        fun selector(WOY: EventInfo): Int = WOY.weekOfYear!!.toInt();

        meetingsInfoList.sortBy({selector(it)})

        for(info in meetingsInfoList)
        {

            var year = info.year!!.toInt();
            var month = info.month!!.toInt();
            var day = info.day!!.toInt();
            var hour =info.hour!!.toInt();
            var minutes =info.minute!!.toInt();
            var dayOfWeek = info.dayOfWeek!!.toInt();
            var weekOfYear = info.weekOfYear!!.toInt();
            var meetingSubj = info.meetingSubj;
            var meetingDescr = info.meetingDesr;

            val selectedTime = Calendar.getInstance();
            selectedTime.set(Calendar.YEAR,year);
            selectedTime.set(Calendar.MONTH, month);
            selectedTime.set(Calendar.DAY_OF_MONTH, day)
            selectedTime.set(Calendar.HOUR_OF_DAY, hour);
            selectedTime.set(Calendar.MINUTE, minutes);
            selectedTime.set(Calendar.WEEK_OF_YEAR, weekOfYear)

            //val id = nextId++.toString()

            if(times == 0) {

                newWeek = Week(selectedTime);
                oldWeek = newWeek;
            }
            else
            {
                if(oldWeek!!.week != weekOfYear)
                {
                    calendar.setEventsForWeek(newWeek!!, events)
                    events.clear();
                    newWeek = Week(selectedTime)
                    oldWeek = newWeek;

                }
            }

            var start:Long? = null;
            var end: Long? = null;
            start = newWeek!!.start + (dayOfWeek - 1) * 24 * 60 * 60 * 1000 + hour * 60 * 60 * 1000 + minutes * 60 * 1000 // DateUtils.WEEK_IN_MILLIS
            end = start + 60 * 60 * 1000 //% (DateUtils.DAY_IN_MILLIS / 8)
            events.add(BaseEvent(meetingSubj!!, meetingDescr, (random.nextInt() or 0xFF000000.toInt()) and 0x00202020.inv(),start!!, end!! ))

            if(times == count-1)
            {
                calendar.setEventsForWeek(newWeek!!, events)
            }
            times++;
        }
    }


}


