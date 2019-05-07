package com.example.MessCul.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MessCul.R
import com.example.MessCul.models.FriendlyMessage
import com.example.MessCul.models.getDialogId
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.custom_bar_image.*
import kotlinx.android.synthetic.main.custom_bar_image.view.*

class ChatActivity : AppCompatActivity() {

    var userId:String? = null;
    var mFirebaseDatabaseRef:DatabaseReference? = null;
    var mFirebaseUser: FirebaseUser? = null;

    var mLinearLayoutManager:LinearLayoutManager? = null;
    var mFirebaseAdapter:FirebaseRecyclerAdapter<FriendlyMessage, MessageViewHolder>? = null; //back to create MessageViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

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
                                                                                                mFirebaseDatabaseRef!!.child("messages").orderByChild("dialogId").equalTo(dialogId.toString()))
        {
            override fun populateViewHolder(viewHolder: MessageViewHolder?, friendlyMessage: FriendlyMessage?, position: Int)
            {
                if(friendlyMessage!!.text !=  null)
                {
                    viewHolder!!.bindView(friendlyMessage)

                    var currentUserId = mFirebaseUser!!.uid;
                    var isMe: Boolean  = friendlyMessage!!.id!!.equals(currentUserId);
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
            if(!intent.extras.get("name").toString().equals(""))
            {
                var currentUserName = intent.extras.get("name");
                var mCurrentUserId = mFirebaseUser!!.uid;

                var friendlyMessage =  FriendlyMessage(mCurrentUserId, messageEdt.text.toString().trim(), currentUserName.toString().trim(), userId.toString().trim())

                mFirebaseDatabaseRef!!.child("messages")
                    .push().setValue(friendlyMessage); // push used because every message must have ow unique id

                messageEdt.setText("");
            }
        }
    }

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var messageTextView: TextView? = null;
        var messengerTextView:TextView? = null;
        var profileImageView: CircleImageView? = null;
        var profileImageViewRight: CircleImageView? = null;

        fun bindView(friendlyMessage: FriendlyMessage)
        {
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messengerTextView = itemView.findViewById(R.id.messengerTextView);
            profileImageView = itemView.findViewById(R.id.messengerImageView);
            profileImageViewRight = itemView.findViewById(R.id.messengerImageViewRigth);

            messengerTextView!!.text = friendlyMessage.name;
            messageTextView!!.text  = friendlyMessage.text;

        }
    }
}
