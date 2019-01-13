package com.example.ivan.customviews

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.ivan.customviews.chart.ChartActivity
import com.example.ivan.customviews.simple_progress.SimpleProgressActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnSimpleProgress: Button
    private lateinit var btnChartView: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        btnSimpleProgress = findViewById(R.id.main_btn_simple_progress)
        btnChartView = findViewById(R.id.main_btn_chart_view)

        btnSimpleProgress.setOnClickListener { openActivity<SimpleProgressActivity>() }
        btnChartView.setOnClickListener { openActivity<ChartActivity>() }
        openActivity<ChartActivity>()
    }

    private inline fun <reified T> openActivity() {
        startActivity(Intent(applicationContext, T::class.java))
    }
}
