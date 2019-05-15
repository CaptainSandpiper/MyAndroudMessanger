package com.kulikulad.MessCul.adapters

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.models.Chats
import de.hdodenhof.circleimageview.CircleImageView

class ChatsAdapter (databaseQuery: DatabaseReference, var context: Context): FirebaseRecyclerAdapter<Chats, ChatsAdapter.ViewHolder>(Chats::class.java,
                                                                                                                                     R.layout.users_row,
                                                                                                                                     ViewHolder::class.java,
                                                                                                                                     databaseQuery)
{
    override fun populateViewHolder(viewHolder: ChatsAdapter.ViewHolder?, model: Chats?, position: Int) {

        var mFirebaseUser = FirebaseAuth.getInstance().currentUser;
        var chatId = getRef(position).key; // the unique firebase id for this current user!
        if(chatId!!.contains(mFirebaseUser!!.uid)) {
            var secUserId = chatId.split(mFirebaseUser!!.uid);
            viewHolder!!.bindView(model!!, context, secUserId[0]);
        }

//        viewHolder!!.itemView.setOnClickListener{
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
//        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: String? = null;
        var userStatusTxt: String? = null;
        var userProfilePicLink:String? = null;

        fun bindView(chat: Chats, context: Context, secUserId:String?)
        {
            //var mUserRef =
            var userName = itemView.findViewById<TextView>(R.id.userName);
            var userStatus = itemView.findViewById<TextView>(R.id.userStatus);
            var userProfilePic = itemView.findViewById<CircleImageView>(R.id.usersProfile);

            //set string to pass in the intent
//            userNameTxt = chat.display_name;
//            userStatusTxt = user.status;
//            userProfilePicLink = user.thumb_image;
//
            userName.text = secUserId;
            userStatus.text = "Status " + secUserId;
//            Picasso.get()
//                .load(userProfilePicLink)
//                .placeholder(R.drawable.profile_img)
//                .into(userProfilePic)
        }

    }
}