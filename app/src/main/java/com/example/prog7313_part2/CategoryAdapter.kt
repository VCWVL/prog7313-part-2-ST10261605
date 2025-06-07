package com.example.prog7313_part2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private var data: List<CategoryAmount>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCategory: TextView = itemView.findViewById(R.id.txtCategory)
        val txtAmount: TextView = itemView.findViewById(R.id.txtAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_amount_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = data[position]
        holder.txtCategory.text = item.category
        holder.txtAmount.text = "R${item.totalAmount}"
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newData: List<CategoryAmount>) {
        data = newData
        notifyDataSetChanged()
    }
}
