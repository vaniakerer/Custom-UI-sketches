package com.example.ivan.customviews.anim_check_box

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.example.ivan.customviews.R

class AnimCheckBoxActivity : AppCompatActivity() {
    private lateinit var checkBox : AnimCheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim_check_box)

        checkBox = findViewById(R.id.anim_check_box)

        Handler().postDelayed({ checkBox.setChecked(true) }, 1000)
    }
}