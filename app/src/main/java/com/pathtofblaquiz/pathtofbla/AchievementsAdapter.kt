package com.pathtofblaquiz.pathtofbla

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.achievement_cells.view.*

/**
 * Achievements adapter is used to easily show the list of user achievements
 * It can show the locked and unlocked achievements while using the same layout
 */
class AchievementsAdapter(private val achievements: UserAchievements) : RecyclerView.Adapter<AchievementsViewHolder>() {

    //list contains all the achievements possible
    private val achievementsList = listOf(
        "Get to Level 5",
        "Get to Level 2",
        "Take 10 Quizzes",
        "Take 5 Quizzes",
        "Score 500 Points",
        "Score 250 Points"
    )

    //list contains the possible points/xp for the subsequent achievements
    private val achievementsPoints = listOf(
        5000,
        1000,
        5000,
        1000,
        5000,
        1000
    )

    //Pass in a view
    //Template view
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AchievementsViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.achievement_cells, p0, false)
        return AchievementsViewHolder(cellForRow)
    }

    //Number of items
    override fun getItemCount(): Int {
        return achievementsList.size
    }

    //This makes elements unique
    override fun onBindViewHolder(p0: AchievementsViewHolder, p1: Int) {
        p0.view.achievementTitle.text = achievementsList[p1]
        p0.view.achievementPoints.text = achievementsPoints[p1].toString() + " points"

        val achievementList = listOf(
            achievements.level5,
            achievements.level2,
            achievements.take10Quizzes,
            achievements.take5Quizzes,
            achievements.score500Points,
            achievements.score250Points
        )

        if (achievementList[p1]) {
            p0.view.achievementStatus.setImageResource(R.drawable.ic_badge_with_a_star)
            p0.view.achievementStatus.setBackgroundResource(R.drawable.achievement_unlocked)
        } else {
            p0.view.achievementStatus.setImageResource(R.drawable.ic_lock_white_24dp)
            p0.view.achievementStatus.setBackgroundResource(R.drawable.achievement_locked)
        }
    }

}

class AchievementsViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
}