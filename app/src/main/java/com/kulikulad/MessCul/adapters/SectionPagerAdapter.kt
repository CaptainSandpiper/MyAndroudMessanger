package com.kulikulad.MessCul.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kulikulad.MessCul.fragments.ChatsFragment
import com.kulikulad.MessCul.fragments.UsersFragment

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