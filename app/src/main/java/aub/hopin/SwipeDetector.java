package aub.hopin;

import android.view.MotionEvent;
import android.view.View;

public class SwipeDetector implements View.OnTouchListener {
    private static final int HORIZONTAL_MIN_DISTANCE = 30;
    private static final int VERTICAL_MIN_DISTANCE = 80;
    private float downX, downY, upX, upY;

    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }
    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            }
            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
                    if (deltaX < 0) {
                        mSwipeDetected = Action.LR;
                        return true;
                    }
                    if (deltaX > 0) {
                        mSwipeDetected = Action.RL;
                        return true;
                    }
                } else
                    if (Math.abs(deltaY) > VERTICAL_MIN_DISTANCE) {
                        if (deltaY < 0) {
                            mSwipeDetected = Action.TB;
                            return false;
                        }
                        if (deltaY > 0) {
                            mSwipeDetected = Action.BT;
                            return false;
                        }
                    }
                return true;
            }
        }
        return false;
    }
}