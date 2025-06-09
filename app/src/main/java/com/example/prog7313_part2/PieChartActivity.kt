package com.example.prog7313_part2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PieChartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart) // This is the layout with FrameLayout (see below)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, PieChartFragment()) // Load your fragment into FrameLayout
            .commit()
    }
}
