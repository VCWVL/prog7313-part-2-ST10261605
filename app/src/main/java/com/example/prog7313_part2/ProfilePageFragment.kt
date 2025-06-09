package com.example.prog7313_part2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.media3.common.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.jvm.java

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfilePageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfilePageFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        return inflater.inflate(R.layout.fragment_profile_page, container, false)
    }

    override fun onViewCreated(view:View,savedInstanceState: Bundle?)
    {
        super.onViewCreated(view,savedInstanceState)

        val logoutbtn: Button = view.findViewById(R.id.Logoutbtn)
        val exportbtn: Button = view.findViewById(R.id.Exportbtn)

        //for logout
        logoutbtn.setOnClickListener {
            logout()
        }
        //for export worksheets
        exportbtn.setOnClickListener {
            fetchExpensesFromFirestore { expenses ->
                exportExpensesToExcel(requireContext(), expenses)
            }
        }
    }

    private fun logout() {
        // Example: Clear user session (if using SharedPreferences)
        val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Navigate to LoginActivity (replace LoginActivity with your actual login activity)
        val intent = Intent(requireActivity(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun fetchExpensesFromFirestore(onResult: (List<Expense>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("expenses")
            .get()
            .addOnSuccessListener { result ->
                val expenses = result.mapNotNull { doc ->
                    try {
                        Expense(
                            id = doc.id,
                            userID = doc.getString("userID") ?: "",
                            category = doc.getString("category") ?: "",
                            amount = doc.getDouble("amount") ?: 0.0,
                            date = doc.getTimestamp("date"),
                            description = doc.getString("description") ?: "",
                            startDate = doc.getTimestamp("startDate"),
                            endDate = doc.getTimestamp("endDate"),
                            fileUri = doc.getString("fileUri")
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
                onResult(expenses)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    //function for exporting expenses to excel worksheet
    fun exportExpensesToExcel(context: Context, expenses: List<Expense>) {
        try {
            val workbook: Workbook = XSSFWorkbook() //initializing workbook
            val sheet = workbook.createSheet("Expenses") //initializing sheet

            // Header row
            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("ID")
            header.createCell(1).setCellValue("User ID")
            header.createCell(2).setCellValue("Category")
            header.createCell(3).setCellValue("Amount")
            header.createCell(4).setCellValue("Date")
            header.createCell(5).setCellValue("Description")

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Data rows
            for ((index, expense) in expenses.withIndex()) {
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(expense.id)
                row.createCell(1).setCellValue(expense.userID)
                row.createCell(2).setCellValue(expense.category)
                row.createCell(3).setCellValue(expense.amount)
                row.createCell(4).setCellValue(
                    expense.date?.toDate()?.let { formatter.format(it) } ?: "N/A"
                )
                row.createCell(5).setCellValue(expense.description)
            }

            // Save file
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "ExpensesExport_${System.currentTimeMillis()}.xlsx")

            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            outputStream.close()
            workbook.close()

            Toast.makeText(context, "Exported to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilePageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfilePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}