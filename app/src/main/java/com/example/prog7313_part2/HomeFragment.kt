package com.example.prog7313_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addMoney = view.findViewById<Button>(R.id.btnAddIncome)
        addMoney.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addMoneyFragment)
        }

        val addExpense = view.findViewById<Button>(R.id.btnAddExpense)
        addExpense.setOnClickListener{
            findNavController().navigate(R.id.action_homeFragment_to_addExpenseFragment)
        }

        val budgetPage = view.findViewById<Button>(R.id.btnAddBudget)
        budgetPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_BudgetFragment)
        }

        val transactionsPage = view.findViewById<Button>(R.id.btnViewTransactions)
        transactionsPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_TransactionsFragment)
        }

        val chartsPage = view.findViewById<Button>(R.id.btnCharts)
        chartsPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_PieChartFragment)
        }

        val reportsPage = view.findViewById<Button>(R.id.btnReports)
        reportsPage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_ReportFragment)
        }

        val profilePage = view.findViewById<Button>(R.id.btnProfilePage)
        profilePage.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_ProfilePageFragment)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}