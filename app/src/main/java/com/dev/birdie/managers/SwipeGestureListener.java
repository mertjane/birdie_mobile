package com.dev.birdie.managers;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeGestureListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public SwipeGestureListener(Context context, SwipeCallback callback) {
        gestureDetector = new GestureDetector(context, new GestureListener(callback));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // We must return true so the view continues to receive touch events
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public interface SwipeCallback {
        void onSwipeLeft();

        void onSwipeRight();
    }

    private static class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;
        private final SwipeCallback callback;

        GestureListener(SwipeCallback callback) {
            this.callback = callback;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            callback.onSwipeRight();
                        } else {
                            callback.onSwipeLeft();
                        }
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}