package com.kulikulad.MessCul.activities

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.FriendlyMessage
import com.kulikulad.MessCul.models.getDialogId
import com.kulikulad.MessCul.models.getFileName
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.custom_bar_image.view.*
import java.text.SimpleDateFormat
import java.util.*



class ChatActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mAdView : AdView;
    private val CHOOSING_FILE_REQUEST = 1234
    private val GALLERY_ID: Int = 1;

    private var fileNameSt:String? = null;
    private var fileUrlSt:String? = null;

    var userId:String? = null;
    var mFirebaseDatabaseRef:DatabaseReference? = null;
    var mFirebaseUser: FirebaseUser? = null;
    var mStorageRef: StorageReference? = null;

    var mLinearLayoutManager:LinearLayoutManager? = null;
    var mFirebaseAdapter:FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>? = null; //back to create MessageViewHolder

    private val STORAGE_PERMISSION_CODE: Int = 1000;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        ////banner
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");

        mAdView = this.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        ////delete

        mStorageRef = FirebaseStorage.getInstance().reference;
        mFirebaseUser = FirebaseAuth.getInstance().currentUser;

        userId = intent.extras.getString("userId");
        mLinearLayoutManager = LinearLayoutManager(this);
        mLinearLayoutManager!!.stackFromEnd = true;

        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowCustomEnabled(true);
        var inflater = this.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;

        var actionBarView = inflater.inflate(R.layout.custom_bar_image, null);
        actionBarView.customBarName.text = intent.extras.getString("name");

        var profileImgLink = intent.extras.get("profile").toString();
        Picasso.get()
            .load(profileImgLink)
            .placeholder(R.drawable.profile_img)
            .into(actionBarView.customBarCircleImage);

        supportActionBar!!.customView = actionBarView;

        mFirebaseDatabaseRef = FirebaseDatabase.getInstance().reference;

        var dialogId = getDialogId( mFirebaseUser!!.uid, userId.toString());

        mFirebaseAdapter = object: FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>(FriendlyMessage::class.java,
                                                                                                R.layout.item_message,
                                                                                                MessageViewHolder::class.java,
                                                                                                mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages").orderByChild("dialogId").equalTo(dialogId.toString()))
        {
            override fun populateViewHolder(viewHolder: MessageViewHolder?, friendlyMessage: FriendlyMessage?, position: Int)
            {
                var currentUserId = mFirebaseUser!!.uid;
                var isMe: Boolean  = friendlyMessage!!.id!!.equals(currentUserId)

                if(friendlyMessage!!.text !=  null)
                {
                    viewHolder!!.bindView(friendlyMessage)
                    ////////////////CHECK
                    if(friendlyMessage!!.type.equals("file"))
                    {
                        viewHolder!!.downloadFileButton!!.setOnClickListener{

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            {
                                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                                {
                                    fileUrlSt = friendlyMessage!!.text;
                                    fileNameSt = friendlyMessage!!.textForFile;
                                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                                }
                                else
                                {
                                    startDowloading(friendlyMessage!!.text, friendlyMessage!!.textForFile)
                                }
                            }
                            else
                            {
                                startDowloading(friendlyMessage!!.text, friendlyMessage!!.textForFile)
                            }
                            //startDowloading(friendlyMessage!!.text)
                        }
                    }
                    //////////////////////////////////////////////////////
                    else if(friendlyMessage!!.type.equals("meeting"))
                    {

                        var meetingLat: Double? = null;
                        var meetingLng: Double? = null;
                        var markerText: String? = null;

                        mFirebaseDatabaseRef!!.child("Meetings").child(friendlyMessage.text.toString())
                            .addListenerForSingleValueEvent(object: ValueEventListener{
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    var year = dataSnapshot!!.child("meetingYear").value.toString()
                                    var month = dataSnapshot!!.child("meetingMonth").value.toString()
                                    var day = dataSnapshot!!.child("meetingDay").value.toString()

                                    var hour = dataSnapshot!!.child("meetingHour").value.toString()
                                    var minutes = dataSnapshot!!.child("meetingMinute").value.toString()
                                    var subj = dataSnapshot!!.child("subjectMeet").value.toString()
                                    var descr = dataSnapshot!!.child("descriptionMeet").value.toString()

                                    meetingLat = dataSnapshot!!.child("meetingLat").value.toString().toDouble();
                                    meetingLng = dataSnapshot!!.child("meetingLng").value.toString().toDouble();


                                    markerText = subj;
                                    viewHolder.messageTextView!!.gravity = Gravity.LEFT;
                                    viewHolder.messageTextView!!.text = friendlyMessage!!.textForFile + " : Let's meet: "+ "$year:$month:$day "+ " $hour:$minutes" +"\nSubject: $subj \nDescription: $descr";


                                }

                                override fun onCancelled(p0: DatabaseError) {
                                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                                }

                            })

                        viewHolder!!.showLocationMeetingButton!!.setOnClickListener {

                            //how to get context
                            var intent = Intent(this@ChatActivity, MeetingPlaceActivity::class.java)
                            intent.putExtra("meetingLat", meetingLat);
                            intent.putExtra("meetingLng", meetingLng);
                            intent.putExtra("markerText", markerText);
                            startActivity(intent)
                        }


                        if(!isMe) {


                            if (friendlyMessage!!.textForFile.equals("not accepted")) {
                                viewHolder!!.acceptMeetingButton!!.setOnClickListener {

                                    var updateObj = HashMap<String, Any>()
                                    updateObj.put("textForFile", "accepted")

                                    mFirebaseDatabaseRef!!.child("Chats").child(friendlyMessage.dialogId.toString())
                                        .child("messages").child(friendlyMessage.messId.toString())
                                        .updateChildren(updateObj);

                                }
                                viewHolder!!.declineMeetingButton!!.setOnClickListener {

                                    var updateObj = HashMap<String, Any>()
                                    updateObj.put("textForFile", "declined")

                                    mFirebaseDatabaseRef!!.child("Chats").child(friendlyMessage.dialogId.toString())
                                        .child("messages").child(friendlyMessage.messId.toString())
                                        .updateChildren(updateObj);


                                    mFirebaseDatabaseRef!!.child("Users").child(mFirebaseUser!!.uid).child("Meetings").child(friendlyMessage!!.text.toString()).removeValue()
                                    mFirebaseDatabaseRef!!.child("Users").child(userId.toString()).child("Meetings").child(friendlyMessage!!.text.toString()).removeValue()
                                }
                            } else if (friendlyMessage!!.textForFile.equals("declined")) {
                                viewHolder!!.acceptMeetingButton!!.visibility = View.GONE;
                                viewHolder!!.declineMeetingButton!!.visibility = View.GONE;

                                viewHolder!!.messageTextView!!.text = "Declined: " + viewHolder!!.messageTextView!!.text.toString()
                            } else if (friendlyMessage!!.textForFile.equals("accepted")) {
                                viewHolder!!.acceptMeetingButton!!.visibility = View.GONE;
                                viewHolder!!.declineMeetingButton!!.visibility = View.GONE;

                                viewHolder!!.messageTextView!!.text =
                                    "Accepted: " + viewHolder!!.messageTextView!!.text.toString()
                            }
                        }
                        else
                        {

                            viewHolder!!.acceptMeetingButton!!.visibility = View.GONE;
                            viewHolder!!.declineMeetingButton!!.visibility = View.GONE;
                            if (friendlyMessage!!.textForFile.equals("declined")) {

                                viewHolder!!.messageTextView!!.text =
                                    "Declined: " + viewHolder!!.messageTextView!!.text.toString()
                            } else if (friendlyMessage!!.textForFile.equals("accepted")) {

                            viewHolder!!.messageTextView!!.text =
                                "Accepted: " + viewHolder!!.messageTextView!!.text.toString()
                            }
                        }

                    }
                    //////////////

;
                    if(isMe)
                    {
                        //Me to right side
                        viewHolder.profileImageViewRight!!.visibility = View.VISIBLE;
                        viewHolder.profileImageView!!.visibility = View.GONE;
                        viewHolder.messageTextView!!.gravity = (Gravity.CENTER_VERTICAL or Gravity.RIGHT);
                        viewHolder.messengerTextView!!.gravity = (Gravity.CENTER_VERTICAL or Gravity.RIGHT);



                        //get image url for me
                        mFirebaseDatabaseRef!!.child("Users")
                            .child(currentUserId)
                            .addValueEventListener(object:ValueEventListener{
                                    override fun onDataChange(data: DataSnapshot)
                                    {
                                        var imageUrl = data!!.child("thumb_image").value.toString();
                                        var displayName = data!!.child("display_name").value.toString();

//                                    viewHolder.messengerTextView!!.text = displayName.toString();
                                        viewHolder.messengerTextView!!.text = "I wrote...";

                                        Picasso.get()
                                            .load(imageUrl)
                                            .placeholder(R.drawable.profile_img)
                                            .into(viewHolder.profileImageViewRight);
                                    }

                                    override fun onCancelled(p0: DatabaseError)
                                    {

                                    }
                            })
                    }
                    else
                    {
                        //to other person show image view in left side

                        //Me to right side
                        viewHolder.profileImageViewRight!!.visibility = View.GONE;
                        viewHolder.profileImageView!!.visibility = View.VISIBLE;
                        viewHolder.messageTextView!!.gravity = (Gravity.CENTER_VERTICAL or Gravity.LEFT);
                        viewHolder.messengerTextView!!.gravity = (Gravity.CENTER_VERTICAL or Gravity.LEFT);

                        //get image url for me
                        mFirebaseDatabaseRef!!.child("Users")
                            .child(userId.toString())
                            .addValueEventListener(object:ValueEventListener{
                                override fun onDataChange(data: DataSnapshot)
                                {
                                    var imageUrl = data!!.child("thumb_image").value.toString();
                                    var displayName = data!!.child("display_name").value.toString();

                                    viewHolder.messengerTextView!!.text = "$displayName wrote...";

                                    Picasso.get()
                                        .load(imageUrl)
                                        .placeholder(R.drawable.profile_img)
                                        .into(viewHolder.profileImageView);
                                }

                                override fun onCancelled(p0: DatabaseError)
                                {

                                }
                            })
                    }
                }
            }
        }

        //set the RecyclerView
        messageRecyclerView.layoutManager = mLinearLayoutManager;
        messageRecyclerView.adapter = mFirebaseAdapter;

        sendButton.setOnClickListener{
            if(!intent.extras.get("name").toString().equals("") && messageEdt.text.toString().trim() != "")
            {
                var currentUserName = intent.extras.get("name");
                var mCurrentUserId = mFirebaseUser!!.uid;

                var friendlyMessage =  FriendlyMessage(mCurrentUserId, messageEdt.text.toString().trim(), currentUserName.toString().trim(), userId.toString().trim(), "text")




//                mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages")
//                    .push().setValue(friendlyMessage);

                var ref = mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages");
                val messId = ref.push().key;
                friendlyMessage.messId = messId;
                ref.child(messId!!).setValue(friendlyMessage);

                //////////CHECK
                var updateObj = HashMap<String, Any>()
                updateObj.put("${userId.toString().trim()}", "new")
                updateObj.put("${mCurrentUserId}", "old")

                mFirebaseDatabaseRef!!.child("Chats").child(dialogId).updateChildren(updateObj);

                mFirebaseDatabaseRef!!.child("Users").child(mCurrentUserId).child("Chats").child(userId.toString().trim()).child("messages")
                .push().setValue(friendlyMessage); // push used because every message must have ow unique id
                mFirebaseDatabaseRef!!.child("Users").child(userId.toString().trim()).child("Chats").child(mCurrentUserId).child("messages")
                    .push().setValue(friendlyMessage); // push used because every message must have ow unique id

                //////////
                messageEdt.setText("");
            }
        }




        var itemBuilder:SubActionButton.Builder = SubActionButton.Builder(this);
        //many buttons

        var imageView1 = ImageView(this);
        imageView1.setImageResource(R.drawable.ic_add_black_24dp);
        var subActionButton1 = itemBuilder.setContentView(imageView1).build();

        var imageView2 = ImageView(this);
        imageView2.setImageResource(R.drawable.ic_add_friend);
        var subActionButton2 = itemBuilder.setContentView(imageView2).build();

        var imageView3 = ImageView(this);
        imageView3.setImageResource(R.drawable.ic_add_group_dialog);
        var subActionButton3 = itemBuilder.setContentView(imageView3).build();

        var actionMenu = FloatingActionMenu.Builder(this)
            .addSubActionView(subActionButton1)
            .addSubActionView(subActionButton2)
            .addSubActionView(subActionButton3)
            .attachTo(addMessageImageView)
            .build();

        subActionButton1.setOnClickListener{
            //send pic
            showChoosingImage();
        }

        subActionButton2.setOnClickListener{
            //send pic
            showChoosingFile();


            //Toast.makeText(this,"$kek",Toast.LENGTH_LONG).show();

        }

        subActionButton3.setOnClickListener{
            //send pic

            var currentUserName = intent.extras.get("name");
            var intent = Intent(this, SetEventActivity::class.java);
            intent.putExtra("mCurrentUserId", mFirebaseUser!!.uid);
            intent.putExtra("recipientUserId", userId)
            intent.putExtra("currentUserName", currentUserName.toString().trim())
            startActivity(intent);
//            var mCheck = FirebaseStorage().getReference()
        }


    }

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var messageTextView: TextView? = null;
        var messengerTextView:TextView? = null;
        var profileImageView: CircleImageView? = null;
        var profileImageViewRight: CircleImageView? = null;
        var messageImageView: ImageView? = null;
        var downloadFileButton: Button? = null;
        var showLocationMeetingButton: Button? = null;
        var acceptMeetingButton: Button? = null;
        var declineMeetingButton: Button? = null;

        //experiment
        var activity:Activity? =null;

        fun bindView(friendlyMessage: FriendlyMessage)
        {
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messengerTextView = itemView.findViewById(R.id.messengerTextView);
            profileImageView = itemView.findViewById(R.id.messengerImageView);
            profileImageViewRight = itemView.findViewById(R.id.messengerImageViewRigth);
            showLocationMeetingButton = itemView.findViewById(R.id.showLocationMeeting);
            acceptMeetingButton = itemView.findViewById(R.id.acceptMeeting);
            declineMeetingButton = itemView.findViewById(R.id.declineMeeting);



            if (friendlyMessage.type.equals("text"))
            {
                messengerTextView!!.text = friendlyMessage.name;
                messageTextView!!.text  = friendlyMessage.text;
            }
            else if(friendlyMessage.type.equals("pic"))
            {
                messageImageView = itemView.findViewById(R.id.messageImageView);

                messengerTextView!!.text = friendlyMessage.name;
                Picasso.get()
                    .load(friendlyMessage.text)
                    .placeholder(R.drawable.happy_woman)
                    .into(messageImageView);

                messageImageView!!.setOnClickListener{

                }
            }

            else if(friendlyMessage.type.equals("file"))
            {
                downloadFileButton = itemView.findViewById(R.id.downloadFileButton);
                downloadFileButton!!.visibility = View.VISIBLE;
                downloadFileButton!!.isClickable = true;
                downloadFileButton!!.text = "DOWNLOAD: "+friendlyMessage.textForFile;

            }

            else if(friendlyMessage.type.equals("meeting"))
            {
                showLocationMeetingButton!!.visibility = View.VISIBLE;
                acceptMeetingButton!!.visibility = View.VISIBLE;
                declineMeetingButton!!.visibility =View.VISIBLE;

            }

        }




    }

    private fun showChoosingImage() {
        val galleryIntent = Intent();
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(galleryIntent, "SELELCT_IMAGE"), GALLERY_ID);
    }

    private fun showChoosingFile()
    {
        val fileIntent = Intent();
        fileIntent.type = "file/*"
        fileIntent.action = Intent.ACTION_GET_CONTENT;
        startActivityForResult(Intent.createChooser(fileIntent, "SELELCT_FILE"), CHOOSING_FILE_REQUEST);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        var image:Uri? = null;
        var file:Uri? = null;

        var mCurrentUserId = mFirebaseUser!!.uid;
        var recipientUserId = userId.toString().trim();
        var chatId = getDialogId(mCurrentUserId, recipientUserId);

        var date = Date();
        val formatter = SimpleDateFormat("MMMddyyyyHH:mma")
        val answer: String = formatter.format(date)

        if(requestCode == GALLERY_ID && resultCode == Activity.RESULT_OK)
        {
            image = data!!.data;

            var filePath = mStorageRef!!.child("users_chat_pictures")
                .child(chatId+answer+ ".jpg");

            filePath.putFile(image).addOnSuccessListener() { taskSnapshot ->
                filePath.downloadUrl.addOnCompleteListener { taskSnapshot ->
                    if (taskSnapshot.isSuccessful) {
                        var downloadUrl = taskSnapshot.result.toString()
                        var currentUserName = intent.extras.get("name");
                        var dialogId = getDialogId( mFirebaseUser!!.uid, userId.toString());
                        var friendlyMessage =  FriendlyMessage(mCurrentUserId, downloadUrl, currentUserName.toString().trim(), recipientUserId, "pic")

                        mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages")
                            .push().setValue(friendlyMessage); // push used because every message must have ow unique id

                        //Toast.makeText(this,"File is uploaded! ",Toast.LENGTH_LONG).show();

                        //////////CHECK
                        var updateObj = HashMap<String, Any>()
                        updateObj.put("${userId.toString().trim()}", "new")
                        updateObj.put("${mCurrentUserId}", "old")

                        mFirebaseDatabaseRef!!.child("Chats").child(dialogId).updateChildren(updateObj);

                        mFirebaseDatabaseRef!!.child("Users").child(mCurrentUserId).child("Chats").child(userId.toString().trim()).child("messages")
                            .push().setValue(friendlyMessage); // push used because every message must have ow unique id
                        mFirebaseDatabaseRef!!.child("Users").child(userId.toString().trim()).child("Chats").child(mCurrentUserId).child("messages")
                            .push().setValue(friendlyMessage); // push used because every message must have ow unique id

                        //////////
                    }
                    else
                    {
                        //Toast.makeText(this,"Somethimg goes wrong ",Toast.LENGTH_LONG).show();
                    }
                }
            }

        }

        if(requestCode == CHOOSING_FILE_REQUEST && resultCode == Activity.RESULT_OK)
        {
            file = data!!.data;
            Toast.makeText(this,"${file.toString()} ",Toast.LENGTH_LONG).show();

            var fileName = getFileName(file.toString());

            var filePath = mStorageRef!!.child("users_chat_files")
                .child(answer + fileName);

            filePath.putFile(file).addOnSuccessListener() { taskSnapshot ->
                filePath.downloadUrl.addOnCompleteListener { taskSnapshot ->
                    if (taskSnapshot.isSuccessful) {
                        var downloadUrl = taskSnapshot.result.toString()

                        var currentUserName = intent.extras.get("name");

                        var dialogId = getDialogId( mFirebaseUser!!.uid, userId.toString());
                        var friendlyMessage =  FriendlyMessage(mCurrentUserId, downloadUrl, currentUserName.toString().trim(), recipientUserId, "file", fileName)

                        mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).child("messages")
                            .push().setValue(friendlyMessage); // push used because every message must have ow unique id


                        //////////CHECK
                        var updateObj = HashMap<String, Any>()
                        updateObj.put("${userId.toString().trim()}", "new")
                        updateObj.put("${mCurrentUserId}", "old")

                        mFirebaseDatabaseRef!!.child("Chats").child(dialogId).updateChildren(updateObj);

                        mFirebaseDatabaseRef!!.child("Users").child(mCurrentUserId).child("Chats").child(userId.toString().trim()).child("messages")
                            .push().setValue(friendlyMessage); // push used because every message must have ow unique id
                        mFirebaseDatabaseRef!!.child("Users").child(userId.toString().trim()).child("Chats").child(mCurrentUserId).child("messages")
                            .push().setValue(friendlyMessage); // push used because every message must have ow unique id

                        //////////
                    }
                    else
                    {
                        //Toast.makeText(this,"Somethimg goes wrong ",Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private fun startDowloading(fileUrl:String?, fileName:String?)
    {
        var url = fileUrl;

        val request = DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI  or DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("$fileName");
        request.setDescription("File is downloading...")

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir("/MyChatApp", "${fileName}");

        val manager = getSystemService(android.content.Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            STORAGE_PERMISSION_CODE ->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startDowloading(fileUrlSt, fileNameSt)
                }
                else
                {
                    Toast.makeText(this,"Permission denied!!",Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()

        var dialogId = getDialogId( mFirebaseUser!!.uid, userId.toString());

        var updateObj = HashMap<String, Any>()
        updateObj.put("${mFirebaseUser!!.uid}", "old")

        mFirebaseDatabaseRef!!.child("Chats").child(dialogId!!).updateChildren(updateObj);

        //Toast.makeText(this, "AAAAAAAAAAAAA", Toast.LENGTH_LONG).show();
    }



    override fun onMapReady(map: GoogleMap?) {
        map!!.addMarker(MarkerOptions().position(LatLng(0.0,0.0)).title("Marker in Sydney"))
        map!!.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.0,0.0)))

    }


}
