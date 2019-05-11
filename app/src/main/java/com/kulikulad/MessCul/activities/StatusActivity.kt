package com.kulikulad.MessCul.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kulikulad.MessCul.R
import kotlinx.android.synthetic.main.activity_status.*

class StatusActivity : AppCompatActivity() {

    var mDatabase:DatabaseReference? = null;
    var mCurrentUser: FirebaseUser? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)


        supportActionBar!!.title = "Status";

        if (intent.extras != null) {
            var oldStatus = intent.extras.get("status");
            statusUpdateEt.setText(oldStatus.toString());
        }

        if (intent.extras.equals(null))
        {
            statusUpdateEt.setText("Enter Your New Status");
        }

        mCurrentUser = FirebaseAuth.getInstance().currentUser;
        var userId = mCurrentUser!!.uid;

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId);



        statusUpdateBtn.setOnClickListener{
            var status = statusUpdateEt.text.toString().trim();

            mDatabase!!.child("status")
                .setValue(status).addOnCompleteListener{
                    task: Task<Void> ->
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this,"Status Updated Successfully!",Toast.LENGTH_LONG).show();

                        startActivity(Intent(this, SettingsActivity::class.java));
                        finish();
                    }
                    else
                    {
                        Toast.makeText(this,"Status Not Updated!",Toast.LENGTH_LONG).show();

                    }
                }


        }
    }
}
