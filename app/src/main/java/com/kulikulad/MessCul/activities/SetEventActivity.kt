package com.kulikulad.MessCul.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kulikulad.MessCul.R
import kotlinx.android.synthetic.main.activity_set_event.*
import java.text.SimpleDateFormat
import java.util.*

class SetEventActivity : AppCompatActivity() {

    val REQUEST_CODE = 1;
    var formate = SimpleDateFormat("dd MMM, YYYY", Locale.US);
    var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_event)

        setTimeButton.setOnClickListener{
            val now = Calendar.getInstance();

            try{
                if(setTimeButton.text != "Set Event Time")
                {
                    val date = timeFormat.parse(setTimeButton.text.toString())
                    now.time = date;
                }
            }
            catch (e:Exception)
            {

            }

            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance();
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);

                Toast.makeText(this,"time: "+timeFormat.format(selectedTime.time), Toast.LENGTH_LONG).show();

                setTimeButton.text = timeFormat.format(selectedTime.time);
                now.time = selectedTime.time;
            },  now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false );

            timePicker.show();
        }

        setDateButton.setOnClickListener{
            val now = Calendar.getInstance();
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                val selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR,year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val date = formate.format(selectedDate.time)

                setDateButton.text = date;
                Toast.makeText(this, date, Toast.LENGTH_LONG).show();
            },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))


            datePicker.datePicker.minDate = Date().time;
            datePicker.show();
        }

        setLocationButton.setOnClickListener{
            var intent = Intent(this, EventPlaceActivity::class.java);
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                var meetingLat = data!!.extras.get("selectedPlaceLat");
                var meetingLng = data!!.extras.get("selectedPlaceLng");

                Toast.makeText(this,"Lat: " + meetingLat + "lon:" + meetingLng,Toast.LENGTH_LONG).show()
            }
        }
    }
}
