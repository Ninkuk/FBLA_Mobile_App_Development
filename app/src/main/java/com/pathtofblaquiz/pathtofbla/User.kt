package com.pathtofblaquiz.pathtofbla

/**
 * The User class contains user related fields such as username and uid
 * It is mainly used to create Users in signup activity
 */
class User(
    val uid: String,
    val username: String,
    val quizzes: Int,
    val level: Int,
    val xp: Int,
    val totalPoints: Int,
    val highScore: Int
) {
    constructor() : this(
        "",
        "",
        0,
        1,
        0,
        0,
        0
    )
}