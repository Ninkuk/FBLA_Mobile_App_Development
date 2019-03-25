package com.pathtofblaquiz.pathtofbla


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_statistics.*


/**
 * Statistics Fragment handles the user's statistics based on their points scored in a quiz
 *
 * It shows a table with score breakdown of each category so they acn see which category they and succeeding in and also not doing very well in so they could focus more on it
 * Then it shows their total points and the average points they have scored
 */
class StatisticsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val uid = FirebaseAuth.getInstance().uid
        val categoryPointsRef = FirebaseDatabase.getInstance().getReference("CategoryPoints/$uid")
        categoryPointsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val points = p0.getValue(CategoryPoints::class.java)

                if (points != null) {
                    org.text = points.organizationPoints.toString()
                    events.text = points.eventsPoints.toString()
                    skills.text = points.skillsPoints.toString()
                    officers.text = points.officersPoints.toString()
                    procedure.text = points.procedurePoints.toString()
                    conference.text = points.conferencePoints.toString()
                    history.text = points.historyPoints.toString()
                    bylaws.text = points.bylawsPoints.toString()
                    creed.text = points.creedPoints.toString()
                    service.text = points.servicePoints.toString()
                    misc.text = points.miscellaneousPoints.toString()

                    val average = (
                            points.organizationPoints +
                                    points.eventsPoints +
                                    points.skillsPoints +
                                    points.officersPoints +
                                    points.procedurePoints +
                                    points.conferencePoints +
                                    points.historyPoints +
                                    points.bylawsPoints +
                                    points.creedPoints +
                                    points.servicePoints +
                                    points.miscellaneousPoints) / 10.0

                    avg.text = average.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val totalPoints = p0.child("totalPoints").value.toString()
                total.text = totalPoints
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StatisticsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}