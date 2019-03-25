package com.pathtofblaquiz.pathtofbla

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_wizard_five.*

/**
 * Wizard Fragment that handles a page in the wizard slideshow
 * This last page has a got it button which takes user back to the dashboard activity
 */
class WizardFragmentFive : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wizard_five, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gotItButton.setOnClickListener {
            val switchToDashboard = Intent(this.context, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WizardFragmentFive().apply {
                arguments = Bundle().apply {
                }
            }
    }
}