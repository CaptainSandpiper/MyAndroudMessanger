package com.kulikulad.MessCul.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.Chats
import com.kulikulad.MessCul.models.getDialogId
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter (databaseQuery: DatabaseReference, var context: Context): FirebaseRecyclerAdapter<Chats, ChatsAdapter.ViewHolder>(Chats::class.java,
                                                                                                                                     R.layout.users_row,
                                                                                                                                     ViewHolder::class.java,
                                                                                                                                     databaseQuery)
{
    override fun populateViewHolder(viewHolder: ChatsAdapter.ViewHolder?, model: Chats?, position: Int) {

        var mFirebaseUser = FirebaseAuth.getInstance().currentUser;
        var secUserId = getRef(position).key; // the unique firebase id for this current user!

        var chatId = getDialogId(mFirebaseUser!!.uid, secUserId);

        viewHolder!!.bindView(model!!, context,mFirebaseUser!!.uid , secUserId!!, chatId!!);

        viewHolder!!.itemView.setOnClickListener{

            Toast.makeText(context, "${chatId}",Toast.LENGTH_LONG).show();
//            //create an alert dialog to current users if they want to see a profile or send message
//            var options = arrayOf("Open Profile", "Send Message");
//            var builder = AlertDialog.Builder(context)
//            builder.setTitle("Select Options");
//            builder.setItems(options, DialogInterface.OnClickListener{ dialogInterface, i ->
//
//                var userName = viewHolder.userNameTxt;
//                var userStat = viewHolder.userStatusTxt;
//                var profilePic = viewHolder.userProfilePicLink;
//
//                if(i == 0)
//                {
//                    //open user profile
//                    var profileIntetn = Intent(context, ProfileActivity::class.java);
//                    profileIntetn.putExtra("userId", userId);
//                    profileIntetn.putExtra("name", userName);
//                    profileIntetn.putExtra("status", userStat);
//                    profileIntetn.putExtra("profile", profilePic);
//                    context.startActivity(profileIntetn);
//                }
//                else
//                {
//                    //send message
//                    var chatIntent = Intent(context, ChatActivity::class.java)
//                    chatIntent.putExtra("userId", userId);
//                    chatIntent.putExtra("name", userName);
//                    chatIntent.putExtra("status", userStat);
//                    chatIntent.putExtra("profile", profilePic);
//                    context.startActivity(chatIntent);
//                }
//            });
//
//            builder.show();
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: String? = null;
        var userStatusTxt: String? = null;
        var userProfilePicLink:String? = null;

        fun bindView(chat: Chats, context: Context,firUserID:String, secUserId:String, chatId:String)
        {


            //var mUserRef =
            var userName = itemView.findViewById<TextView>(R.id.userName);
            var userStatus = itemView.findViewById<TextView>(R.id.userStatus);
            var userProfilePic = itemView.findViewById<CircleImageView>(R.id.usersProfile);
            var isNewMess = itemView.findViewById<TextView>(R.id.newMess);

            var mChatStatus = FirebaseDatabase.getInstance().reference
                .child("Chats")
                .child(chatId);

            mChatStatus.addValueEventListener(object: ValueEventListener
            {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if(dataSnapshot.child("${firUserID}").value != null) {
                        if (dataSnapshot.child("${firUserID}").value!!.equals("new")) {
                            isNewMess.visibility = View.VISIBLE;
                        } else if (dataSnapshot.child("${firUserID}").value!!.equals("old")) {
                            isNewMess.visibility = View.GONE;
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })



            var mDatabase = FirebaseDatabase.getInstance().reference
                .child("Users")
                .child(secUserId!!);

            //set string to pass in the intent
//            userNameTxt = chat.display_name;
//            userStatusTxt = user.status;
//            userProfilePicLink = user.thumb_image;
//
            mDatabase!!.addValueEventListener(object: ValueEventListener
            {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var displayName = dataSnapshot!!.child("display_name").value;
                    var image = dataSnapshot!!.child("image").value.toString();
                    var userStatuss = dataSnapshot!!.child("status").value;
                    var thumnnail = dataSnapshot!!.child("thumb_image").value;

                    userName.text = displayName.toString();
                    userStatus.text = userStatuss.toString();
                    Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.profile_img)
                        .into(userProfilePic)
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        }

    }
}