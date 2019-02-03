package com.example.ivan.customviews

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import com.example.ivan.customviews.anim_check_box.AnimCheckBoxActivity
import com.example.ivan.customviews.bottom_nav.BottomNavActivity
import com.example.ivan.customviews.chart.ChartActivity
import com.example.ivan.customviews.simple_progress.SimpleProgressActivity

class MainActivity : AppCompatActivity() {

    private lateinit var btnSimpleProgress: Button
    private lateinit var btnChartView: Button
    private lateinit var btnBottomNav: Button
    private lateinit var btnAnimCheckBox: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        btnSimpleProgress = findViewById(R.id.main_btn_simple_progress)
        btnChartView = findViewById(R.id.main_btn_chart_view)
        btnBottomNav = findViewById(R.id.main_btn_bottom_nav)
        btnAnimCheckBox = findViewById(R.id.main_btn_anim_check_box)


        btnSimpleProgress.setOnClickListener { openActivity<SimpleProgressActivity>() }
        btnChartView.setOnClickListener { openActivity<ChartActivity>() }
        btnBottomNav.setOnClickListener { openActivity<BottomNavActivity>() }
        btnAnimCheckBox.setOnClickListener { openActivity<AnimCheckBoxActivity>() }

        openActivity<AnimCheckBoxActivity>()
    }

    private inline fun <reified T> openActivity() {
        startActivity(Intent(applicationContext, T::class.java))
    }
}
