package com.pathtofblaquiz.pathtofbla

import android.app.Activity
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_bug_report.*
import java.util.*

/**
 * Bug Report Fragment handles the reporting of bugs by the users
 * It has a simple form with features to enter a title, description, the page that bug exists on and a screenshot of the bug
 */
class BugReportFragment : Fragment() {

    //this list contains the options for spinner used to select the page
    val pageOptions = arrayOf(
        "Select a page",
        "Dashboard Page",
        "Questions Page",
        "Leaderboard Page",
        "Achievements Page",
        "Statistics Page",
        "Resources Page",
        "About Page",
        "Bug Report Page",
        "Not a page specific issue"
    )

    //this string is initialized in the getSpinnerValue and used in the submitReport function
    lateinit var selectedPage: String

    //this uri is initialized in attachScreenshot function and used in submitReport function
    private var uri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bug_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //these functions are called when the view is created
        checkInternetConnection()
        getSpinnerValue()
        attachScreenshot()
        submitReport()
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

    /**
     * this function simply creates a spinner/dropdown and gets the value selected by user
     */
    private fun getSpinnerValue() {
        pageSpinner.adapter = ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, pageOptions)

        pageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedPage = pageOptions[p2]
            }

        }
    }

    /**
     * this function allows users to attach an optional screenshot to further illustrate their bug issue
     */
    private fun attachScreenshot() {
        attachScreenshot.setOnClickListener {
            val pickPhoto = Intent(Intent.ACTION_PICK)
            pickPhoto.type = "image/*"
            startActivityForResult(pickPhoto, 0)
        }

        selectedFileChip.setOnCloseIconClickListener {
            uri = null
            selectedFileChip.visibility = View.INVISIBLE
        }
    }

    /**
     * this function makes the chip visible when the user has attached a screenshot
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {

            uri = data.data
            selectedFileChip.visibility = View.VISIBLE
        }
    }

    /**
     * this is the main function of this fragment and it takes the user input and sends the bug report to Firebase Database
     */
    private fun submitReport() {
        submitReportButton.setOnClickListener {
            //following block of code gets the values of user input and id there is no input in te required fields a warning Toast is showed
            if (titleText.text.toString().isEmpty()) {
                textInputLayout.error = "Please enter a title"
                return@setOnClickListener
            } else {
                textInputLayout.error = null
            }
            if (descriptionText.text.toString().isEmpty()) {
                textInputLayout1.error = "Please enter a description"
                return@setOnClickListener
            } else {
                textInputLayout1.error = null
            }
            if (selectedPage == "Select a page") {
                spinnerHelperText.text = "Please select a page"
                spinnerHelperText.setTextColor(resources.getColor(R.color.dark_red))
                return@setOnClickListener
            } else {
                spinnerHelperText.text = "*required"
                spinnerHelperText.setTextColor(resources.getColor(R.color.gray))
            }

            if (uri != null) {
                val filename = UUID.randomUUID().toString()
                val storageRef = FirebaseStorage.getInstance().getReference("/bugScreenshots/$filename")

                storageRef.putFile(uri!!)
            }

            val uuid = UUID.randomUUID().toString()
            val dbRef = FirebaseDatabase.getInstance().getReference("/bugReports/$uuid")
            val bug = BugReports(titleText.text.toString(), descriptionText.text.toString(), selectedPage)
            dbRef.setValue(bug)
                .addOnSuccessListener {
                    titleText.text?.clear()
                    descriptionText.text?.clear()
                    selectedPage = pageOptions[0]
                    pageSpinner.setSelection(0)
                    uri = null
                    selectedFileChip.visibility = View.INVISIBLE
                    Toast.makeText(this.context, "Bug report has been submitted successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this.context, "Bug report did not submit. Please try again", Toast.LENGTH_LONG)
                        .show()
                }
        }
    }

    //helps to create an instance of bug report fragment in the main activity
    companion object {
        @JvmStatic
        fun newInstance() =
            BugReportFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

class BugReports(val title: String, val description: String, val page: String)