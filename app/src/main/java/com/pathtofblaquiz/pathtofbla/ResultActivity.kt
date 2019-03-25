package com.pathtofblaquiz.pathtofbla

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_result.*

/**
 * The Result Activity is shown to the users once they finish a quiz
 *
 * It allows them to see their final score and gives them the ability to share that score on various social media platforms
 *
 * It also plays a cheerful soundtrack if the users scored well on the quiz
 */
class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val points = intent.getStringExtra("POINTS_EARNED").toString()
        val category = intent.getStringExtra("CATEGORY")
        pointsEarned.text = "$points Points"

        if (points.toInt() != 0) {
            val mediaPlayerTada = MediaPlayer.create(this, R.raw.tada)
            mediaPlayerTada.start()
        }

        continueToDashboard.setOnClickListener {
            val switchToDashboard = Intent(this, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        }

        twitterShareButton.setOnClickListener {
            val message = "I just earned $points points in $category quiz in Path to FBLA app"

            val appId = applicationContext.packageName
            val url = "http://www.twitter.com/intent/tweet?url=https://play.google.com/store/apps/details?id=$appId&text=$message"

            val intentShare = Intent()
            intentShare.action = Intent.ACTION_VIEW
            intentShare.data = Uri.parse(url)
            startActivity(intentShare)
        }

        emailShareButton.setOnClickListener {
            val appId = applicationContext.packageName
            val message = "I just earned $points points in $category quiz in Path to FBLA app\n\nhttps://play.google.com/store/apps/details?id=$appId"

            val intentShare = Intent()
            intentShare.action = Intent.ACTION_SEND
            intentShare.putExtra(Intent.EXTRA_TEXT, message)
            intentShare.type = "text/plain"
            startActivity(Intent.createChooser(intentShare, "Share using"))
        }
    }
}