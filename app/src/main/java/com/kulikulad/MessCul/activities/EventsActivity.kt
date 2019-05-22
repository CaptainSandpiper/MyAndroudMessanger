package com.kulikulad.MessCul.activities

import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jonaswanke.calendar.BaseEvent
import com.jonaswanke.calendar.CalendarView
import com.jonaswanke.calendar.Event
import com.jonaswanke.calendar.utils.Day
import com.jonaswanke.calendar.utils.Week
import com.kulikulad.MessCul.R
import kotlinx.android.synthetic.main.activity_events.*
import java.util.*
import kotlin.math.abs

class EventsActivity : AppCompatActivity() {

    private var nextId: Long = 0
    private val random = Random();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        calendar.eventRequestCallback = {
            populate(it)
        }




        calendar.onEventClickListener = { event ->
            // EventWithPlace was clicked
            Toast.makeText(this, "${event.title} clicked", Toast.LENGTH_LONG).show()
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
    private fun populate(week: Week, force: Boolean = false) {
        if (!force && calendar.cachedEvents.contains(week))
            return

        val events = mutableListOf<Event>()
        for (i in 0..15) {
            val id = nextId++.toString()
            val start = week.start + abs(random.nextLong()) % DateUtils.WEEK_IN_MILLIS
            events.add(
                BaseEvent(
                    id,
                    id,
                    (random.nextInt() or 0xFF000000.toInt()) and 0x00202020.inv(),
                    start,
                    start + abs(random.nextLong()) % (DateUtils.DAY_IN_MILLIS / 8))
            )
        }

        calendar.setEventsForWeek(week, events)
    }



}
