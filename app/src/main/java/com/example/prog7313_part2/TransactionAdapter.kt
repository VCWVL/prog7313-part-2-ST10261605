package com.example.prog7313_part2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        // Set icon based on transaction type
        /** val iconRes = if (transaction.type == TransactionType.INCOME) {
            R.drawable.ic_income // You need to add this drawable (or use a built-in one)
        } else {
            R.drawable.ic_expense // You need to add this drawable (or use a built-in one)
        }**/

        //holder.imgTransactionIcon.setImageResource(iconRes)

        // Set amount, format it nicely
        val amountText = if (transaction.type == TransactionType.INCOME) {
            "+R%.2f".format(transaction.amount)
        } else {
            "-R%.2f".format(transaction.amount)
        }
        holder.txtTransactionAmount.text = amountText

        // Set date
        holder.txtTransactionDate.text = dateFormatter.format(transaction.date)

        // Set category
        holder.txtTransactionCategory.text = "Category: ${transaction.category}"
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgTransactionIcon: ImageView = itemView.findViewById(R.id.imgTransactionIcon)
        val txtTransactionAmount: TextView = itemView.findViewById(R.id.txtTransactionAmount)
        val txtTransactionDate: TextView = itemView.findViewById(R.id.txtTransactionDate)
        val txtTransactionCategory: TextView = itemView.findViewById(R.id.txtTransactionCategory)
    }
}
