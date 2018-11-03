package com.example.ivan.customviews.simple_progress

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.example.ivan.customviews.R

class SimpleProgressActivity : AppCompatActivity() {

    private lateinit var progressView: ProgressView
    private lateinit var etxtNewProgress: EditText
    private lateinit var btnApplyChanges: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_progress)
        initViews()
    }

    private fun initViews() {
        progressView = findViewById(R.id.simple_progress_progress_view)
        etxtNewProgress = findViewById(R.id.simple_progress_etxt_new_value)
        btnApplyChanges = findViewById(R.id.simple_progress_btn_apply_changes)

        btnApplyChanges.setOnClickListener {
            val text = etxtNewProgress.text.toString()
            progressView.setProgress(if (!text.isEmpty()) text.toFloat() else 0f)
        }
    }
}