package com.pathtofblaquiz.pathtofbla

/**
 * User Achievements class contains user's achievements
 * When a user profile is created the fields are instantiated to false and as they progress the achievements are unlocked
 */
class UserAchievements(
    val level5: Boolean,
    val level2: Boolean,
    val take10Quizzes: Boolean,
    val take5Quizzes: Boolean,
    val score500Points: Boolean,
    val score250Points: Boolean
) {
    constructor() : this(
        false,
        false,
        false,
        false,
        false,
        false
    )
}