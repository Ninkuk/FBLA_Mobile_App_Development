package com.pathtofblaquiz.pathtofbla

/**
 * Achievement class contains the parameters that can be used to check if the user has completed the requirements to unlock an achievement
 */
class Achievement(var requirement: Int, var xp: Int) {
    constructor() : this(0, 0)
}