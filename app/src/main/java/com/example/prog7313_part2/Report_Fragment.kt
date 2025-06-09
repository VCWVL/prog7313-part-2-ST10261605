package com.example.prog7313_part2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class Report_Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_report_, container, false)


        val cardViewAllExpenses = view.findViewById<View>(R.id.cardViewAllExpenses)
        cardViewAllExpenses.setOnClickListener {
            val intent = Intent(requireContext(), ExpensesReportActivity::class.java)
            startActivity(intent)
        }


        val cardViewCategoryAmounts = view.findViewById<View>(R.id.cardViewCategoryAmounts)
        cardViewCategoryAmounts.setOnClickListener {
            val intent = Intent(requireContext(), CategoryReportActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}
