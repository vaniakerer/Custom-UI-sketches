package com.example.ivan.customviews.util

import android.content.Context
import android.util.DisplayMetrics



class UiUtil{
    companion object {
        fun dp2px(dp: Float, context: Context): Float {
            return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }


        fun px2dp(px: Float, context: Context): Float {
            return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}