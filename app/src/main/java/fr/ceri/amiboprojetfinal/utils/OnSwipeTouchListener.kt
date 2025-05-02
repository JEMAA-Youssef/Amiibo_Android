package fr.ceri.amiboprojetfinal.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import fr.ceri.amiboprojetfinal.GameActivity
import kotlin.math.abs

class OnSwipeTouchListener(
    context: Context,
    private val gameActivity: GameActivity
) : View.OnTouchListener {

    private val gestureDetector = GestureDetector(context, GestureListener(gameActivity))

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event!!)
    }

    private class GestureListener(val gameActivity: GameActivity) :
        GestureDetector.SimpleOnGestureListener() {

        companion object {
            const val SWIPE_THRESHOLD = 100
            const val SWIPE_VELOCITY_THRESHOLD = 100
        }

        override fun onDown(e: MotionEvent): Boolean = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null || e2 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            return if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) onSwipeRight() else onSwipeLeft()
                    true
                } else false
            } else false
        }


        private fun onSwipeRight() {
            gameActivity.setQuestionType(true)
        }

        private fun onSwipeLeft() {
            gameActivity.setQuestionType(false)
        }
    }
}
