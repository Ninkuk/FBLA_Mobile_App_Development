package com.pathtofblaquiz.pathtofbla

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

/**
 * The Question class contains question related fields such as answer and the question
 * it's main use is in the Quiz class, where a List of Question is instantiated from queried data
 */
class Question(var answer: String, var choice: String, var choice1: String, var choice2: String, var question: String) {
    constructor() : this(
        "",
        "",
        "",
        "",
        ""
    ) //This is a no argument constructor which is used while querying the Firebase Database

    /*
        This list contains the answer, choice, choice1, and choice2.
        A list is created in order to shuffle the choices and customize the user experience
        The list is not instantiated in the constructor, because the data hasn't been queried yet
        Instead, the list will be instantiated in the firebaseCallback when the getQuestions method is called
        This ensures that fields have been queried
     */
    var choices: List<String> = mutableListOf()
}

/**
 * An object of this class is instantiated when a user starts a test.
 * The class queries a List of questions for the test and calculates the score when the test is over
 */
class Quiz {
    var correct = 0 //keeps track of how many correct answers the user has answered
    var questions: List<Question> =
        mutableListOf() //holds a list of Question objects that are declared in the getQuestions method

    /**
     * Queries data from the database and creates Question objects from the data
     * Shuffles the questions List in order to give the user a new experience each game
     *
     * @param topic - Used to indicate what category of questions are being queried. Determines tha path of the database reference
     * @param numberOfQuestions - the number of questions the user chooses to be tested on. Determines how many Questions to query from the given topic
     * @param firebaseQuestionCallback - The callback is used due to Firebase's asynchronous behavior. When this method is called, you have to override the firebaseQuestionCallback method in the parameter.
     *                           This makes the program wait for the List to finish querying before using the List, which avoids any IndexOutOfBounds Exceptions
     */
    fun getQuestions(topic: String, numberOfQuestions: Int, firebaseQuestionCallback: FirebaseQuestionCallback) {
        val database =
            FirebaseDatabase.getInstance().getReference(topic) //creates a database reference to the specified topic

        //queries Questions from the database, adds them to the questions list, and then shuffles the list
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val random = Random()
                var randomList =
                    listOf<Int>() //keeps track of the random numbers generated in order to avoid a duplicate question query

                var i = 0 //used for iterating through the while loop
                while (i < numberOfQuestions) {
                    var randomNumber =
                        random.nextInt(p0.childrenCount.toInt()) + 1 //generates a random number from 1 to the number of children that the node of data has (both sides of the range are inclusive).

                    //if the number has already been queried, then the loop skips this iteration in order to avoid querying duplicate questions
                    if (randomList.contains(randomNumber)) {
                        continue
                    }
                    randomList =
                        randomList.plus(randomNumber)//adds the random number to the list in order to check if there is a repeat random number

                    /*
                        queries data from the database and instantiates them as Question objects
                        The primary keys of the questions in the database are indexed from 1 and incremented by 1.
                        Meaning, we can query a random question by entering a random Int number as the path
                     */
                    val query = p0.child(randomNumber.toString()).getValue(Question::class.java)
                    questions = questions.plus(query!!)
                    i++
                }
                Collections.shuffle(questions)
                firebaseQuestionCallback.onQuestionCallback(questions)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DB ERROR", "Error occurred while connecting to the Database")
            }
        })
    }

    /**
     * Queries usernames and their points from the database and adds them to a list
     * The query is order by points to be displayed from biggest to smallest in the leaderboards page
     *
     * @param FirebaseLeaderboardsCallback - The callback is used due to Firebase's asynchronous behavior. When this method is called, you have to override the FirebaseLeaderboardsCallback method in the parameter.
     *                                       This makes the program wait for the List to finish querying before using the List, which avoids any IndexOutOfBounds Exceptions
     */
    fun getQuickQuestions(firebaseQuestionCallback: FirebaseQuestionCallback) {
        val random = Random() //used to create random numbers for selecting random questions from each categories

        /*
            A list of the quiz categories the app has
            The query will iterate through this list and query each category
         */
        val categoryTitles = listOf(
            "FBLA Organization",
            "Competitive Events",
            "Business Skills",
            "National Officers",
            "Parliamentary Procedure",
            "National Conference",
            "FBLA History",
            "FBLA Bylaws",
            "Creed, National Goals, and Ethics",
            "Community Service"
        )
        val ref = FirebaseDatabase.getInstance().reference //creates a database reference to the whole database

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                //initialized outside the while loop to avoid them being initialized multiple times
                var randomCategory = 0
                var randomQuestion = 0

                var randomCategoryList = listOf<Int>()

                var i = 0 //used to iterate through the loop
                // queries 1 questions from each category
                while (i < 10) {
                    randomCategory = random.nextInt(10)
                    randomQuestion = random.nextInt(p0.childrenCount.toInt()) + 1 //generates a random number from 1 to the number of children that the node of data has (both sides of the range are inclusive).

                    if (randomCategoryList.contains(randomCategory)) {
                        continue
                    }
                    randomCategoryList = randomCategoryList.plus(randomCategory)

                    val query = p0.child(categoryTitles[randomCategory]).child(randomQuestion.toString()) //queries a random question from each category and creates Question object from it
                        .getValue(Question::class.java)
                    questions = questions.plus(query!!)

                    i++
                }
                Collections.shuffle(questions) //The questions list is shuffled to give a new experience for the user each time they play
                firebaseQuestionCallback.onQuestionCallback(questions) //passes the questions list to its callback method to ensure the list is only called after it has been fully queried.
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("DB ERROR", "Error occurred while connecting to the Database")
            }
        })
    }

    /**
     * This function is called when the test is over in order to calculate the user's score
     * @return Int - the score value
     */
    fun calculateScore(totalTimeLeft: Long): Int {
        val score = (totalTimeLeft / 1000) * correct
        return score.toInt()
    }
}