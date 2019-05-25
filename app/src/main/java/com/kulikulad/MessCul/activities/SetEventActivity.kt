package com.kulikulad.MessCul.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.jonaswanke.calendar.utils.dayOfWeek
import com.jonaswanke.calendar.utils.toWeek
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.FriendlyMessage
import com.kulikulad.MessCul.models.getDialogId
import kotlinx.android.synthetic.main.activity_set_event.*
import java.text.SimpleDateFormat
import java.util.*

class SetEventActivity : AppCompatActivity() {

    private var nextId: Long = 0
    private val random = Random();

    var mFirebaseDatabaseRef: DatabaseReference? = null;
    var mFirebaseUser: FirebaseUser? = null;

    var yearSel: Int?= null;
    var monthSel: Int? = null;
    var daySel: Int? = null;
    var hourSel: Int? = null;
    var minutesSel:Int? = null;
    var dayOfWeekSel:Int? = null;
    var weekOfYear:Int? = null;

    var meetingDate: Date? = null;
    var meetingTime: Date? = null;
    var meetingLat: Double? = null;
    var meetingLng: Double? = null;

    var currentUserId: String? = null;
    var recipientUserId:String? = null;
    var currentUserName: String? = null;

    val REQUEST_CODE = 1;
    var formate = SimpleDateFormat("dd MMM, YYYY", Locale.US);
    var timeFormat = SimpleDateFormat("hh:mm a", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_event)

        currentUserId = intent.extras.getString("mCurrentUserId")
        recipientUserId = intent.extras.getString("recipientUserId")
        currentUserName = intent.extras.getString("currentUserName")

        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference;

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

                hourSel = hourOfDay;
                minutesSel = minute;

                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedTime.set(Calendar.MINUTE, minute);

                Toast.makeText(this,"$hourSel:$minutesSel", Toast.LENGTH_LONG).show();
                /////////////////////////////////
                 meetingTime = selectedTime.time;

                setTimeButton.text = timeFormat.format(selectedTime.time);
                now.time = selectedTime.time;
            },  now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true );

            timePicker.show();
        }

        setDateButton.setOnClickListener{
            val now = Calendar.getInstance();
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                val selectedDate = Calendar.getInstance();
                yearSel = year;
                monthSel = month;
                daySel = dayOfMonth;


                selectedDate.set(Calendar.YEAR,year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val date = formate.format(selectedDate.time)

                ///////////////////////////////
                meetingDate = selectedDate.time
                dayOfWeekSel = selectedDate.dayOfWeek;
                weekOfYear = selectedDate.toWeek().week;

                setDateButton.text = date;
                Toast.makeText(this, "$dayOfWeekSel", Toast.LENGTH_LONG).show();
            },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))


            datePicker.datePicker.minDate = Date().time;
            datePicker.show();
        }

        setLocationButton.setOnClickListener{
            var intent = Intent(this, EventPlaceActivity::class.java);
            startActivityForResult(intent, REQUEST_CODE);
        }

        setEventMeetingButton.setOnClickListener{
            if(meetingDate != null && meetingTime != null && meetingLat != null && meetingLng != null && !subjectMeet.text.toString().trim().equals("") && !descriptionMeet.text.toString().trim().equals(""))
            {
                Toast.makeText(this,"good", Toast.LENGTH_LONG).show();

                var dialogId = getDialogId(currentUserId, recipientUserId);
                var meetingId = dialogId;
                meetingId += meetingDate;
                meetingId += meetingTime;

                var friendlyMessage = FriendlyMessage(currentUserId!!, meetingId!!, currentUserName.toString().trim(), recipientUserId, "meeting", "not accepted");

                var meetingObj = HashMap<String, Any>();
                meetingObj.put("subjectMeet", subjectMeet.text.toString().trim());
                meetingObj.put("descriptionMeet", descriptionMeet.text.toString().trim());
                meetingObj.put("meetingYear", yearSel.toString());
                meetingObj.put("meetingMonth", monthSel.toString());
                meetingObj.put("meetingDay", daySel.toString());
                meetingObj.put("meetingHour", hourSel.toString());
                meetingObj.put("meetingMinute", minutesSel.toString());
                meetingObj.put("meetingDayOfWeek", dayOfWeekSel.toString());
                meetingObj.put("meetingLat",meetingLat.toString());
                meetingObj.put("meetingLng",meetingLng.toString());
                meetingObj.put("meetingWeekOfYear", weekOfYear.toString());

                mFirebaseDatabaseRef!!.child("Meetings").child(meetingId)
                    .updateChildren(meetingObj);

//                mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages")
//                    .push().setValue(friendlyMessage); // push used because every message must have ow unique id
                var ref = mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages");
                val messId = ref.push().key;
                friendlyMessage.messId = messId;
                ref.child(messId!!).setValue(friendlyMessage);

                mFirebaseDatabaseRef!!.child("Users").child(currentUserId!!).child("Chats").child(recipientUserId.toString().trim()).child("messages")
                    .push().setValue(friendlyMessage); // push used because every message must have ow unique id


                ////////////////
                mFirebaseDatabaseRef!!.child("Users").child(currentUserId!!).child("Meetings").child(meetingId).updateChildren(meetingObj)
                mFirebaseDatabaseRef!!.child("Users").child(recipientUserId!!).child("Meetings").child(meetingId).updateChildren(meetingObj)









                finish();
            }
            else
            {
                Toast.makeText(this,"Please set all attributes", Toast.LENGTH_LONG).show();
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE )
        {
            if(resultCode == Activity.RESULT_OK)
            {
                var meetingLatStr = data!!.extras.get("selectedPlaceLat");
                var meetingLngStr = data!!.extras.get("selectedPlaceLng");

                meetingLat = meetingLatStr!!.toString().toDouble();
                meetingLng = meetingLngStr!!.toString().toDouble();

                Toast.makeText(this,"Lat: " + meetingLatStr + "lon:" + meetingLngStr,Toast.LENGTH_LONG).show()
            }
        }
    }
}
