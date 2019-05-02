package com.pathtofblaquiz.pathtofbla


import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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

    val uid = FirebaseAuth.getInstance().uid

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

        //this is the function that is called when the layout is created
        checkInternetConnection()
        updateChart()
        updateTables()
    }

    /**
     * This function checks if the user's device is connected to the internet to access Firebase Database
     * If the user is not connected to the internet then an alert dialog box appears reminding them to do so
     */
    private fun checkInternetConnection() {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        //if there is no connection then it shows the alert and if there is a connection nothing happens
        if (networkInfo != null && networkInfo.isConnected) {
        } else {
            val builder = AlertDialog.Builder(this.context!!)
            builder.setTitle("Connection Alert")
            builder.setMessage("Please connect to the internet to use this app!")
            builder.setPositiveButton("Retry") { dialogInterface: DialogInterface, i: Int ->
                checkInternetConnection() //If the user presses retry then the function is called again
            }
            builder.show()
        }
    }

    /**
     * This function queries the last 5 user quiz scores from the database and presents them the user by creating a line chart
     */
    private fun updateChart() {

        getScoreHistory(object : FirebaseScoresCallback {
            override fun onScoresCallback(scores: List<Float>) {
                val dataVal = mutableListOf<Entry>()
                var i = 0f
                for (score in scores) {
                    dataVal.add(Entry(i, score))
                    i++
                }

                if (dataVal.size < 2) {
                    noDataError.visibility = View.VISIBLE
                }

                val lineDataSet = LineDataSet(dataVal, "Quiz Scores")
                lineDataSet.color = resources.getColor(R.color.tealDark)
                lineDataSet.lineWidth = 3f
                lineDataSet.setCircleColor(resources.getColor(R.color.google_red))
                lineDataSet.setDrawCircleHole(false)
                val dataSets = ArrayList<ILineDataSet>()
                dataSets.add(lineDataSet)

                val data = LineData(dataSets)

                val description = scoreLineChart.description
                description.text = "Score Progress"

                scoreLineChart.setNoDataText("Please take a quiz to show statistics")

                scoreLineChart.data = data
                scoreLineChart.axisRight.isEnabled = false
                val xAxis = scoreLineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                val legend = scoreLineChart.legend
                legend.xOffset = -10f
                scoreLineChart.extraBottomOffset = 15f
                scoreLineChart.animateX(500)
                scoreLineChart.invalidate()
            }
        })
    }

    /**
     * This function queries the user statistics from the database and presents them to the user by creating various tables such as category points, total points and average points
     */
    private fun updateTables() {
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
                                    points.miscellaneousPoints) / 11.0
                    avg.text = average.toString()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "Error occurred while connecting to the Database", Toast.LENGTH_LONG).show()
            }
        })

        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val totalPoints = p0.child("totalPoints").value.toString()
                total.text = totalPoints
                val highScore = p0.child("highScore").value.toString()
                high.text = highScore
            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "Error occurred while connecting to the Database", Toast.LENGTH_LONG).show()
            }
        })
    }

    /**
     * This function creates a list of the user's last 5 quiz scores
     */
    private fun getScoreHistory(firebaseScoresCallback: FirebaseScoresCallback) {
        val database = FirebaseDatabase.getInstance().getReference("ScoreHistory/$uid")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var scores: List<Float> = mutableListOf()
                for (postSnapshot in p0.children) {
                    val query = postSnapshot.value.toString().toFloat()
                    scores += query
                }
                firebaseScoresCallback.onScoresCallback(scores)
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