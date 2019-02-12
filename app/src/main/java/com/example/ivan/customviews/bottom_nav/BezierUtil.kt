package com.example.ivan.customviews.bottom_nav

class BezierUtil {
    companion object CurveIntermediatePoints {

        /**
         * @return offset/shift by X from start bezier curve point
         */
        internal fun getBezierCurveFirstIntermediatePointShiftedX(startPointX: Float, curveDiametr: Float): Float {
            return startPointX + curveDiametr * BottomNavV2.BIZIER_CURVE_FIRST_POINT_X_SHIFT
        }

        /**
         * @return offset/shift by Y from start bezier curve point
         */
        internal fun getBezierCurveFirstIntermediatePointShiftedY(startPointY: Float, curveRadius: Float): Float {
            return startPointY + curveRadius * BottomNavV2.BIZIER_CURVE_FIRST_POINT_Y_SHIFT
        }

        /**
         * @return offset/shift by X from end bezier curve point
         */
        internal fun getBezierCurveSecondIntermediatePointShiftedX(endPointX: Float, curveDiametr: Float): Float {
            return endPointX - curveDiametr * BottomNavV2.BIZIER_CURVE_FIRST_POINT_X_SHIFT
        }

        /**
         * @return offset/shift by Y from start bezier curve point
         */
        internal fun getBezierCurveSecondIntermediatePointShiftedY(endPointY: Float, curveRadius: Float): Float {
            return getBezierCurveFirstIntermediatePointShiftedY(endPointY, curveRadius)
        }
    }
}