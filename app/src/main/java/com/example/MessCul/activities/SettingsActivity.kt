package com.example.MessCul.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.MessCul.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.*
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.ByteArrayOutputStream
import java.io.File

class SettingsActivity : AppCompatActivity() {

    var mDatabase: DatabaseReference? = null;
    var mCurrentUser: FirebaseUser? = null;
    var mStorageRef:StorageReference? = null;
    var GALLERY_ID: Int = 1;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        mStorageRef = FirebaseStorage.getInstance().reference;
        mCurrentUser = FirebaseAuth.getInstance().currentUser;
        var userId = mCurrentUser!!.uid;

        mDatabase = FirebaseDatabase.getInstance().reference
            .child("Users")
            .child(userId);

        mDatabase!!.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                var displayName = dataSnapshot!!.child("display_name").value;
                var image = dataSnapshot!!.child("image").value.toString();
                var userStatus = dataSnapshot!!.child("status").value;
                var thumnnail = dataSnapshot!!.child("thumb_image").value;

                settingDisplayNameId.text = displayName.toString();
                settingsStatusText.text = userStatus.toString();

                if(!image!!.equals("default"))
                {
                    var image = dataSnapshot!!.child("image").value.toString();
                    Log.d("lol:", image);
                   // Picasso.with(applicationSettings)
                    Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.profile_img)
                        .into(settingsProfileID)
                }


            }

            override fun onCancelled(databaseErrorSnapshot: DatabaseError)
            {

            }
        })

        settingChangeStatus.setOnClickListener{

            var intent = Intent(this, StatusActivity::class.java);
            intent.putExtra("status", settingsStatusText.text.toString().trim());
            startActivity(intent);
            finish();
        }

        settingChangeImgBtn.setOnClickListener{
            var galleryIntent = Intent();
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "SELELCT_IMAGE"), GALLERY_ID);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK)
        {
            var image: Uri = data!!.data;

            CropImage.activity(image)
                .setAspectRatio(1,1)
                .start(this);
        }

        if(requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            val result = CropImage.getActivityResult(data);

            if(resultCode === Activity.RESULT_OK)
            {
                val resultUri = result.uri;
                var userId = mCurrentUser!!.uid;
                var thumbFile = File(resultUri.path);

                var thumbBitmap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(65)
                    .compressToBitmap(thumbFile);

                //Upload images to Firebase

                var byteArray = ByteArrayOutputStream();
                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
                var thumbByteArray: ByteArray;
                thumbByteArray = byteArray.toByteArray();

                var filePath = mStorageRef!!.child("chat_profile_images")
                    .child(userId + ".jpg");

                //Directory for thumb(smaller compressed images)
                var thumbFilePath = mStorageRef!!.child("chat_profile_images")
                    .child("thumbs")
                    .child(userId + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(){taskSnapshot->
                    filePath.downloadUrl.addOnCompleteListener{
                            taskSnapshot ->
                        if(taskSnapshot.isSuccessful) {
                            //Let's get the pic url
                            var downloadUrl = taskSnapshot.result.toString()

                            //Upload taski
                            var uploadTask: UploadTask = thumbFilePath
                                .putBytes(thumbByteArray)

                            uploadTask.addOnSuccessListener() { taskSnapshot ->
                                thumbFilePath.downloadUrl.addOnCompleteListener { taskSnapshot ->

                                    var thumbUrl = taskSnapshot.result.toString();
                                    if (taskSnapshot.isSuccessful) {
                                        var updateObj = HashMap<String, Any>()
                                        updateObj.put("image", downloadUrl) //big image
                                        updateObj.put("thumb_image", thumbUrl) //small image

                                        //we save the profile image
                                        mDatabase!!.updateChildren(updateObj)
                                            .addOnCompleteListener { task: Task<Void> ->
                                                if (task.isSuccessful) {
                                                    Toast.makeText(this, "Profile Image Saved!", Toast.LENGTH_LONG)
                                                        .show();
                                                } else {
                                                    Toast.makeText(this, "Profile Image NOT Saved!", Toast.LENGTH_LONG)
                                                        .show();
                                                }
                                            }
                                    } else { }
                                }
                            }
                        }else {}
                    }
                }
            }
        }
    }
}
