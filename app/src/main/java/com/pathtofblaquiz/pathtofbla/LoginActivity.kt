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
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Login Activity handles all the user login related activities
 *
 * The class first verifies the user credentials
 * Then it allows them to log in
 * The users can also reset their password here
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //these are all the functions that are called when the layout is created
        animations()
        checkInternetConnection()
        login()
        changePassword()
        changeActivityToSignup()
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
     * this function handles the user login
     * first it gets the user input of email and password
     * then it verifies the user's credentials in Firebase Auth using the email and password
     * and finally upon its success it logs them in
     */
    private fun login() {
        loginButton.setOnClickListener {

            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()

            /*
            Checks if all the fields are filled in
            If they are not an error message is displayed to the user reminding them to do so
             */
            if (emailTextView.text.toString().isEmpty()) {
                emailTextInputLayout.error = "Please enter a valid email address"
                return@setOnClickListener
            } else {
                emailTextInputLayout.error = null
            }

            if (passwordTextView.text.toString().isEmpty()) {
                passwordTextInputLayout.error = "Please enter your password"
                return@setOnClickListener
            } else {
                passwordTextInputLayout.error = null
            }

            //verifies the user credentials and switches the activity to Dashboard
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        emailTextView.text?.clear()
                        passwordTextView.text?.clear()

                        val switchToDashboard = Intent(this, MainActivity::class.java)
                        switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(switchToDashboard)
                    } else {
                        return@addOnCompleteListener
                    }
                }
                //this message is showed when the user's credentials are not correct
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to Log In. ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    /**
     * this function takes the user's email and allows them to change their password through Firebase Auth
     */
    private fun changePassword() {
        forgotPassword.setOnClickListener {
            val email = emailTextView.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address in order to reset password", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            //uses Firebase Auth's feature of resetting password
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Check your email for instructions to change the password",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    /**
     * This function switches the activity to signup activity
     */
    private fun changeActivityToSignup() {
        accountCheck.setOnClickListener {
            val switchToSignup = Intent(this, SignupActivity::class.java)
            switchToSignup.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToSignup)
        }
    }
}
