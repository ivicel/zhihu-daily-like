package github.ivicel.zhihustory;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Ivicel on 18/10/2017.
 */

public class IndicatorViewPager extends ViewPager {
    private static final String TAG = "IndicatorViewPager";
    private static final int DEFAULT_DURATION = 2500;

    /* indicator should be a state drawable with a selected/unselected state */
    private Drawable mIndicatorDrawable;
    /* visible view's width */
    private int mItemViewWidth;
    /* visible view's height */
    private int mItemViewHeight;
    /* indicator's width */
    private int mIndicatorWidth;
    /* indicator's height */
    private int mIndicatorHeight;
    /* the distance between indicators */
    private int mIntervalWidth;
    /* numbers of indicators */
    private int mIndicatorCount;
    /* left position of first indicator relative to current item view */
    private float mIndicatorsLeft;
    /* top position of first indicator relative to current item view */
    private float mIndicatorsTop;
    /* left position of first indicator relative to viewpager */
    private float mIndicatorOffsetLeft;
    /* left position of activated indicator relative to viewpager */
    private float mActivatedIndicatorOffsetLeft;
    /* last swipe to right index, this will not change when swipe to left */
    private int mPosition;


    public IndicatorViewPager(Context context) {
        this(context, null);
    }

    public IndicatorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        FixedScrollerDuration fixedScroller = new FixedScrollerDuration(getContext());
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, fixedScroller);
        } catch (NoSuchFieldException | IllegalAccessException e) {}

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.IndicatorViewPager);
            int N = typedArray.getIndexCount();
            for (int i = 0; i < N; i++) {
                switch (typedArray.getIndex(i)) {
                    case R.styleable.IndicatorViewPager_indicatorSrc:
                        mIndicatorDrawable = typedArray.getDrawable(i);
                        break;
                    case R.styleable.IndicatorViewPager_scrollDuration:
                        int duration = typedArray.getInt(i, DEFAULT_DURATION);
                        fixedScroller.setDuration(duration);
                        break;
                }
            }
            typedArray.recycle();
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mItemViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mItemViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setOnIndicatorMeasure();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        /* draw unselected indicators */
        canvas.save();
        canvas.translate(mIndicatorOffsetLeft, mIndicatorsTop);

        for (int i = 0; i < mIndicatorCount; i++) {
            mIndicatorDrawable.setState(EMPTY_STATE_SET);
            mIndicatorDrawable.draw(canvas);
            canvas.translate(mIndicatorWidth + mIntervalWidth, 0);
        }
        canvas.restore();

        /*
         * now we will draw the selected indicator,
         * check has any item view added first
         * */
        if (mIndicatorCount > 0) {
            canvas.save();
            canvas.translate(mActivatedIndicatorOffsetLeft, mIndicatorsTop);
            mIndicatorDrawable.setState(SELECTED_STATE_SET);
            mIndicatorDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);

        if (mPosition >= position) {
            mIndicatorOffsetLeft =
                    mIndicatorsLeft +
                    offsetPixels +
                    mItemViewWidth * position;

            mActivatedIndicatorOffsetLeft =
                    mIndicatorOffsetLeft +
                    (mIntervalWidth + mIndicatorWidth) *
                    (offset + position);
        } else {
            mIndicatorOffsetLeft = mItemViewWidth * position + mIndicatorsLeft;
            mPosition = position;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (getAdapter() != null) {
            getAdapter().unregisterDataSetObserver(mDataObserver);
        }
        removeCallbacks(mAutoScrollTask);
        removeOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getAdapter() != null) {
            getAdapter().registerDataSetObserver(mDataObserver);
        }
        addOnPageChangeListener(mPageChangeListener);
        postDelayed(mAutoScrollTask, DEFAULT_DURATION);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);

        if (adapter != null) {
            mIndicatorCount = adapter.getCount();
            mPosition = getCurrentItem();
        }
    }


    private void setOnIndicatorMeasure() {
        mIndicatorWidth = mIndicatorDrawable.getIntrinsicWidth();
        mIndicatorHeight = mIndicatorDrawable.getIntrinsicWidth();
        mIndicatorDrawable.setBounds(0, 0, mIndicatorWidth, mIndicatorHeight);

        if (mIntervalWidth == 0) {
            mIntervalWidth = mIndicatorWidth * 3;
        }

        float indicatorsGroupWidth = (mIndicatorCount - 1) * (mIndicatorWidth + mIntervalWidth) +
                mIndicatorWidth;
        mIndicatorsLeft = (mItemViewWidth - indicatorsGroupWidth) / 2;
        mIndicatorsTop = (mItemViewHeight ) * 0.85f;

        mIndicatorOffsetLeft = getCurrentItem() * mItemViewWidth + mIndicatorsLeft;
        mActivatedIndicatorOffsetLeft = mIndicatorOffsetLeft + getCurrentItem() * (mIntervalWidth +
                mIndicatorWidth);
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mIndicatorCount = getAdapter().getCount();
        }
    };

    private class FixedScrollerDuration extends Scroller {
        private int mDuration = DEFAULT_DURATION;

        FixedScrollerDuration(Context context) {
            super(context);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        void setDuration(int duration) {
            mDuration = duration;
        }
    }

    private Runnable mAutoScrollTask = new Runnable() {
        @Override
        public void run() {
            int position = (mPosition + 1) % mIndicatorCount;
            setCurrentItem(position);
        }
    };

    private OnPageChangeListener mPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case SCROLL_STATE_DRAGGING:
                case SCROLL_STATE_SETTLING:
                    removeCallbacks(mAutoScrollTask);
                    break;
                case SCROLL_STATE_IDLE:
                    mPosition = getCurrentItem();
                    postDelayed(mAutoScrollTask, DEFAULT_DURATION);
                    break;
            }
        }
    };
}
