package com.kulikulad.MessCul.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kulikulad.MessCul.R
import com.kulikulad.MessCul.adapters.ChatsAdapter
import kotlinx.android.synthetic.main.fragment_chats.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ChatsFragment : Fragment() {
    var mChatsFragment: DatabaseReference? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_chats, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var linearLayoutManager = LinearLayoutManager(context);
        linearLayoutManager.orientation = RecyclerView.VERTICAL;
        linearLayoutManager.reverseLayout = false;

        mChatsFragment = FirebaseDatabase.getInstance().reference.child("Chats");

        chatRecyclerViewId.setHasFixedSize(true);
        chatRecyclerViewId.layoutManager = linearLayoutManager;
        chatRecyclerViewId.adapter = ChatsAdapter(mChatsFragment!!, context!!);

    }
}
