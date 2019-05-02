package com.pathtofblaquiz.pathtofbla

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_achievements.*

/**
 * Achievement Fragment handles the achievements page layout
 *
 * shows the user's progress in completing the achievement
 * sets the recycler view adapter and manager and creates a progress bar for the achievements
 */
class AchievementsFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_achievements, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets the layout manager of the achievements recycler view. it is a Linear Layout because it is a list
        recyclerViewAchievements.layoutManager = LinearLayoutManager(this.context)

        //this is the function that is called when the layout is created
        checkInternetConnection()
        updateAchievements()
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
     * This function updates the user's achievements and shows the latest data
     */
    private fun updateAchievements() {
        val uid = FirebaseAuth.getInstance().uid
        val achievementsRef = FirebaseDatabase.getInstance().getReference("UserAchievements/$uid")
        achievementsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val achievements = p0.getValue(UserAchievements::class.java)
                val adapterAchievement = AchievementsAdapter(achievements!!)
                recyclerViewAchievements.adapter = adapterAchievement

                var achieved = 0

                if (achievements.score500Points) { achieved++ }
                if (achievements.score250Points) { achieved++ }
                if (achievements.level5) { achieved++ }
                if (achievements.level2) { achieved++ }
                if (achievements.take10Quizzes) { achieved++ }
                if (achievements.take5Quizzes) {achieved++}

                achievedText.text = achieved.toString()
                levelProgressBar.max = 6
                levelProgressBar.progress = achieved
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "Error occurred while connecting to the Database", Toast.LENGTH_LONG).show()
            }
        })
    }

    //helps to create an instance of achievement fragment in the main activity
    companion object {
        @JvmStatic
        fun newInstance() =
            AchievementsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}