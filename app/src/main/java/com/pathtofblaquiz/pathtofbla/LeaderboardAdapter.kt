package com.pathtofblaquiz.pathtofbla

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.leaderboard_cells.view.*

/**
 * Leaderboard Adapter allows for an easy creation of the list of users in teh leaderboard fragment
 *
 * The users are shown from #4 because the top 3 users are shown using the Leaderboard Fragment and is not part of the list
 */
class LeaderboardAdapter(val users: List<String>, val points: List<Int>) :
    RecyclerView.Adapter<LeaderboardViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): LeaderboardViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.leaderboard_cells, p0, false)
        return LeaderboardViewHolder(cellForRow)
    }

    override fun getItemCount(): Int {
        return users.size - 3
    }

    override fun onBindViewHolder(p0: LeaderboardViewHolder, p1: Int) {
        p0.view.positionButton.text = (p1 + 4).toString()
        p0.view.positionUsername.text = users[p1 + 3]
        p0.view.positionPoints.text = points[p1 + 3].toString()
    }

}

class LeaderboardViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

}