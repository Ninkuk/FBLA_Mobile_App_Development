package com.pathtofblaquiz.pathtofbla

/**
 * A Kotlin file containing interfces for callBack methods
 * the methods are used to handle Firebase's asynchronous behavior and run code only when the data is fully queried
 */
/**
 * This interface is used when querying questions for quizzes
 */
interface FirebaseQuestionCallback {
    fun onQuestionCallback(list: List<Question>)
}

/**
 * This interface is used when querying usernames and points for the leaderboards
 */
interface FirebaseLeaderboardsCallback {
    fun onLeaderboardsCallback(users: List<String>, points: List<Int>)
}

/**
 * This interface is used when querying user's incomplete achievements
 */
interface FirebaseAchievementsCallback {
    fun onAchievementsCallback(achievementList: List<String>)
}

/**
 * This interface is used when querying user's score history
 */
interface FirebaseScoresCallback {
    fun onScoresCallback(scores: List<Float>)
}