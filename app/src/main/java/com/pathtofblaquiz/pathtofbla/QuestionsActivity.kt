package com.pathtofblaquiz.pathtofbla

import android.content.DialogInterface
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_questions.*
import java.util.*

/**
 * Questions Activity handles the the questions that are displayed to th eusers
 *
 * It first queries the questions from the database according to which category the users have chosen
 * 5 questions are selected at random from the queried question bank in the Questions class
 * then it iterates through the questions as the users answer them and when they are finished it updates the database with their progress and switches to the result activity
 *
 * Every right answer plays a cheerful sound and wrong answer plays a error sound
 */
class QuestionsActivity : AppCompatActivity() {

    var right = 0
    var questionTimeLeft = (MainActivity.timerHardness * 1000).toLong()
    var totalTimeLeft = 0L
    lateinit var timer: CountDownTimer
    val uid = FirebaseAuth.getInstance().uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        exitButton.setOnClickListener {
            exitConfirmation()
        }

        val chosenCategory = intent.getStringExtra(CategoryViewHolder.CATEGORY_TITLE_KEY)
        continueButton.visibility = View.INVISIBLE

        val quiz = Quiz()

        /*
            if the student has chosen a category, then the getQuestions function will be called
            otherwise, the quick quiz function will be called
         */
        if (chosenCategory != null) {
            category.text = chosenCategory
            quiz.getQuestions(chosenCategory, MainActivity.numberOfQuestions, object : FirebaseQuestionCallback {
                override fun onQuestionCallback(list: List<Question>) {
                    for (i in list) {
                        i.choices = mutableListOf(i.answer, i.choice, i.choice1, i.choice2)
                        Collections.shuffle(i.choices) //the choices are shuffled so that the user has a different experience on each run
                    }
                    var questionNumber = 0
                    iterateQuestions(list, questionNumber)
                    checkAnswer(list, questionNumber)

                    /*
                        only appears after a question has been answered
                        if there are questions left, then the button displays the next question
                        otherwise, it takes the user to the score screen
                     */
                    continueButton.setOnClickListener {
                        questionNumber++
                        quiz.correct = right
                        if (questionNumber < list.size) {
                            iterateQuestions(list, questionNumber)
                            checkAnswer(list, questionNumber)
                        } else {
                            val score = quiz.calculateScore(totalTimeLeft)
                            updateDatabase(score, chosenCategory)
                        }
                        continueButton.visibility = View.INVISIBLE
                        choice0.backgroundTintList = resources.getColorStateList(R.color.white)
                        choice1.backgroundTintList = resources.getColorStateList(R.color.white)
                        choice2.backgroundTintList = resources.getColorStateList(R.color.white)
                        choice3.backgroundTintList = resources.getColorStateList(R.color.white)
                    }
                }
            })
        } else {
            category.text = "Quick Quiz"
            quiz.getQuickQuestions(object : FirebaseQuestionCallback {
                override fun onQuestionCallback(list: List<Question>) {
                    for (i in list) {
                        i.choices = mutableListOf(i.answer, i.choice, i.choice1, i.choice2)
                        Collections.shuffle(i.choices) //the choices are shuffled so that the user has a different experience on each run
                    }
                    var questionNumber = 0
                    iterateQuestions(list, questionNumber)
                    checkAnswer(list, questionNumber)

                    /*
                       only appears after a question has been answered
                       if there are questions left, then the button displays the next question
                       otherwise, it takes the user to the score screen
                    */
                    continueButton.setOnClickListener {
                        questionNumber++
                        quiz.correct = right
                        if (questionNumber < list.size) {
                            iterateQuestions(list, questionNumber)
                            checkAnswer(list, questionNumber)
                        } else {
                            val score = quiz.calculateScore(totalTimeLeft)
                            updateDatabase(score, "miscellaneous")
                        }
                        continueButton.visibility = View.INVISIBLE
                        choice0.backgroundTintList = resources.getColorStateList(R.color.white)
                        choice1.backgroundTintList = resources.getColorStateList(R.color.white)
                        choice2.backgroundTintList = resources.getColorStateList(R.color.white)
                        choice3.backgroundTintList = resources.getColorStateList(R.color.white)
                    }
                }

            })
        }
    }

    /**
     * This function overrides the android back button and asks users for a confirmation if they want to quit teh quiz
     */
    override fun onBackPressed() {
        exitConfirmation()
    }

    /**
     * There is no way to override the home button on android so the next best option is for us to safely quit the quiz for users.
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            val switchToDashboard = Intent(this, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        }
    }

    /**
     * this function is used to iterate through the questions in a quiz
     * it updates the question fields, such as the question title, for the next question
     * it also resets the timer
     *
     * @param list - the question list is needed to update the values of the next question
     * @param questionNumber - the question number is needed in order to know which question to check in the list and to check if this is the last question or there's more
     */
    private fun iterateQuestions(list: List<Question>, questionNumber: Int) {

        //updates the question title, choices, etc. values to the new question
        questionsText.text = list[questionNumber].question
        val choicesTextView = list[questionNumber].choices
        choice0.text = choicesTextView[0]
        choice1.text = choicesTextView[1]
        choice2.text = choicesTextView[2]
        choice3.text = choicesTextView[3]
        questionNumberSmall.text = (questionNumber + 1).toString() + "/" + list.size
        questionNumberBig.text = "Question " + (questionNumber + 1).toString()

        //resets the timer for the question
        timer = object : CountDownTimer((MainActivity.timerHardness * 1000).toLong(), 10) {
            override fun onTick(p0: Long) {
                timerProgressBar.max =
                    MainActivity.timerHardness * 1000 //question timer is based upon the difficulty chosen
                timerProgressBar.progress = p0.toInt()
                questionTimeLeft = p0
            }

            /*
                if the time runs out, then the continue button is made visible and all the choices are colored red
                The correct answer is later made teal
             */
            override fun onFinish() {
                choice0.backgroundTintList = resources.getColorStateList(R.color.google_red)
                choice1.backgroundTintList = resources.getColorStateList(R.color.google_red)
                choice2.backgroundTintList = resources.getColorStateList(R.color.google_red)
                choice3.backgroundTintList = resources.getColorStateList(R.color.google_red)

                buttonModify(list, questionNumber)

                continueButton.visibility = View.VISIBLE
            }
        }
        timer.start()
    }

    /**
     * This function after the user has chosen their answer
     * checks if the user chose the correct answer and color the choices appropriately
     *
     * @param list - the list is needed to check the user choice with the answer
     * @param questionNumber - the question number is needed in order to know which question to check in the list
     */
    private fun checkAnswer(list: List<Question>, questionNumber: Int) {
        val mediaPlayerCorrect = MediaPlayer.create(this, R.raw.correct) //the sound for a correct answer
        val mediaPlayerIncorrect = MediaPlayer.create(this, R.raw.incorrect) //the sound for an incorrect answer

        //the function has a set on click listener for each choice
        choice0.setOnClickListener {
            if (list[questionNumber].answer == choice0.text.toString()) {
                choice0.backgroundTintList = resources.getColorStateList(R.color.teal)
                mediaPlayerCorrect.start()
                right++
            } else {
                choice0.backgroundTintList = resources.getColorStateList(R.color.google_red)
                mediaPlayerIncorrect.start()
                questionTimeLeft =
                    0L //the timer is set to zero as to not count towards the score (The score equation takes into account the time left for each correct answer)
            }
            buttonModify(list, questionNumber)
            continueButton.visibility =
                View.VISIBLE //the continue button is only made visible after the question is answered
        }
        choice1.setOnClickListener {
            if (list[questionNumber].answer == choice1.text.toString()) {
                choice1.backgroundTintList = resources.getColorStateList(R.color.teal)
                mediaPlayerCorrect.start()
                right++
            } else {
                choice1.backgroundTintList = resources.getColorStateList(R.color.google_red)
                mediaPlayerIncorrect.start()
                questionTimeLeft =
                    0L //the timer is set to zero as to not count towards the score (The score equation takes into account the time left for each correct answer)
            }
            buttonModify(list, questionNumber)
            continueButton.visibility =
                View.VISIBLE //the continue button is only made visible after the question is answered
        }
        choice2.setOnClickListener {
            if (list[questionNumber].answer == choice2.text.toString()) {
                choice2.backgroundTintList = resources.getColorStateList(R.color.teal)
                mediaPlayerCorrect.start()
                right++
            } else {
                choice2.backgroundTintList = resources.getColorStateList(R.color.google_red)
                mediaPlayerIncorrect.start()
                questionTimeLeft =
                    0L //the timer is set to zero as to not count towards the score (The score equation takes into account the time left for each correct answer)
            }
            buttonModify(list, questionNumber)
            continueButton.visibility =
                View.VISIBLE //the continue button is only made visible after the question is answered
        }
        choice3.setOnClickListener {
            if (list[questionNumber].answer == choice3.text.toString()) {
                choice3.backgroundTintList = resources.getColorStateList(R.color.teal)
                mediaPlayerCorrect.start()
                right++
            } else {
                choice3.backgroundTintList = resources.getColorStateList(R.color.google_red)
                mediaPlayerIncorrect.start()
                questionTimeLeft =
                    0L //the timer is set to zero as to not count towards the score (The score equation takes into account the time left for each correct answer)
            }
            buttonModify(list, questionNumber)
            continueButton.visibility =
                View.VISIBLE //the continue button is only made visible after the question is answered
        }
    }

    /**
     * disables all the buttons, changes the correct choice to teal,and resets the timer
     * @param list - the list is needed to in order to know which choice is the correct answer
     * @param questionNumber - the question number is needed in order to know which question to check in the list
     */
    private fun buttonModify(list: List<Question>, questionNumber: Int) {

        //disables all the buttons
        choice0.isClickable = false
        choice1.isClickable = false
        choice2.isClickable = false
        choice3.isClickable = false

        if (list[questionNumber].answer == choice0.text.toString()) {
            choice0.backgroundTintList = resources.getColorStateList(R.color.teal)
        }
        if (list[questionNumber].answer == choice1.text.toString()) {
            choice1.backgroundTintList = resources.getColorStateList(R.color.teal)
        }
        if (list[questionNumber].answer == choice2.text.toString()) {
            choice2.backgroundTintList = resources.getColorStateList(R.color.teal)
        }
        if (list[questionNumber].answer == choice3.text.toString()) {
            choice3.backgroundTintList = resources.getColorStateList(R.color.teal)
        }

        totalTimeLeft += questionTimeLeft //the totalTimeLeft will be used to calculate the score of the quiz
        questionTimeLeft = 0L
        timer.cancel()
    }

    /**
     * This function is called after the quiz is taken
     *
     * it add the earned score to the total points and category points in database
     * it also increments the quizzes taken by 1 in database
     *
     * it checks if the user has leveled up from the earned xp, and if so, it updates the new level and xp in the database
     * it also checks if the user has earned any achievements and updates those in the databse
     *
     * @param score - the score is used to calculate the current xp and in updating the score fields in database
     * @param chosenCategory - the chosen category is needed to know which category points field needs to be updated
     */
    private fun updateDatabase(score: Int, chosenCategory: String) {
        val categoryRef = categoryReference(chosenCategory)
        val userRef = FirebaseDatabase.getInstance().reference
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val quizzes =
                    p0.child("users/$uid/quizzes").value.toString().toInt() + 1 //queries the current quiz number of the user and increments it
                val points =
                    p0.child("users/$uid/totalPoints").value.toString().toInt() //queries the current points of the user
                val categoryPoints = p0.child("CategoryPoints/$uid/$categoryRef").value.toString()
                    .toInt() //queries the current category points of the user

                var level = p0.child("users/$uid/level").value.toString()
                    .toDouble() //queries the current level of the user to calculate how much XP is needed
                var currentXP =
                    p0.child("users/$uid/xp").value.toString().toDouble() + score //queries the current XP of the user to see if the user is ready to level up
                var neededXP = level * 100 //calculates how much XP is needede to level up

                //a while loop instead of an if statement in case the user has enough XP to level up more than once
                while (currentXP >= neededXP) {
                    level++
                    currentXP -= neededXP

                    userRef.child("users/$uid/level").setValue(level.toInt()) //update the user's level in db
                    userRef.child("users/$uid/xp").setValue(currentXP.toInt()) //update the XP of the user in db

                    neededXP =
                        level * 100 //recalculates needed XP to level up again, because the user's level has increased
                }

                userRef.child("users/$uid/quizzes").setValue(quizzes) //updates the quizzes taken by the user
                userRef.child("users/$uid/totalPoints")
                    .setValue(score + points) //adds the earned score to the total points of the user
                userRef.child("CategoryPoints/$uid/$categoryRef")
                    .setValue(score + categoryPoints) //adds the earned score to the category points of the user

                //checks if any level up achievements have been earned
                getLevelUpAchievements(object : FirebaseAchievementsCallback {

                    override fun onAchievementsCallback(achievementList: List<String>) {
                        achievementEarned(level.toInt(), achievementList)
                    }

                })

                //checks if any quiz achievements have been earned
                getQuizAchievements(object : FirebaseAchievementsCallback {
                    override fun onAchievementsCallback(achievementList: List<String>) {
                        Collections.reverse(achievementList) //the quiz achievements are ordered from hardest to easiest by firebase database, therefore the list is reversed to put the order of achievments in order
                        achievementEarned(quizzes, achievementList)

                    }

                })

                //checks if any score achievements have been earned
                getScoreAchievements(object : FirebaseAchievementsCallback {
                    override fun onAchievementsCallback(achievementList: List<String>) {
                        achievementEarned(score, achievementList)
                    }

                })

                //moves the scene to the results page
                val switchToResult = Intent(this@QuestionsActivity, ResultActivity()::class.java)
                switchToResult.putExtra("SCORE_EARNED", score.toString())
                switchToResult.putExtra("CATEGORY", chosenCategory)
                switchToResult.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(switchToResult)
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@QuestionsActivity,
                    "Error occurred while connecting to the Database",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * This function is called before calling achievementEarned
     * Queries level up achievements that the user has yet to earn and adds them to a list
     * the list is passed to the achievementEarned
     */
    private fun getLevelUpAchievements(firebaseAchievementsCallback: FirebaseAchievementsCallback) {
        val levelUpAchievementList =
            mutableListOf<String>()//clears the list of any data it might have before when checking other achievements

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val achievementsRef =
            FirebaseDatabase.getInstance().getReference("UserAchievements/$uid/").orderByValue()
                .equalTo(false) //creates a database reference for all achievements that the user has yet to earn
        achievementsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children) {
                    val query = child.key.toString() //queries the name of the achievements

                    // add the query to the list only if it's a level achievement
                    if (query.contains("level")) {
                        levelUpAchievementList.add(query)
                    }
                }
                firebaseAchievementsCallback.onAchievementsCallback(levelUpAchievementList) //passes the achievement list to its callback method to ensure the list is only called after it has been fully queried
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@QuestionsActivity,
                    "Error occurred while connecting to the Database",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * This function is called before calling achievementEarned
     * Queries quiz achievements that the user has yet to earn and adds them to a list
     * the list is passed to the achievementEarned
     */
    private fun getQuizAchievements(firebaseAchievementsCallback: FirebaseAchievementsCallback) {
        val quizAchievementList =
            mutableListOf<String>() //clears the list of any data it might have before when checking other achievements

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val achievementsRef =
            FirebaseDatabase.getInstance().getReference("UserAchievements/$uid/").orderByValue()
                .equalTo(false) //creates a database reference for all achievements that the user has yet to earn
        achievementsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children) {
                    val query = child.key.toString() //queries the name of the achievements

                    // add the query to the list only if it's a quiz achievement
                    if (query.contains("Quizzes")) {
                        quizAchievementList.add(query)
                    }
                }
                firebaseAchievementsCallback.onAchievementsCallback(quizAchievementList) //passes the achievement list to its callback method to ensure the list is only called after it has been fully queried
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@QuestionsActivity,
                    "Error occurred while connecting to the Database",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * This function is called before calling achievementEarned
     * Queries score achievements that the user has yet to earn and adds them to a list
     * the list is passed to the achievementEarned
     */
    private fun getScoreAchievements(firebaseAchievementsCallback: FirebaseAchievementsCallback) {

        val scoreAchievementList =
            mutableListOf<String>() //clears the list of any data it might have before when checking other achievements

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val achievementsRef =
            FirebaseDatabase.getInstance().getReference("UserAchievements/$uid/").orderByValue()
                .equalTo(false) //creates a database reference for all achievements that the user has yet to earn
        achievementsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (child in p0.children) {
                    val query = child.key.toString() //queries the name of the achievements

                    // add the query to the list only if it's a points achievement
                    if (query.contains("Points")) {
                        scoreAchievementList.add(query)
                    }
                }
                firebaseAchievementsCallback.onAchievementsCallback(scoreAchievementList) //passes the achievement list to its callback method to ensure the list is only called after it has been fully queried
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@QuestionsActivity,
                    "Error occurred while connecting to the Database",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    /**
     * This function is called after a list has been queried using a get achievements function
     * In order to avoid any errors with calling the list before it's fully queried, this function should be called inside the onAchievementsCallback
     *
     * The function creates achievement objects using the list
     * Using that object, it checks if the user has met the requirements
     * If so, it marks the achievements as true and rewards the user with the proper xp
     *
     *@param userData - the data being compared with the achievements' requirement. For example, the parameter could be the user's level or their score
     */
    private fun achievementEarned(userData: Int, incompleteAchievementList: List<String>) {
        /*
            Firebase dB orders the achievements from highest to lowest, so the list is reversed to check the achievements in chronological order.
            This saves time through the loop in this function.
            If an achievement to score 500 is unmet, then there is no need to check for the achievement to score 1000
         */
        val dbRef = FirebaseDatabase.getInstance().reference //creates a database reference to the whole database
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                //the loop iterates through every achievement in the list
                for (achievementTitle in incompleteAchievementList) {
                    val achievement = p0.child("Achievements/$achievementTitle")
                        .getValue(Achievement::class.java) //creates an achievement class by using the achievements title in the incompleteAchievementList
                    if (userData >= achievement!!.requirement) {
                        val xp = p0.child("users/$uid/xp").value.toString().toInt() //queries the user's xp
                        val newxp = xp + achievement.xp //adds the achievement reward to the current xp
                        dbRef.child("users/$uid/xp").setValue(newxp) //sets the new xp in the database
                        dbRef.child("UserAchievements/$uid/$achievementTitle")
                            .setValue(true) //sets the achievement as true in the database for the user

                        //because the user has earned xp from the achievement reward, the following code checks if the user has leveled up
                        var level = p0.child("users/$uid/level").value.toString()
                            .toDouble() //queries the current level of the user to calculate how much XP is needed
                        var currentXP = newxp.toDouble()
                        var neededXP = level * 100 //calculates how much xp is need to level up

                        //a while loop instead of an if statement in case the user has enough XP to level up more than once
                        while (currentXP >= neededXP) {
                            level++
                            currentXP -= neededXP

                            dbRef.child("users/$uid/level").setValue(level.toInt()) //update the user's level in db
                            dbRef.child("users/$uid/xp").setValue(currentXP.toInt()) //update the XP of the user in db

                            neededXP =
                                level * 100 //recalculates needed XP to level up again, because the user's level has increased
                        }
                    } else {
                        break //if the achievement requirement was not met, then there is no need to check the other achievement since they have higher requirements.
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@QuestionsActivity,
                    "Error occurred while connecting to the Database",
                    Toast.LENGTH_LONG
                ).show()
            }

        })
    }

    /**
     * a confirmation that the user wants to exit the quiz
     * it's used in case the user accidentely clicked the exit button and doesn't to lose their score
     */
    private fun exitConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Exit")
        builder.setMessage("Are you sure you want to exit the quiz?")
        builder.setPositiveButton("Exit") { dialogInterface: DialogInterface, i: Int ->
            val switchToDashboard = Intent(this, MainActivity::class.java)
            switchToDashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(switchToDashboard)
        }
        builder.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int -> }
        builder.show()
    }

    /**
     * the fields in database have spaces in their names , so in order to use them as variables, they are converted to camelCase
     * They have spaces in database because they are queired as so in the dashboard view
     */
    private fun categoryReference(chosenCategory: String): String {
        lateinit var categoryRef: String
        when (chosenCategory) {
            "FBLA Organization" -> {
                categoryRef = "organizationPoints"
            }
            "Competitive Events" -> {
                categoryRef = "eventsPoints"
            }
            "Business Skills" -> {
                categoryRef = "skillsPoints"
            }
            "National Officers" -> {
                categoryRef = "officersPoints"
            }
            "Parliamentary Procedure" -> {
                categoryRef = "procedurePoints"
            }
            "National Conference" -> {
                categoryRef = "conferencePoints"
            }
            "FBLA History" -> {
                categoryRef = "historyPoints"
            }
            "FBLA Bylaws" -> {
                categoryRef = "bylawsPoints"
            }
            "Creed, National Goals, and Ethics" -> {
                categoryRef = "creedPoints"
            }
            "Community Service" -> {
                categoryRef = "servicePoints"
            }
            "miscellaneous" -> {
                categoryRef = "miscellaneousPoints"
            }
        }
        return categoryRef
    }
}
