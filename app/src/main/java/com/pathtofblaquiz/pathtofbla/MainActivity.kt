package com.pathtofblaquiz.pathtofbla

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.FragmentTransaction
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton

/**
 * Main Activity handles all the page fragments in the app. This is the super class that contains the logic to switch between the pages through the navigation drawer
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //these fragments are declared before the activity is created because they are used in the onNavigationItemSelected function
    lateinit var dashboardFragment: DashboardFragment
    lateinit var statisticsFragment: StatisticsFragment
    lateinit var leaderboardFragment: LeaderboardFragment
    lateinit var resourcesFragment: ResourcesFragment
    lateinit var achievementsFragment: AchievementsFragment
    lateinit var aboutFragment: AboutFragment
    lateinit var bugReportFragment: BugReportFragment

    //the setting values are static because they are global settings and can be applied to all the categories
    companion object {
        var numberOfQuestions = 5
        var timerHardness = 30
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        //these are all the functions that are called when the layout is created
        checkInternetConnection()
        updateNavHeader()
        settingsDialog()
        signout()

        //instantiates all the Fragments
        dashboardFragment = DashboardFragment.newInstance()
        statisticsFragment = StatisticsFragment.newInstance()
        leaderboardFragment = LeaderboardFragment.newInstance()
        resourcesFragment = ResourcesFragment.newInstance()
        achievementsFragment = AchievementsFragment.newInstance()
        aboutFragment = AboutFragment.newInstance()
        bugReportFragment = BugReportFragment.newInstance()

        //sets the default fragment to dashboard fragment as the activity launches
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.switchContainer, dashboardFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }

    /**
     * This function checks if the user's device is connected to the internet to access Firebase Database
     * If the user is not connected to the internet then an alert dialog box appears reminding them to do so
     */
    private fun checkInternetConnection() {
        val connectivityManager = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        //if there is no connection then it shows the alert and if there is a connection nothing happens
        if (networkInfo != null && networkInfo.isConnected) {
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Connection Alert")
            builder.setMessage("Please connect to the internet to use this app!")
            builder.setPositiveButton("Retry") { dialogInterface: DialogInterface, i: Int ->
                checkInternetConnection() //If the user presses retry then the function is called again
            }
            builder.show()
        }
    }

    /**
     * This function updates the user's level progress bar in the navigation drawer header
     * It queries the database for the current level and sets the progress bar programmatically
     */
    private fun updateNavHeader() {
        val uid = FirebaseAuth.getInstance().uid
        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val level = p0.child("level").value.toString().toDouble()
                val currentXP = p0.child("xp").value.toString().toDouble()

                //the formula that calculates the XP needed to level up
                val neededXP = level * 100

                //sets the progress bar and progress text programmatically
                levelProgressBar.max = (neededXP + currentXP).toInt()
                levelProgressBar.progress = currentXP.toInt()
                currentLevelText.text = "Level ${level.toInt()}"
                levelProgressText.text = "${neededXP.toInt()} points to next level"
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error occurred while connecting to the Database", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    /**
     * This function creates and shows the settings option for the users
     * Settings include Number of questions from the range 5-15 questions
     *                  Timer for questions from the range 10-30 seconds
     */
    private fun settingsDialog() {
        settingsButton.setOnClickListener {
            val settingsDialog = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.settings_dialog, null)

            val questionsSeekBar = dialogView.findViewById<SeekBar>(R.id.questionsSeekBar)
            val questionNumberText = dialogView.findViewById<TextView>(R.id.questionNumberText)
            val easyButton = dialogView.findViewById<MaterialButton>(R.id.easyButton)
            val mediumButton = dialogView.findViewById<MaterialButton>(R.id.mediumButton)
            val hardButton = dialogView.findViewById<MaterialButton>(R.id.hardButton)

            questionsSeekBar.progress = numberOfQuestions - 5
            questionNumberText.text = numberOfQuestions.toString()

            when (timerHardness) {
                30 -> {
                    easyButton.strokeColor = resources.getColorStateList(R.color.tealDark)
                    mediumButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                    hardButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                }
                20 -> {
                    easyButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                    mediumButton.strokeColor = resources.getColorStateList(R.color.tealDark)
                    hardButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                }
                10 -> {
                    easyButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                    mediumButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                    hardButton.strokeColor = resources.getColorStateList(R.color.tealDark)
                }
            }

            settingsDialog.setView(dialogView)
            settingsDialog.setPositiveButton("Close", { dialogInterface: DialogInterface, i: Int -> })
            settingsDialog.show()

            questionsSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    numberOfQuestions = p1 + 5
                    questionNumberText.text = numberOfQuestions.toString()
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })

            easyButton.setOnClickListener {
                timerHardness = 30
                easyButton.strokeColor = resources.getColorStateList(R.color.tealDark)
                mediumButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                hardButton.strokeColor = resources.getColorStateList(R.color.light_gray)
            }
            mediumButton.setOnClickListener {
                timerHardness = 20
                easyButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                mediumButton.strokeColor = resources.getColorStateList(R.color.tealDark)
                hardButton.strokeColor = resources.getColorStateList(R.color.light_gray)
            }
            hardButton.setOnClickListener {
                timerHardness = 10
                easyButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                mediumButton.strokeColor = resources.getColorStateList(R.color.light_gray)
                hardButton.strokeColor = resources.getColorStateList(R.color.tealDark)
            }
        }
    }

    /**
     * This function allows users to navigate between fragments through the navigation drawer
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_dashboard -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, dashboardFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                settingsButton.visibility = View.VISIBLE
                toolbarTitle.visibility = View.GONE
            }
            R.id.nav_statistics -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, statisticsFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                toolbar.title = "Statistics"
                settingsButton.visibility = View.GONE
                toolbarTitle.text = "Statistics"
                toolbarTitle.visibility = View.VISIBLE
            }
            R.id.nav_leaderboard -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, leaderboardFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                toolbar.title = "Leaderboard"
                settingsButton.visibility = View.GONE
                toolbarTitle.text = "Leaderboard"
                toolbarTitle.visibility = View.VISIBLE
            }
            R.id.nav_resources -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, resourcesFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                toolbar.title = "Resources"
                settingsButton.visibility = View.GONE
                toolbarTitle.text = "Resources"
                toolbarTitle.visibility = View.VISIBLE
            }
            R.id.nav_achievements -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, achievementsFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                toolbar.title = "Achievements"
                settingsButton.visibility = View.GONE
                toolbarTitle.text = "Achivements"
                toolbarTitle.visibility = View.VISIBLE
            }
            R.id.nav_about -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, aboutFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                toolbar.title = "About Us"
                settingsButton.visibility = View.GONE
                toolbarTitle.visibility = View.GONE
            }
            R.id.nav_bug_report -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.switchContainer, bugReportFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
                toolbar.title = "Bug Report"
                settingsButton.visibility = View.GONE
                toolbarTitle.text = "Bug Report"
                toolbarTitle.visibility = View.VISIBLE
            }
            R.id.nav_share -> {
                val appId = applicationContext.packageName
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    "Download Path to FBLA App Today!\n\nhttps://play.google.com/store/apps/details?id=$appId"
                )
                startActivity(Intent.createChooser(share, "Share app using"))
            }
            R.id.nav_rate_us -> {
                val appId = applicationContext.packageName
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * This function allows users to safely sign out by disconnecting from the Firebase auth and saving their progress so no data is lost
     */
    private fun signout() {
        signoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val switchToSignup = Intent(this, SignupActivity::class.java)
            switchToSignup.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToSignup)
        }
    }

    /**
     * This function allows users to close the navigation drawer by pressing the back button
     */
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
