package com.example.ivan.customviews.bottom_nav

import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent

class TabBarUtil {
    companion object {

        /**
         * @return if tapped point is in tabBar rect
         */
        fun isTapOnTabBar(startPoint: PointF, endPoint: PointF, tapEvent: MotionEvent): Boolean =
                tapEvent.x > startPoint.x
                        && tapEvent.x < endPoint.x
                        && tapEvent.y > startPoint.y
                        && tapEvent.y < endPoint.y

        /**
         * @return selected tab position by tapped point coordinates
         */
        fun getTappedTabPosition(
                tabBarRect: RectF,
                tabBarStartPoint: PointF,
                tapEvent: MotionEvent,
                tabsCount: Int): Int {

            // calculating width from tap event to start of tabBar
            val pointWidthFromTabBarStart = tapEvent.x - tabBarStartPoint.x

            return (((pointWidthFromTabBarStart / tabBarRect.width())) * tabsCount).toInt()
        }
    }
}