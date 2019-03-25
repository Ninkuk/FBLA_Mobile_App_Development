package com.pathtofblaquiz.pathtofbla

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*


/**
 * Signup Activity handles all the user signup related activities
 *
 * The class first checks if the user is connected to the internet
 * Then it takes the user input and creates a user profile in the Firebase Database
 */
class SignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //these are all the functions that are called when the layout is created
        changeActivityToDashboard()
        animations()
        checkInternetConnection()
        signup()
        changeActivityToLogin()
    }

    /**
     * This function checks if a user is already logged in and if they are then the signup activity is bypassed and the user goes straight to the Dashboard Activity
     */
    private fun changeActivityToDashboard() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val switchToDashboard = Intent(this, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        } else {
            // No user is signed in
        }
    }

    /**
     * this function initializes the animation when the activity loads
     * the animation makes the layout fade in and move up
     */
    private fun animations() {
        val containerTranslateY = ObjectAnimator.ofFloat(superLinearContainer, "translationY", -100f)
        val containerTranslateAlpha = ObjectAnimator.ofFloat(superLinearContainer, "alpha", 0f, 1f)
        AnimatorSet().apply {
            playTogether(
                containerTranslateY,
                containerTranslateAlpha
            )
            duration = 1200
            start()
        }
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
     * this function handles the user signup
     * first it gets the user input of email, username and password
     * then it creates a user in Firebase Auth using the email and password
     * and finally upon its success it also creates a user in Firebase Database and initializes default values to the user
     */
    private fun signup() {
        signupButton.setOnClickListener {
            val username = usernameTextView.text.toString()
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()

            /*
            Checks if all the fields are filled in
            If they are not a Toast is displayed to the user reminding them to do so
             */
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all the fields to sign up", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            //A new user is created in Firebase Auth using the user inputted email and password
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    //if the user is created in Firebase Auth then it also creates a user object in the Firebase Database
                    if (it.isSuccessful) {
                        val uid = FirebaseAuth.getInstance().uid ?: ""
                        val userRef = FirebaseDatabase.getInstance()
                            .getReference("/users/$uid") //creates a database reference to the user node
                        val achievementsRef = FirebaseDatabase.getInstance()
                            .getReference("/UserAchievements/$uid") //creates a database reference to the user achievements node
                        val categoryPointsRef = FirebaseDatabase.getInstance()
                            .getReference("/CategoryPoints/$uid") //creates a database reference to the user category points node

                        val user = User(uid, username, 0, 1, 0, 0) //creates a User object
                        val userAchievements =
                            UserAchievements( //creates a User Achievement object and sets default values as false
                                level5 = false,
                                level2 = false,
                                take10Quizzes = false,
                                take5Quizzes = false,
                                score500Points = false,
                                score250Points = false
                            )
                        val categoryPoints = CategoryPoints(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

                        achievementsRef.setValue(userAchievements) //adds the User Achievement object to the database
                        categoryPointsRef.setValue(categoryPoints) //adds the user Category Points object to the database
                        userRef.setValue(user) //adds User object to the database
                            .addOnSuccessListener {

                                //New users are shown the intro wizard
                                val switchToWizard = Intent(this, WizardActivity::class.java)
                                switchToWizard.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(switchToWizard)
                            }

                    } else {
                        return@addOnCompleteListener
                    }
                }
                //If no user is created in Firebase Auth then a Toast is shown with the message about why no user was created
                //The user creation fails when user fails to input properly formatted email or password
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to create account. ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    /**
     * This function switches the activity to login activity
     */
    private fun changeActivityToLogin() {
        accountCheck.setOnClickListener {
            val switchToLogin = Intent(this, LoginActivity::class.java)
            startActivity(switchToLogin)
        }
    }
}