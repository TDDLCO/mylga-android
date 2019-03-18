package co.tddl.mylga.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import co.tddl.mylga.AroundYou
import co.tddl.mylga.YourFeed

class TabPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private var numberOfTabs = 0

    constructor(fm: FragmentManager, numberOfTabs: Int) : this(fm) {
        this.numberOfTabs = numberOfTabs

    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> { YourFeed() }
            1 -> { AroundYou() }
            else -> YourFeed()
        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }

}