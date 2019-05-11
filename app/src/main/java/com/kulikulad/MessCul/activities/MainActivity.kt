package com.kulikulad.MessCul.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.UserLocation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null;
    var user: FirebaseUser? = null;
    var mAuthListener: FirebaseAuth.AuthStateListener? = null;
    var mDatabase: DatabaseReference? = null;

    var client:FusedLocationProviderClient? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = FirebaseAuth.AuthStateListener {
            firebaseAuth: FirebaseAuth ->

                user = firebaseAuth.currentUser;

            if(user != null) {

                //where we are??
                mDatabase = FirebaseDatabase.getInstance().reference
                    .child("Users")
                    .child(user!!.uid);

                client = LocationServices.getFusedLocationProviderClient(this);
                if (ActivityCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@AuthStateListener
                }
                client!!.lastLocation.addOnSuccessListener(this) {

                        location ->
                    if (location != null) {

                        var newLoc = UserLocation(location);
                        //update User attribute
                        var updateObj = HashMap<String, Any>()
                        updateObj.put("user_last_location", newLoc) //loc

                        mDatabase!!.updateChildren(updateObj)
                            .addOnCompleteListener { task: Task<Void> ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Profile Location Saved!", Toast.LENGTH_LONG)
                                        .show();
                                } else {
                                    Toast.makeText(this, "Profile Location NOT Saved!", Toast.LENGTH_LONG)
                                        .show();
                                }

                                Toast.makeText(
                                    this,
                                    "${location.latitude} lol ${location.longitude}",
                                    Toast.LENGTH_LONG
                                ).show();
                            }
                    }
                    //
                }
                    //Let's go to dashboard
                    startActivity(Intent(this, DashboardActivity::class.java));
                    finish();
            }
            else
            {
                Toast.makeText(this, "Not Signed In", Toast.LENGTH_LONG).show();
            }
        }

        createAccButton.setOnClickListener{
            startActivity(Intent(this, CreateAccountActivity::class.java));
        }

        loginButton.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java));
        }
    }

    override  fun onStart()
    {
        super.onStart();
        mAuth!!.addAuthStateListener(mAuthListener!!);
    }

    override fun onStop()
    {
        super.onStop()

        if(mAuthListener != null)
        {
            mAuth!!.removeAuthStateListener(mAuthListener!!);
        }
    }
}
