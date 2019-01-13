package com.example.ivan.customviews.chart

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.example.ivan.customviews.R
import java.util.*

class ChartActivity : AppCompatActivity() {

    private lateinit var chartView: ChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        initChart()
    }

    private fun initChart() {
        chartView = findViewById(R.id.chart_activity_markers_chart)
        chartView.textLog = findViewById(R.id.text_test)

        Handler().postDelayed({ chartView.markers = getMarkers() }, 100)

        //    findViewById<TextView>(R.id.text_test).text = chartView.markers.toString()
    }

    private fun getMarkers(): List<Marker> {
        val markers = arrayListOf<Marker>()
        for (i in 0..7 * 7) {//seven weeks
            markers.add(Marker(Random().nextInt(100)))
        }

        return markers
    }
}