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