package com.pathtofblaquiz.pathtofbla


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_dashboard.*

/**
 * Dashboard Fragment handles the user dashboard activity
 * It queries the user info such as points, levels, username etc and shows it in the gui
 */
class DashboardFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDashboard()

        quickQuizButton.setOnClickListener {
            val switchToQuestions = Intent(view.context, QuestionsActivity::class.java)
            switchToQuestions.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            view.context.startActivity(switchToQuestions)
        }

        //recycler view adapter is Grid layout of 2x5 for 10 categories
        recyclerViewCategories.layoutManager = GridLayoutManager(this.context, 2)
        val adapterCategories = CategoriesAdapter()
        recyclerViewCategories.adapter = adapterCategories
    }

    /**
     * This function queries the database fo user information and displays it in the gui
     */
    private fun updateDashboard() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.child("username").value.toString()
                val points = p0.child("totalPoints").value.toString()
                val quizzes = p0.child("quizzes").value.toString()
                val level = p0.child("level").value.toString()
                userText.text = user
                pointsText.text = points
                quizzesText.text = quizzes
                levelText.text = level
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    //helps to create an instance of dashboard fragment in the main activity
    companion object {
        @JvmStatic
        fun newInstance() =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
