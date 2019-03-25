package com.pathtofblaquiz.pathtofbla

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_leaderboard.*

/**
 * Leaderboard fragment handles the leaderboards activity which shows the list of users who have the most points from a descending order
 */
class LeaderboardFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewLeaderboard.layoutManager = LinearLayoutManager(this.context)

        getLeaderboards(object : FirebaseLeaderboardsCallback {
            override fun onLeaderboardsCallback(users: List<String>, points: List<Int>) {
                val adapterLeaderboard = LeaderboardAdapter(users.reversed(), points.reversed())
                recyclerViewLeaderboard.adapter = adapterLeaderboard

                firstUsername.text = users.reversed()[0]
                secondUsername.text = users.reversed()[1]
                thirdUsername.text = users.reversed()[2]
                firstPoints.text = points.reversed()[0].toString()
                secondPoints.text = points.reversed()[1].toString()
                thirdPoints.text = points.reversed()[2].toString()
            }

        })
    }

    var users = listOf<String>() //holds a list of usernames order by their points for the leaderboards page. Initialized in getLeaderboards function
    var points = listOf<Int>() //holds a list of points order by value for the leaderboards page. Initialized in getLeaderboards function
    /**
     * Queries usernames and their points from the database and adds them to a list
     * The query is order by points to be displayed from biggest to smallest in the leaderboards page
     *
     * @param FirebaseLeaderboardsCallback - The callback is used due to Firebase's asynchronous behavior. When this method is called, you have to override the FirebaseLeaderboardsCallback method in the parameter.
     *                               This makes the program wait for the List to finish querying before using the List, which avoids any IndexOutOfBounds Exceptions
     */
    fun getLeaderboards(FirebaseLeaderboardsCallback: FirebaseLeaderboardsCallback) {
        val database = FirebaseDatabase.getInstance().getReference("users").orderByChild("totalPoints") //database reference to the users table ordered by their total points
        users = emptyList() //clears the users list to ensure the list is empty beforehand
        points = emptyList() //clears the points list to ensure the list is empty beforehand

        //queries usernames and their total points from the database and adds them to the list
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (postSnapshot in p0.children) {
                    val usernameQuery = postSnapshot.child("username").value.toString() //queries the username
                    val pointsQuery = postSnapshot.child("totalPoints").value.toString().toInt() //queries the total points of that username
                    users = users.plus(usernameQuery)
                    points = points.plus(pointsQuery)
                }
                FirebaseLeaderboardsCallback.onLeaderboardsCallback(users, points) //passes the users and points list to a callback method to ensure the lists are only called after they has been fully queried.
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    //helps to create an instance of leaderboard fragment in the main activity
    companion object {
        @JvmStatic
        fun newInstance() =
            LeaderboardFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}