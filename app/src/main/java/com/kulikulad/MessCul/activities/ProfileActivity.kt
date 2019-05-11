package com.kulikulad.MessCul.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kulikulad.MessCul.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    var mCurrentuser: FirebaseUser? = null;
    var mUsersDatabase: DatabaseReference? = null;
    var userId: String? = null;

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
}
