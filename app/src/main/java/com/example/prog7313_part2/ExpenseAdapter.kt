package com.example.prog7313_part2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private var expenseList: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // ViewHolder inner class
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.categorySelection)
        val amountText: TextView = itemView.findViewById(R.id.edtAmount)
        val dateText: TextView = itemView.findViewById(R.id.edtDatePicker)
        val descriptionText: TextView = itemView.findViewById(R.id.edtDescription)
    }

    // Inflate the layout for each row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_add_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    // Bind data to the views
    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.categoryText.text = "Category: ${expense.category}"
        holder.amountText.text = "Amount: R${expense.amount}"
        holder.dateText.text = "Date: ${expense.date}"
        holder.descriptionText.text = "Note: ${expense.description}"
    }

    override fun getItemCount(): Int = expenseList.size

    // Update the list of expenses
    fun updateExpenses(newExpenses: List<Expense>) {
        expenseList = newExpenses
        notifyDataSetChanged()
    }
}
