package com.example.prog7313_part2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.exp

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

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
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.transactionsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //Get the databases
        val incomeDatabase = IncomeDatabase.getDatabase(requireContext())
        val expenseDatabase = ExpenseDatabase.getDatabase(requireContext())

        //Get the data
        val incomeList = incomeDatabase.incomeDao().getAllIncome()
        val expenseList = expenseDatabase.expenseDao().getAllExpense()

        //Map to transactions
        val incomeTransaction = incomeList.map { income ->
            Transaction(
                id = income.id.toString(),
                amount = income.amount,
                date = parseDate(income.date),
                category = income.category,
                type = TransactionType.INCOME
            )
        }
        val expenseTransaction = expenseList.map { expense ->
            Transaction(
                id = expense.id.toString(),
                amount = expense.amount,
                date = parseDate (expense.date),
                category = expense.category,
                type = TransactionType.EXPENSE
            )
        }

        //Combine and sort
        val allTransactions =(incomeTransaction + expenseTransaction)
            .sortedByDescending{it.date}

        recyclerView.adapter = TransactionsAdapter(allTransactions)
    }
    //function to parse String date to data object
    private fun parseDate(dateString: String): Date {
        return try {
            dateFormatter.parse(dateString) ?: Date()
        } catch (e: Exception) {
            e.printStackTrace()
            Date()
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}