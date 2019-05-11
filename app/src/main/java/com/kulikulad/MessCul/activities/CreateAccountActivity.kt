package com.kulikulad.MessCul.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.kulikulad.MessCul.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null;
    var mDatabase: DatabaseReference? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        mAuth = FirebaseAuth.getInstance();

        accountCreateActBtn.setOnClickListener{
            var email = accountEmailEt.text.toString().trim();
            var password = accountPasswordEt.text.toString().trim();
            var dispalyName = accountDisplayNameEt.text.toString().trim();

            if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(dispalyName))
            {
                createAccount(email, password, dispalyName);
            }
            else
            {
                Toast.makeText(this,"Please fill out all the fields",Toast.LENGTH_LONG).show();
            }
        }
    }

    fun createAccount(email: String, password:String, dispalyName:String)
    {
        mAuth!!.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener{
                    task: Task<AuthResult> ->
                    if(task.isSuccessful)
                    {
                        var currentUser = mAuth!!.currentUser;
                        var userID = currentUser!!.uid;

                        mDatabase = FirebaseDatabase.getInstance().reference.child("Users").child(userID);

                        var userObject = HashMap<String, String>();
                        userObject.put("display_name", dispalyName);
                        userObject.put("status", "Hello there...");
                        userObject.put("image", "default");
                        userObject.put("thumb_image", "default");

                        mDatabase!!.setValue(userObject).addOnCompleteListener {
                            task2: Task<Void> ->
                            if(task2.isSuccessful)
                            {
                                Toast.makeText(this,"User Created!",Toast.LENGTH_LONG).show();
                                var dashBoardIntent = Intent(this, DashboardActivity::class.java);
                                dashBoardIntent.putExtra("name", dispalyName);
                                startActivity(dashBoardIntent);
                                finish();

                            }
                            else
                            {
                                Toast.makeText(this,"User NOT Created!",Toast.LENGTH_LONG).show();
                            }
                        };
                    }
                    else
                    {

                    }

                }
    }
}
