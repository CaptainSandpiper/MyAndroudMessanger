package com.kulikulad.MessCul.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.kulikulad.MessCul.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    var mCurrentuser: FirebaseUser? = null;
    var mUsersDatabase: DatabaseReference? = null;
    var userId: String? = null;

    private var lat:String? = null;
    private var lon:String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar!!.title = "Profile";
        supportActionBar!!.setDisplayHomeAsUpEnabled(true); //allow to go back to previous screen button on the top left

        if(intent.extras != null )
        {
            userId = intent.extras.get("userId").toString();
            mCurrentuser = FirebaseAuth.getInstance().currentUser;
            mUsersDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userId!!);

            setUpProfile();
        }

        setUserLocation();

        profileSendMessage.setOnClickListener{ //send message
            var chatIntent = Intent(this, ChatActivity::class.java)
            chatIntent.putExtra("userId", intent.extras.get("userId").toString());
            chatIntent.putExtra("name", intent.extras.get("name").toString());
            chatIntent.putExtra("status", intent.extras.get("status").toString());
            chatIntent.putExtra("profile", intent.extras.get("profile").toString());
            this.startActivity(chatIntent);

        }

        showUserLocation.setOnClickListener{
            //Show this User Location
            //Toast.makeText(this,"Click!",Toast.LENGTH_LONG).show();

            var locationIntent = Intent(this, UserLocationActivity::class.java)
            locationIntent.putExtra("userName", intent.extras.get("name").toString());
            locationIntent.putExtra("userId", intent.extras.get("userId").toString());
//            locationIntent.putExtra("userLat", lat);
//            locationIntent.putExtra("userLon", lon);
            startActivity(locationIntent);
        }


    }

    private fun setUpProfile() {

        mUsersDatabase!!.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var displayName = dataSnapshot.child("display_name").value.toString();
                var status = dataSnapshot.child("status").value.toString();
                var image = dataSnapshot.child("image").value.toString();

                profileName.text = displayName;
                profileStatus.text = status;
                Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.happy_woman)
                    .into(profilePicture);
            }

            override fun onCancelled(dataBaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun setUserLocation()
    {
        mUsersDatabase!!.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                lat = dataSnapshot.child("user_last_location").child("lat").value.toString();
                lon = dataSnapshot.child("user_last_location").child("lon").value.toString();
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
