package com.example.prog7313_part2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class PointsPageFragment : Fragment() {

    private lateinit var incomeEntriesProgress: TextView
    private lateinit var categoriesProgress: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_points_page, container, false)

        //Initialize auth and db first
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        //Now can use to auth
        val pointsTextView = view.findViewById<TextView>(R.id.totalPoints)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("UserAchievements").document(userId)
                .addSnapshotListener { snapshot, _ ->
                    val points = snapshot?.getLong("points") ?: 0
                    pointsTextView.text = "Points: $points"
                }
        }

        incomeEntriesProgress = view.findViewById(R.id.incomeEntriesProgress)
        categoriesProgress = view.findViewById(R.id.categoriesProgress)

        fetchIncomeAchievements()
        return view
    }

    //function to dynamically update users points as they unlock achievements
    private fun fetchIncomeAchievements() {
        val userId = auth.currentUser?.uid ?: return
        val userAchievementsRef = db.collection("UserAchievements").document(userId)

        db.collection("Income")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { incomeDocs ->
                val totalEntries = incomeDocs.size()
                val categorySet = incomeDocs.mapNotNull { it.getString("category")?.trim() }.toSet()

                // Update UI
                incomeEntriesProgress.text = "${totalEntries.coerceAtMost(20)}/20"
                categoriesProgress.text = "${categorySet.size.coerceAtMost(5)}/5"

                userAchievementsRef.get().addOnSuccessListener { doc ->
                    val data = doc.data ?: emptyMap<String, Any>()
                    var pointsToAdd = 0
                    val updates = mutableMapOf<String, Any>()

                    val entriesComplete = totalEntries >= 20
                    val categoriesComplete = categorySet.size >= 5

                    if (entriesComplete && data["incomeEntriesCompleted"] != true) {
                        updates["incomeEntriesCompleted"] = true
                        pointsToAdd += 10
                    }

                    if (categoriesComplete && data["incomeCategoriesCompleted"] != true) {
                        updates["incomeCategoriesCompleted"] = true
                        pointsToAdd += 10
                    }

                    if (pointsToAdd > 0) {
                        val currentPoints = (data["points"] as? Long)?.toInt() ?: 0
                        updates["points"] = currentPoints + pointsToAdd

                        userAchievementsRef.set(updates, SetOptions.merge())
                    }
                }
            }
            .addOnFailureListener {
                incomeEntriesProgress.text = "Error"
                categoriesProgress.text = "Error"
            }
    }
}
