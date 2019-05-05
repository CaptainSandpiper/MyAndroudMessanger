package com.example.MessCul.adapters

import android.app.AlertDialog
import com.google.firebase.database.DatabaseReference
import android.content.Context;
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.MessCul.R
import com.example.MessCul.activities.ChatActivity
import com.example.MessCul.activities.ProfileActivity
import com.example.MessCul.models.Users
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_settings.*

//val options = FirebaseRecyclerOptions.Builder<Users>()
//    .setQuery(usersQuery, Users::class.java)
//    .setLifecycleOwner(UsersAdapter.ViewHolder::class.java)
//    .build();

class UsersAdapter(databaseQuery: DatabaseReference, var context: Context): FirebaseRecyclerAdapter<Users, UsersAdapter.ViewHolder>(Users::class.java,
                                                                                                                                    R.layout.users_row,
                                                                                                                                    ViewHolder::class.java,
                                                                                                                                    databaseQuery)
{
    override fun populateViewHolder(viewHolder: ViewHolder?, model: Users?, position: Int) {
        var userId = getRef(position).key; // the unique firebase id for this current user!
        viewHolder!!.bindView(model!!, context );

        viewHolder.itemView.setOnClickListener{
           //create an alert dialog to current users if they want to see a profile or send message
            var options = arrayOf("Open Profile", "Send Message");
            var builder = AlertDialog.Builder(context)
            builder.setTitle("Select Options");
            builder.setItems(options, DialogInterface.OnClickListener{dialogInterface, i ->

                var userName = viewHolder.userNameTxt;
                var userStat = viewHolder.userStatusTxt;
                var profilePic = viewHolder.userProfilePicLink;

                if(i == 0)
                {
                    //open user profile
                    var profileIntetn = Intent(context, ProfileActivity::class.java);
                    profileIntetn.putExtra("userId", userId);
                    context.startActivity(profileIntetn);
                }
                else
                {
                    //send message
                    var chatIntent = Intent(context, ChatActivity::class.java)
                    chatIntent.putExtra("userId", userId);
                    chatIntent.putExtra("name", userName);
                    chatIntent.putExtra("status", userStat);
                    chatIntent.putExtra("profile", profilePic);
                    context.startActivity(chatIntent);
                }
            });

            builder.show();
        }
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: String? = null;
        var userStatusTxt: String? = null;
        var userProfilePicLink:String? = null;

        fun bindView(user: Users, context: Context)
        {
            var userName = itemView.findViewById<TextView>(R.id.userName);
            var userStatus = itemView.findViewById<TextView>(R.id.userStatus);
            var userProfilePic = itemView.findViewById<CircleImageView>(R.id.usersProfile);

            //set string to pass in the intent
            userNameTxt = user.display_name;
            userStatusTxt = user.user_status;
            userProfilePicLink = user.thumb_image;

            userName.text = user.display_name;
            userStatus.text = user.user_status;
            Picasso.get()
                .load(userProfilePicLink)
                .placeholder(R.drawable.profile_img)
                .into(userProfilePic)
        }

    }
}