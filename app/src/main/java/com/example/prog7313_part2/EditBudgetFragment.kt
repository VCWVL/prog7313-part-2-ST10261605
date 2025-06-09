package com.example.prog7313_part2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditBudgetFragment : Fragment() {

    private lateinit var categoryEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var totalAmountEditText: EditText
    private lateinit var maxGoalEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var budgetToEdit: Budget? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        budgetToEdit = arguments?.getSerializable("budget") as? Budget
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_budget, container, false)

        categoryEditText = view.findViewById(R.id.editTextCategory)
        descriptionEditText = view.findViewById(R.id.editTextDescription)
        totalAmountEditText = view.findViewById(R.id.editTextTotalAmount)
        maxGoalEditText = view.findViewById(R.id.editTextMaxGoal)
        saveButton = view.findViewById(R.id.buttonSave)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        populateFields()

        saveButton.setOnClickListener {
            saveChanges()
        }

        return view
    }

    private fun populateFields() {
        budgetToEdit?.let { budget ->
            categoryEditText.setText(budget.category)
            descriptionEditText.setText(budget.description)
            totalAmountEditText.setText(budget.totalAmount.toString())
            maxGoalEditText.setText(budget.maxGoal.toString())
        }
    }

    private fun saveChanges() {
        val category = categoryEditText.text.toString()
        val description = descriptionEditText.text.toString()
        val totalAmount = totalAmountEditText.text.toString().toDoubleOrNull() ?: 0.0
        val maxGoal = maxGoalEditText.text.toString().toDoubleOrNull() ?: 0.0

        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        firestore.collection("budgets")
            .whereEqualTo("userID", userId)
            .whereEqualTo("budgetPeriod", budgetToEdit?.budgetPeriod)
            .whereEqualTo("category", budgetToEdit?.category)
            .whereEqualTo("totalAmount", budgetToEdit?.totalAmount)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val docRef = firestore.collection("budgets").document(document.id)
                    docRef.update(
                        mapOf(
                            "category" to category,
                            "description" to description,
                            "totalAmount" to totalAmount,
                            "maxGoal" to maxGoal
                        )
                    ).addOnSuccessListener {
                        Toast.makeText(requireContext(), "Budget updated", Toast.LENGTH_SHORT).show()
                        requireActivity().supportFragmentManager.popBackStack()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to update", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
