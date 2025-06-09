package com.example.prog7313_part2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TransactionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.transactionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = TransactionsAdapter(mutableListOf())
        recyclerView.adapter = adapter

        val db = FirebaseFirestore.getInstance()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val allTransactions = mutableListOf<Transaction>()

        // Fetch Expenses
        db.collection("expenses")
            .whereEqualTo("userID", currentUserId)
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val expense = doc.toObject(Expense::class.java)

                    val date = expense.date?.toDate() ?: continue
                    val amount = expense.amount ?: 0.0
                    val category = expense.category ?: "Unknown"
                    val id = doc.id

                    val transaction = Transaction(
                        id = id,
                        amount = amount,
                        date = date,
                        category = category,
                        type = TransactionType.EXPENSE
                    )
                    allTransactions.add(transaction)
                }

                // After fetching expenses, fetch incomes
                db.collection("income")
                    .whereEqualTo("userID", currentUserId)
                    .get()
                    .addOnSuccessListener { incomeResult ->
                        for (doc in incomeResult) {
                            val income = doc.toObject(Income::class.java)

                            val date = income.date?.toDate() ?: continue
                            val amount = income.amount ?: 0.0
                            val category = income.category ?: "Unknown"
                            val id = doc.id

                            val transaction = Transaction(
                                id = id,
                                amount = amount,
                                date = date,
                                category = category,
                                type = TransactionType.INCOME
                            )
                            allTransactions.add(transaction)
                        }

                        // Sort transactions by date descending
                        val sortedTransactions = allTransactions.sortedByDescending { it.date }

                        // Update adapter
                        adapter.updateData(sortedTransactions)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Error loading income: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error loading expenses: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
