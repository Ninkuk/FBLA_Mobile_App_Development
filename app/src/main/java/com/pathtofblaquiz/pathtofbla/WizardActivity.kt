package com.pathtofblaquiz.pathtofbla

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_wizard.*

/**
 * Wizard Activity handles the introductory slideshow that the new users see and that the old users can see through the about page
 */
class WizardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        //creates the adapter for the wizard slideshow using Fragments Manager
        val adapter = WizardAdapter(supportFragmentManager)

        //sets the adapter to the wizard View Pager
        wizardViewPager.adapter = adapter

        //creates the pagination dots at the bottom of the page
        dot0.text = Html.fromHtml("&#8226")
        dot0.textSize = 35F
        dot0.setTextColor(resources.getColor(R.color.white))
        dot1.text = Html.fromHtml("&#8226")
        dot1.textSize = 35F
        dot1.setTextColor(resources.getColor(R.color.gray))
        dot2.text = Html.fromHtml("&#8226")
        dot2.textSize = 35F
        dot2.setTextColor(resources.getColor(R.color.gray))
        dot3.text = Html.fromHtml("&#8226")
        dot3.textSize = 35F
        dot3.setTextColor(resources.getColor(R.color.gray))
        dot4.text = Html.fromHtml("&#8226")
        dot4.textSize = 35F
        dot4.setTextColor(resources.getColor(R.color.gray))

        //handles the View Pager changes such as changing the pages of the wizard
        wizardViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        dot0.setTextColor(resources.getColor(R.color.white))
                        dot1.setTextColor(resources.getColor(R.color.gray))
                        dot2.setTextColor(resources.getColor(R.color.gray))
                        dot3.setTextColor(resources.getColor(R.color.gray))
                        dot4.setTextColor(resources.getColor(R.color.gray))
                    }
                    1 -> {
                        dot0.setTextColor(resources.getColor(R.color.gray))
                        dot1.setTextColor(resources.getColor(R.color.white))
                        dot2.setTextColor(resources.getColor(R.color.gray))
                        dot3.setTextColor(resources.getColor(R.color.gray))
                        dot4.setTextColor(resources.getColor(R.color.gray))
                    }
                    2 -> {
                        dot0.setTextColor(resources.getColor(R.color.gray))
                        dot1.setTextColor(resources.getColor(R.color.gray))
                        dot2.setTextColor(resources.getColor(R.color.white))
                        dot3.setTextColor(resources.getColor(R.color.gray))
                        dot4.setTextColor(resources.getColor(R.color.gray))
                    }
                    3 -> {
                        dot0.setTextColor(resources.getColor(R.color.gray))
                        dot1.setTextColor(resources.getColor(R.color.gray))
                        dot2.setTextColor(resources.getColor(R.color.gray))
                        dot3.setTextColor(resources.getColor(R.color.white))
                        dot4.setTextColor(resources.getColor(R.color.gray))
                    }
                    4 -> {
                        dot0.setTextColor(resources.getColor(R.color.gray))
                        dot1.setTextColor(resources.getColor(R.color.gray))
                        dot2.setTextColor(resources.getColor(R.color.gray))
                        dot3.setTextColor(resources.getColor(R.color.gray))
                        dot4.setTextColor(resources.getColor(R.color.white))
                    }
                    else -> {
                        dot0.setTextColor(resources.getColor(R.color.white))
                        dot1.setTextColor(resources.getColor(R.color.gray))
                        dot2.setTextColor(resources.getColor(R.color.gray))
                        dot3.setTextColor(resources.getColor(R.color.gray))
                        dot4.setTextColor(resources.getColor(R.color.gray))
                    }
                }
            }

        })

        //handles the skip button and changes the activity to Dashboard
        skipWizard.setOnClickListener {
            val switchToDashboard = Intent(this, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        }
    }
}