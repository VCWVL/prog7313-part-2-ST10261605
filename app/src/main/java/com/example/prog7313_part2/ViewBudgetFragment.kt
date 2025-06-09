package com.example.prog7313_part2

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewBudgetFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var currentBudgetAmountText: TextView
    private lateinit var remainingAmountText: TextView
    private lateinit var categoryBreakdownLayout: LinearLayout

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var budgetList = mutableListOf<Budget>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_budget, container, false)

        searchView = view.findViewById(R.id.searchBar)
        currentBudgetAmountText = view.findViewById(R.id.textView6)
        remainingAmountText = view.findViewById(R.id.textView7)
        categoryBreakdownLayout = view.findViewById(R.id.categoryBreakdownLayout)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        loadBudgets()

        setupSearch()

        return view
    }

    private fun loadBudgets() {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        firestore.collection("budgets")
            .whereEqualTo("userID", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                budgetList.clear()
                for (document in querySnapshot) {
                    val budget = document.toObject(Budget::class.java)
                    budgetList.add(budget)
                }
                updateUI(budgetList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load budgets", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUI(listToDisplay: List<Budget>) {
        // Total budget amount
        val totalBudget = listToDisplay.sumOf { it.totalAmount }
        currentBudgetAmountText.text = "Current Budget Amount: $${totalBudget.toInt()}"

        // Remaining amount
        val remainingAmount = listToDisplay.sumOf { it.maxGoal } - totalBudget
        remainingAmountText.text = "Remaining Amount: $${remainingAmount.toInt()}"

        // Category Breakdown
        categoryBreakdownLayout.removeAllViews()

        listToDisplay.forEach { budget ->
            val budgetCard = LayoutInflater.from(requireContext())
                .inflate(R.layout.budget_item, categoryBreakdownLayout, false)

            val categoryText = budgetCard.findViewById<TextView>(R.id.categoryText)
            val amountText = budgetCard.findViewById<TextView>(R.id.amountText)
            val progressBar = budgetCard.findViewById<ProgressBar>(R.id.progressBar)
            val editButton = budgetCard.findViewById<Button>(R.id.editButton)
            val deleteButton = budgetCard.findViewById<Button>(R.id.deleteButton)

            categoryText.text = budget.category
            amountText.text = "Amount: $${budget.totalAmount.toInt()} / Max Goal: $${budget.maxGoal.toInt()}"

            val progress = ((budget.totalAmount / budget.maxGoal) * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progress

            val progressColor = when {
                progress < 50 -> android.graphics.Color.parseColor("#228B22") // Green
                progress < 80 -> android.graphics.Color.parseColor("#FFA500") // Orange
                else -> android.graphics.Color.parseColor("#FF0000") // Red
            }

            progressBar.progressDrawable.setColorFilter(progressColor, android.graphics.PorterDuff.Mode.SRC_IN)

            // Edit button (you can implement navigation to an Edit screen)
            editButton.setOnClickListener {
                val bundle = Bundle()
                //bundle.putSerializable("budget", budget)

                val editFragment = EditBudgetFragment()
                editFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    //.replace(R.id.fragment_container, editFragment) // adjust if your container has another ID
                    .addToBackStack(null)
                    .commit()
            }

            // Delete button
            deleteButton.setOnClickListener {
                showDeleteConfirmationDialog(budget)
            }

            categoryBreakdownLayout.addView(budgetCard)
        }
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterBudgets(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBudgets(newText)
                return true
            }
        })
    }

    private fun filterBudgets(query: String?) {
        val filteredList = if (query.isNullOrEmpty()) {
            budgetList
        } else {
            budgetList.filter {
                it.category.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }

        updateUI(filteredList)
    }

    private fun showDeleteConfirmationDialog(budget: Budget) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Budget")
            .setMessage("Are you sure you want to delete this budget?")
            .setPositiveButton("Yes") { _, _ ->
                deleteBudget(budget)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteBudget(budget: Budget) {
        // You need the document ID to delete, so here we're using a simple workaround:
        firestore.collection("budgets")
            .whereEqualTo("userID", budget.userID)
            .whereEqualTo("budgetPeriod", budget.budgetPeriod)
            .whereEqualTo("category", budget.category)
            .whereEqualTo("totalAmount", budget.totalAmount)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    firestore.collection("budgets").document(document.id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Budget deleted", Toast.LENGTH_SHORT).show()
                            loadBudgets() // Reload after deletion
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to delete budget", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }
}
