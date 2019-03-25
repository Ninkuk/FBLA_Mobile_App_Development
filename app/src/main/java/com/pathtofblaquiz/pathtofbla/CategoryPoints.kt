package com.pathtofblaquiz.pathtofbla

/**
 * Category Points class contains the user's category points
 * When a user profile is created the fields are instantiated to 0 and as they progress the points are incremented in the database
 */
class CategoryPoints(
    val organizationPoints: Int,
    val eventsPoints: Int,
    val skillsPoints: Int,
    val officersPoints: Int,
    val procedurePoints: Int,
    val conferencePoints: Int,
    val historyPoints: Int,
    val bylawsPoints: Int,
    val creedPoints: Int,
    val servicePoints: Int,
    val miscellaneousPoints: Int
) {
    constructor() : this(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0
    )
}