package com.pathtofblaquiz.pathtofbla

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_result.*

/**
 * The Result Activity is shown to the users once they finish a quiz
 *
 * It allows them to see their final score and gives them the ability to share that score on various social media platforms
 *
 * It also plays a cheerful soundtrack if the users scored well on the quiz
 */
class ResultActivity : AppCompatActivity() {

    val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val score = intent.getStringExtra("SCORE_EARNED").toInt()
        val category = intent.getStringExtra("CATEGORY")
        pointsEarned.text = "$score Points"

        updateScoreHistory(score.toFloat())
        updateHighScore(score)

        if (score != 0) {
            val mediaPlayerTada = MediaPlayer.create(this, R.raw.tada)
            mediaPlayerTada.start()
        }

        continueToDashboard.setOnClickListener {
            val switchToDashboard = Intent(this, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        }

        twitterShareButton.setOnClickListener {
            val message = "I just earned $score points in $category quiz in Path to FBLA app"

            val appId = applicationContext.packageName
            val url =
                "http://www.twitter.com/intent/tweet?url=https://play.google.com/store/apps/details?id=$appId&text=$message"

            val intentShare = Intent()
            intentShare.action = Intent.ACTION_VIEW
            intentShare.data = Uri.parse(url)
            startActivity(intentShare)
        }

        emailShareButton.setOnClickListener {
            val appId = applicationContext.packageName
            val message =
                "I just earned $score points in $category quiz in Path to FBLA app\n\nhttps://play.google.com/store/apps/details?id=$appId"

            val intentShare = Intent()
            intentShare.action = Intent.ACTION_SEND
            intentShare.putExtra(Intent.EXTRA_TEXT, message)
            intentShare.type = "text/plain"
            startActivity(Intent.createChooser(intentShare, "Share using"))
        }
    }

    /**
     * This function queries and checks the current high score of a user and if the current score is greater than that, it replaces the high score
     */
    private fun updateHighScore(score: Int) {
        val ref = FirebaseDatabase.getInstance()
            .getReference("users/$uid/highScore") //creates a database reference to the user's high score
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var query = p0.value.toString().toInt() //queries the user's high score

                //if the score of the user is higher than their high score, then it updates the high score value in database
                if (score > query) {
                    ref.setValue(score)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

        })
    }

    /**
     * This function updates the user's score history. The new score is added to the databse and the oldest score is removed.
     * @param newScore - the new score is used to append the database with recent scores
     */
    private fun updateScoreHistory(newScore: Float) {
        getScoreHistory(object : FirebaseScoresCallback {
            override fun onScoresCallback(list: List<Float>) {
                val scoreList = list.toMutableList()

                if (scoreList.size < 5) {
                    scoreList.add(newScore)
                } else {
                    scoreList.removeAt(0)
                    scoreList.add(newScore)
                }

                val database = FirebaseDatabase.getInstance().getReference("ScoreHistory/$uid")
                for (i in 0..(scoreList.size - 1)) {
                    val x = i + 1
                    database.child("$x").setValue(scoreList[i])
                }
            }
        })
    }

    /**
     * This function queries the last 5 user scores and puts them in a list to be updated
     */
    private fun getScoreHistory(firebaseScoresCallback: FirebaseScoresCallback) {
        val database = FirebaseDatabase.getInstance().getReference("ScoreHistory/$uid")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var scores: List<Float> = mutableListOf()
                for (postSnapshot in p0.children) {
                    val query = postSnapshot.value.toString().toFloat()
                    scores += query
                }
                firebaseScoresCallback.onScoresCallback(scores)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}