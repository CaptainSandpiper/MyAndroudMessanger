package com.example.MessCul.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.MessCul.fragments.ChatsFragment
import com.example.MessCul.fragments.UsersFragment

class SectionPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
                when(position){
                    0 -> {return UsersFragment()
                    }
                    1 -> {return ChatsFragment()
                    }
                }
        return null!!;
            }

    override fun getCount(): Int {
                return 2;
            }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position)
        {
            0 ->{return "USERS"}
            1 ->{return "CHATS"}
        }
        return null!!;
    }

}