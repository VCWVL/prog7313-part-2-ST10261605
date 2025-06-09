package com.example.prog7313_part2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PieChartViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _budgetData = MutableStateFlow<List<BudgetCategory>>(emptyList())
    val budgetData: LiveData<List<BudgetCategory>> = _budgetData.asLiveData()

    fun fetchBudgetData(userId: String) {
        firestore.collection("users").document(userId).collection("categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val categories = snapshot?.documents?.mapNotNull { doc ->
                    BudgetCategory(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        amount = doc.getDouble("amount")?.toFloat() ?: 0f,
                        goal = doc.getDouble("goal")?.toFloat() ?: 0f,
                        color = doc.getString("color") ?: "#000000"
                    )
                } ?: emptyList()

                _budgetData.value = categories
            }
    }

}

data class BudgetCategory(
    val id: String,
    val name: String,
    val amount: Float,
    val goal: Float,
    val color: String
)

//References or Code Attribution: https://www.geeksforgeeks.org/android-create-a-pie-chart-with-kotlin/ ; GeeksforGeeks ; 31 May, 2024 ; Android - Create a Pie Chart with Kotlin
//                                https://androidapps-development-blogs.medium.com/how-to-create-charts-in-android-part-1-bar-chart-pie-chart-radar-chart-mp-android-chart-9c313e17068e ; Medium ; 25 Dec 2020 ; Golap Gunjan Barman
//                                https://www.geeksforgeeks.org/how-to-add-a-pie-chart-into-an-android-application/ ; GeeksforGeeks ; 11 Feb, 2025 ; How to add a Pie Chart into an Android Application