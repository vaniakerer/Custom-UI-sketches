package com.example.ivan.customviews

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import java.security.AccessControlContext

@ColorInt
fun Int.toColorInt(context: Context): Int = ContextCompat.getColor(context, this)