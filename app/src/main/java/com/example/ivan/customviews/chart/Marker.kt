package com.example.ivan.customviews.chart

import android.graphics.PointF

data class Marker(val value: Int) {
    val point = PointF()

    operator fun minus(marker: Marker?): Int {
        marker?.value ?: return value
        return value - marker.value
    }

    override fun toString(): String {
        return "[x=${point.x} y= ${point.y} value=$value]"
    }
}