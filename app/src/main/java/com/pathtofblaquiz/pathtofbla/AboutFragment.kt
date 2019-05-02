package com.pathtofblaquiz.pathtofbla

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.fragment_about.*

/**
 * About Fragment handles the about us layout
 *
 * Show users the information about the app
 * It allows them to watch the Wizard slideshow again and follow our app on different social media platforms
 */
class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //this is the function that is called when the layout is created
        buttonClick()
        checkInternetConnection()
    }

    /**
     * This function checks if the user's device is connected to the internet to access Firebase Database
     * If the user is not connected to the internet then an alert dialog box appears reminding them to do so
     */
    private fun checkInternetConnection() {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        //if there is no connection then it shows the alert and if there is a connection nothing happens
        if (networkInfo != null && networkInfo.isConnected) {
        } else {
            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Connection Alert")
            builder.setMessage("Please connect to the internet to use this app!")
            builder.setPositiveButton("Retry") { dialogInterface: DialogInterface, i: Int ->
                checkInternetConnection() //If the user presses retry then the function is called again
            }
            builder.show()
        }
    }

    private fun buttonClick() {
        tourWizardButton.setOnClickListener {
            val switchToWizard = Intent(this.context, WizardActivity::class.java)
            switchToWizard.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToWizard)
        }
        instagramFollowButton.setOnClickListener {
            val instagramFollow = Intent()
            instagramFollow.action = Intent.ACTION_VIEW
            instagramFollow.data = Uri.parse("http://instagram.com/_u/pathtofbla")
            startActivity(instagramFollow)
        }

        twitterFollowButton.setOnClickListener {
            val twitterFollow = Intent()
            twitterFollow.action = Intent.ACTION_VIEW
            twitterFollow.data = Uri.parse("http://twitter.com/pathtofbla")
            startActivity(twitterFollow)
        }
        emailButton.setOnClickListener {
            val email = Intent()
            email.action = Intent.ACTION_SENDTO
            email.type = "text/plain"
            email.putExtra(Intent.EXTRA_EMAIL, "pathtofbla@gmail.com")
            startActivity(email)
        }
        termsOfServiceDownload.setOnClickListener {
            val pdfIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://firebasestorage.googleapis.com/v0/b/pathtofbla-48419.appspot.com/o/Path_to_FBLA_TERMS_OF_SERVICE.pdf?alt=media&token=340ff012-dad5-44fc-9d96-ece6004957b7")
            )
            startActivity(pdfIntent)
        }
        statementOfAssuranceDownload.setOnClickListener {
            val pdfIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://firebasestorage.googleapis.com/v0/b/pathtofbla-48419.appspot.com/o/Assurance_Statement_Pinnacle_Ninad_Kulkarni_Bader_Alrifai_Mobile_Application_Development.pdf?alt=media&token=f6c97c4b-26ee-4f4a-ac8a-1b723a448557")
            )
            startActivity(pdfIntent)
        }
    }

    //helps to create an instance of about fragment in the main activity
    companion object {
        @JvmStatic
        fun newInstance() =
            AboutFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}