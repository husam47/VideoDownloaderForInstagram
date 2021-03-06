package com.zxmark.videodownloader.component;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.imobapp.videodownloaderforinstagram.R;
import com.nineoldandroids.view.ViewHelper;
import com.zxmark.videodownloader.util.LogUtil;
import com.zxmark.videodownloader.util.Utils;

/**
 * Created by fanlitao on 17/6/9.
 */

public class FloatNotificationView extends FrameLayout implements View.OnClickListener {

    private static final int MIN_FLING_VELOCITY = 400; // dips
    private static final int MIN_DISTANCE_FOR_FLING = 25;

    private float mRawX;
    private float mRawY;
    private int mInitRootY;
    private int mInitRootX;

    private boolean mPerformDrag = false;
    private boolean mPerformDismiss = false;

    private LayoutInflater mInflater;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWm;
    private float touchSlop;
    float density = 0;
    private int mMinimumVelocity = 0;
    private int mMaximumVelocity = 0;
    private int mFlingDistance = 0;

    private VelocityTracker mVelocityTracker;
    private Context mContext;
    private View mProgressInfoTv;

    @Override
    public void onClick(View v) {

    }

    public FloatNotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FloatNotificationView(Context context) {
        super(context);

        setDrawingCacheEnabled(false);
        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.float_view, this, false);
        //final int width = DeviceUtil.getScreenWidth() - DimensUtil.dip2px(context, 18);
        //final int height = DimensUtil.dip2px(context, 180);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(view);
        init(context);
        mContext = context;
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    private void init(Context context) {
        mProgressInfoTv = (ImageView) findViewById(R.id.progress_info);
        mProgressInfoTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endDismissAnimation(false);
                Utils.launchMySelf();
            }
        });
        ViewConfiguration configuration = ViewConfiguration
                .get(context);
        touchSlop = ViewConfigurationCompat
                .getScaledPagingTouchSlop(configuration);
        density = context.getResources().getDisplayMetrics().density;
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
    }

    public void setProgress(int progress) {
    }

    public void setWindowManager(WindowManager wm) {
        mWm = wm;
    }

    public void setLayoutParams(WindowManager.LayoutParams params) {
        mParams = params;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        final float x = event.getRawX();
        final float y = event.getRawY();
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRawX = x;
                mRawY = y;
                mInitRootY = mParams.y;
                mInitRootX = mParams.x;
                mPerformDrag = false;
                mPerformDismiss = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float xdiff = Math.abs(x - mRawX);
                final float ydiff = Math.abs(y - mRawY);

                if (ydiff > touchSlop || xdiff > touchSlop) {
                    mPerformDrag = true;
                    performDrag(y - mRawY, x - mRawX);
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mPerformDismiss) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000,
                            mMaximumVelocity);
                    int activePointerId = MotionEventCompat.getPointerId(
                            event, 0);
                    int initialVelocity = (int) VelocityTrackerCompat
                            .getYVelocity(velocityTracker, activePointerId);
                    final int totalDelta = (int) (x - mRawX);
                    int endPosition = determineTargetPosition(
                            initialVelocity, totalDelta);

                    if (endPosition == 1) {
                        endDismissAnimation(true);
                    } else {
                        float xdiff2 = ViewHelper.getX(this);
                        endDismissAnimation(Math.abs(xdiff2) > getWidth() / 2);
                    }
                } else if (mPerformDrag) {
                    if (mParams != null) {
//                        DuBus.getDefault().execute(new Runnable() {
//                            @Override
//                            public void run() {
//                                PreferenceUtil.saveYCordinate(mParams.y);
//                            }
//                        });
                    }
                }

                mPerformDrag = false;
                mPerformDismiss = false;

                break;
        }
        return true;
    }

    private void endDismissAnimation(boolean dismiss) {

        mWm.removeViewImmediate(this);
    }

    private int determineTargetPosition(int velocity, int deltaX) {
        int targetPosition = 0;
        if (Math.abs(deltaX) > mFlingDistance
                && Math.abs(velocity) > mMinimumVelocity) {
            targetPosition = 1;
        }
        return targetPosition;
    }

    private void performDismiss(float xDiff) {
//        float x = mInitRootX + xDiff;
//        ViewHelper.setX(this, x);
//        float diff = Math.abs(x);
//        float alpha = 1.0f - diff / mWidth;
//        ViewHelper.setAlpha(this, alpha);
        int x = mInitRootX + (int) xDiff;
        mParams.x = x;

        if (getParent() != null) {
            if (isPhoneCouldMove(mParams)) {
                mWm.updateViewLayout(this, mParams);
            }
        }
    }

    private void performDrag(float diffY, float diffX) {
        int y = mInitRootY + (int) diffY;
        int x = mInitRootX + (int) diffX;
        mParams.y = y;
        mParams.x = x;
        LogUtil.v("fan10", "perforDrag:" + mParams.x + ":" + mParams.y);
        if (getParent() != null) {
            if (isPhoneCouldMove(mParams)) {
                mWm.updateViewLayout(this, mParams);
            }
        }
    }


    // 判断是否到屏幕最上方
    private boolean isPhoneCouldMove(WindowManager.LayoutParams newParams) {
//        if (newParams.y >= DeviceUtil.getStatusBarHeight(getContext()) + DimensUtil.dip2px(5)) {
//            return true;
//        }
//        return false;
        return true;
    }
}
