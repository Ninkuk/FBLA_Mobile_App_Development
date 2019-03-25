package com.pathtofblaquiz.pathtofbla

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
/**
 * Wizard Adapter allows for easy creation of the wizard
 */
class WizardAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentList = listOf(
        WizardFragmentOne(),
        WizardFragmentTwo(),
        WizardFragmentThree(),
        WizardFragmentFour(),
        WizardFragmentFive()
    )

    override fun getItem(position: Int): Fragment {
        //sets the fragments sequentially using the list
        return fragmentList[position]
    }

    override fun getCount(): Int {
        //gets the precise number of pages needed for the wizard through the page list size
        return fragmentList.size
    }
}