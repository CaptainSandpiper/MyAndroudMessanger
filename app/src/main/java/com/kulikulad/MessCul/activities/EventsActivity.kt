package com.kulikulad.MessCul.activities

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.jonaswanke.calendar.BaseEvent
import com.jonaswanke.calendar.CalendarView
import com.jonaswanke.calendar.Event
import com.jonaswanke.calendar.utils.Day
import com.jonaswanke.calendar.utils.Week
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.EventInfo
import kotlinx.android.synthetic.main.activity_events.*
import java.util.*

class EventsActivity : AppCompatActivity() {

    private var nextId: Long = 0
    private val random = Random();
    var mFirebaseDatabaseRef: DatabaseReference? = null;
    var mFirebaseUser: FirebaseUser? = null;

    var meetingList: MutableList<EventInfo>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

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
                Toast.makeText(this@EventsActivity, "AAAA",Toast.LENGTH_LONG).show()
            }

            override fun onCancelled(p0: DatabaseError)
            {

            }
        })


        Toast.makeText(this,"OnCreate",Toast.LENGTH_LONG).show();

//        //populate( meetingList!!)
//        calendar.eventRequestCallback = {
//
//            fun selector(WOY: EventInfo): Int = WOY.weekOfYear!!.toInt();
//
//            meetingList!!.sortBy({selector(it)})
//
//            populate( meetingList!!)
//            //populate(it)
//            Toast.makeText(this, "${it.week}",Toast.LENGTH_LONG).show()
//        }





        calendar.onEventClickListener = { event ->
            // EventWithPlace was clicked
            Toast.makeText(this, "${event.start} clicked", Toast.LENGTH_LONG).show()

            Log.d("start:", event.start.toString());
            Log.d("end:", event.end.toString());
        }
        calendar.onEventLongClickListener = { event ->
            // EventWithPlace was long clicked
            Toast.makeText(this, "${event.title} long clicked", Toast.LENGTH_LONG).show()
        }

        calendar.onAddEventListener = { addEvent ->
            // User tried to create a new event. addEvent is an event with populated start and end.
            Toast.makeText(this, "Add event at ", Toast.LENGTH_SHORT).show()
            // Return true to remove the placeholder
            true
        }

    }

    override fun onResume() {
        super.onResume()

        Toast.makeText(this,"OnResume",Toast.LENGTH_LONG).show();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.events_menu, menu);

        return true;
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        if(item != null)
        {
            if(item.itemId == R.id.dayView)
            {
                //var cal = findViewById<com.jonaswanke.calendar.CalendarView>(R.id.calendar)
                calendar.range = CalendarView.RANGE_DAY;
                Toast.makeText(this, "DAY", Toast.LENGTH_LONG).show()
            }

            if(item.itemId == R.id.weekView)
            {
                calendar.range = CalendarView.RANGE_WEEK;
                Toast.makeText(this, "WEEK", Toast.LENGTH_LONG).show()
            }

            if(item.itemId == R.id.main_action_today)
            {
                calendar.range = 1
                calendar.visibleStart = Day()
                Toast.makeText(this, "TODAAY", Toast.LENGTH_LONG).show()
            }

        }

        return true;
    }


    ///////
//    private fun populate(week: Week, force: Boolean = false) {
//        if (!force && calendar.cachedEvents.contains(week))
//            return
//
//        var year = 2019;
//        var month = 4;
//        var day = 24;
//        var hour =19;
//        var minutes = 33;
//
//        val selectedTime = Calendar.getInstance();
//        selectedTime.set(Calendar.YEAR,year);
//        selectedTime.set(Calendar.MONTH, month);
//        selectedTime.set(Calendar.DAY_OF_MONTH, day)
//        selectedTime.set(Calendar.HOUR_OF_DAY, hour);
//        selectedTime.set(Calendar.MINUTE, minutes);
//
//        var newWeek = Week(selectedTime);
//        var dayS = Day(selectedTime);
//
//
//
//
//
//        val events = mutableListOf<Event>()
//        val id = nextId++.toString()
//        val start = newWeek.start + 5*24*60*60*1000 + 19*60*60*1000  // DateUtils.WEEK_IN_MILLIS
//        val end = start + 60*60*1000 //% (DateUtils.DAY_IN_MILLIS / 8)
//
//        events.add(BaseEvent(id, id, (random.nextInt() or 0xFF000000.toInt()) and 0x00202020.inv(),start, end ))
//
//        Log.d("AAAA", newWeek.start.toString())
//        Log.d("Start", start.toString())
//        Log.d("End",end.toString())
//
//
////        for (i in 0..15) {
////            val id = nextId++.toString()
////            val start = newWeek.start + 2 % DateUtils.WEEK_IN_MILLIS
////            events.add(
////                BaseEvent(
////                    id,
////                    id,
////                    (random.nextInt() or 0xFF000000.toInt()) and 0x00202020.inv(),
////                    start,
////                    start + abs(random.nextLong()) % (DateUtils.DAY_IN_MILLIS / 8))
////            )
////        }
//
//        calendar.setEventsForWeek(newWeek, events)
//    }


    fun populate(meetingsInfoList:MutableList<EventInfo>) {

        var oldWeek:Week? = null;
        var newWeek:Week? = null;
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

            val id = nextId++.toString()

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

//    private fun populate(week: Week, force: Boolean = false) {
//        if (!force && calendar.cachedEvents.contains(week))
//            return
//
//        val events = mutableListOf<Event>()
//        for (i in 0..2) {
//            val id = nextId++.toString()
//            val start = week.start + abs(random.nextLong()) % DateUtils.WEEK_IN_MILLIS
//            events.add(BaseEvent(
//                id,
//                id,
//                (random.nextInt() or 0xFF000000.toInt()) and 0x00202020.inv(),
//                start,
//                start + abs(random.nextLong()) % (DateUtils.DAY_IN_MILLIS / 8)))
//        }
//        calendar.setEventsForWeek(week, events)
//    }


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





}
