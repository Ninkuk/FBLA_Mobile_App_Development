package com.pathtofblaquiz.pathtofbla

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_resources.*

/**
 * Resources Fragment shows a list of all the categories that are available for users to study from
 */
class ResourcesFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resources, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //this is the function that is called when the layout is created
        checkInternetConnection()

        recyclerViewResources.layoutManager = LinearLayoutManager(this.context)
        val adapterResources = ResourcesAdapter()
        recyclerViewResources.adapter = adapterResources
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

    companion object {
        @JvmStatic
        fun newInstance() =
            ResourcesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}