package com.example.prog7313_part2

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.LinearLayout
import android.widget.TextView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PieChartFragment : Fragment() {

    private lateinit var pieChart: PieChart
    private lateinit var legendLayout: LinearLayout
    private lateinit var summaryTextView: TextView

    private val viewModel: PieChartViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pie_chart, container, false)
        pieChart = view.findViewById(R.id.pieChart)
        legendLayout = view.findViewById(R.id.legendLayout)
        summaryTextView = view.findViewById(R.id.summaryTextView)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPieChart()

        auth.currentUser?.uid?.let { userId ->
            viewModel.fetchBudgetData(userId)
        }

        viewModel.budgetData.observe(viewLifecycleOwner) { categories ->
            if (categories.isNotEmpty()) {
                updatePieChart(categories)
                updateLegend(categories)
                updateSummary(categories)
            }
        }
    }

    private fun setupPieChart() {
        pieChart.setUsePercentValues(false)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f
        pieChart.setDrawCenterText(true)
        pieChart.rotationAngle = 0f
        pieChart.isRotationEnabled = true
        pieChart.isHighlightPerTapEnabled = true
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(12f)
    }

    private fun updatePieChart(categories: List<BudgetCategory>) {
        val entries = categories.map {
            PieEntry(it.amount, it.name)
        }

        val dataSet = PieDataSet(entries, "Budget Categories")
        dataSet.colors = categories.map { Color.parseColor(it.color) }
        dataSet.valueTextSize = 12f
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "$${value.toInt()}"
            }
        }

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.centerText = "Budget\nDistribution"
        pieChart.invalidate()
    }

    private fun updateLegend(categories: List<BudgetCategory>) {
        legendLayout.removeAllViews()

        categories.forEach { category ->
            val view = layoutInflater.inflate(R.layout.item_legend, legendLayout, false)
            val colorView = view.findViewById<View>(R.id.colorView)
            val nameText = view.findViewById<TextView>(R.id.nameText)
            val amountText = view.findViewById<TextView>(R.id.amountText)
            val goalText = view.findViewById<TextView>(R.id.goalText)

            colorView.setBackgroundColor(Color.parseColor(category.color))
            nameText.text = category.name
            amountText.text = "$${category.amount.toInt()}"
            goalText.text = "Goal: $${category.goal.toInt()}"

            legendLayout.addView(view)
        }

    }

    private fun updateSummary(categories: List<BudgetCategory>) {
        if (categories.isEmpty()) return

        val totalSpent = categories.sumOf { it.amount.toDouble() }.toFloat()
        val maxCategory = categories.maxByOrNull { it.amount }
        val minCategory = categories.minByOrNull { it.amount }

        val summaryText = """
            Total Spent: $${totalSpent.toInt()}
            
            Highest Spending: ${maxCategory?.name} ($${maxCategory?.amount?.toInt()})
            Lowest Spending: ${minCategory?.name} ($${minCategory?.amount?.toInt()})
            
            Goals:
            ${categories.joinToString("\n") {
            "${it.name}: ${calculateProgress(it.amount, it.goal)}% of goal ($${it.amount.toInt()}/$${it.goal.toInt()})"
        }}
        """.trimIndent()

        summaryTextView.text = summaryText
    }

    private fun calculateProgress(amount: Float, goal: Float): Int {
        return if (goal == 0f) 0 else (amount / goal * 100).toInt()
    }


}

// References or Code Attribution: https://www.geeksforgeeks.org/android-create-a-pie-chart-with-kotlin/ ; GeeksforGeeks ; 31 May, 2024 ; Android - Create a Pie Chart with Kotlin
//                                 https://www.geeksforgeeks.org/how-to-add-a-pie-chart-into-an-android-application/ ; GeeksforGeeks ; 11 Feb, 2025 ; How to add a Pie Chart into an Android Application