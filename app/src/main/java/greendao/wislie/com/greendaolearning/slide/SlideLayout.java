package greendao.wislie.com.greendaolearning.slide;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;


/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/11/13 11:05 AM
 * desc   : 侧滑SlideLayout 参考 https://github.com/Dsiner/SlideLayout/blob/master/lib/src/main/java/com/d/lib/slidelayout/SlideLayout.java
 * version: 1.0
 */
public class SlideLayout extends ViewGroup {

    private static final String TAG = SlideLayout.class.getSimpleName();
    /**
     * 快速点击时长
     */
    private static final int QUICK_CLOSE_DURATION = 15;
    /**
     * 正常点击时长
     */
    private static final int NORMAL_CLOSE_DURATION = 500;
    /**
     * 可移动的临界值
     **/
    private int mTouchSlop;
    /**
     * 滑动的最小速度
     **/
    private int mMinimumVelocity;
    /**
     * 滑动的最大速度
     **/
    private int mMaximumVelocity;
    /**
     * 点击的坐标
     **/
    private float mLastX, mLastY;
    /**
     * 可以滑动的最大距离
     **/
    private int mScrollMaxWidth;
    /**
     * true表示向右滑动
     **/
    private boolean mSlideRight = false;
    /**
     * 分别表示可滑动视图左侧子视图的宽度和右侧子视图的宽度
     **/
    private int leftChildWidth, rightChildWidth;
    /**
     * 速度追踪器
     **/
    private VelocityTracker mVelocityTracker;
    /**
     * true表示打开侧滑, false表示关闭侧滑
     **/
    private boolean isExpanded = false;
    /**
     * 滚动工具
     **/
    private Scroller mScroller;
    /**
     * 滑动时长
     */
    private long mScrollDuration;
    /**
     * 可滑动子视图集合
     **/
    private List<View> mChildViews = new ArrayList<>();

    public SlideLayout(Context context) {
        this(context, null);
    }

    public SlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ///////////// 设置长宽 /////////////
        int measuredWidth = 0, measuredHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            measuredWidth += child.getMeasuredWidth();
            measuredHeight = child.getMeasuredHeight();
        }
        setMeasuredDimension(measuredWidth, measuredHeight);

        View lastView = getChildAt(getChildCount() - 1);
        if (lastView != null) {
            if (lastView instanceof ViewGroup) {
                ViewGroup parent = (ViewGroup) lastView;

                mChildViews.clear();
                //添加子View
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    if (i == 0) {
                        leftChildWidth = child.getMeasuredWidth();
                    } else if (i == parent.getChildCount() - 1) {
                        rightChildWidth = child.getMeasuredWidth();
                    }
                    mChildViews.add(child);
                }
            }
            mScrollMaxWidth = lastView.getMeasuredWidth();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int startLeft = 0;
            //子视图排列在同一行
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int left = startLeft;
                int right = left + child.getMeasuredWidth();
                int bottom = child.getMeasuredHeight();
                startLeft += right;
                child.layout(left, 0, right, bottom);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                obtainVelocityTracker(ev);
                mLastX = ev.getX();
                mLastY = ev.getY();
                if (!SlideManager.INSTANCE.isClosedAll()) {
                    View child = getClickView(ev);
                    //侧滑打开的情况
                    if (mSlideChangeListener == null || !mSlideChangeListener.shouldExpandLayout(child)) {
                        mSlideRight = true; //向右
                        //关闭侧滑
                        SlideManager.INSTANCE.closeAll();
                        SlideManager.INSTANCE.removeAll();

                        //判断是否应该把事件传递给子View
                        if (mSlideChangeListener != null && mSlideChangeListener.shouldDispatchEventToChild(child)) {
                            return super.dispatchTouchEvent(ev);
                        }
                        Log.i(TAG, "dispatchTouchEvent..");
                        return true;
                        //继续往下执行
                    }
                    Log.i(TAG, "点击详情..");
                } else {
                    Log.i(TAG, "侧滑处于关闭..");
                    return true;
                }

        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE: //事件拦截
                float x = ev.getX();
                float y = ev.getY();
                //同时满足x偏移量大于临界值mTouchSlop, x偏移量大于y偏移量
                if (Math.abs(x - mLastX) > mTouchSlop && Math.abs(x - mLastX) > Math.abs(y - mLastY)) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                if (Math.abs(x - mLastX) > mTouchSlop && Math.abs(x - mLastX) > Math.abs(y - mLastY)) {
                    //防止事件被父布局拦截
                    requestDisallowInterceptTouchEvent(true);
                }
                int deltaX = (int) -(x - mLastX);
                scrollBy(deltaX, 0);
                mLastX = x;
                //x的值变大,说明向右滑动
                mSlideRight = x > mLastX;
                return true;
            case MotionEvent.ACTION_UP:
                VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) velocityTracker.getXVelocity();
                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    fling(-initialVelocity);
                } else {
                    //滑动到最左边或者最右边
                    scrollToEdge();
                }
                releaseVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                releaseVelocityTracker();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取点击的子view
     *
     * @param ev
     * @return
     */
    private View getClickView(MotionEvent ev) {
        for (int i = 0; i < mChildViews.size(); i++) {
            View child = mChildViews.get(i);
            RectF rect = DensityUtil.calcViewScreenLocation(child);
            boolean isInViewRect = rect.contains(ev.getRawX(), ev.getRawY());
            if (isInViewRect) {
                Log.i(TAG, "child position:");
                return child;
            }
        }
        return null;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            if (!mScroller.computeScrollOffset()) { //滚动结束
                Log.i(TAG, "滚动结束...");
                if (isExpanded) {
                    SlideManager.INSTANCE.add(this);
                } else {
                    SlideManager.INSTANCE.remove(this);
                }
            }
            //继续调用computeScroll方法
            postInvalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x >= mScrollMaxWidth) {
            x = mScrollMaxWidth;
        } else if (x < 0) {
            x = 0;
        }
        if (getScrollX() != x) {
            super.scrollTo(x, y);
        }
    }

    /**
     * 滑动到边缘
     */
    private void scrollToEdge() {
        int dx;
        if (mSlideRight) { //向右滑动
            dx = mScrollMaxWidth - getScrollX();
            if (dx < rightChildWidth / 2) {
                if (dx < 1) {
                    scrollBy(dx, 0);
                    return;
                }
            } else {
                dx = -getScrollX();
                isExpanded = false;
            }
        } else { //向左滑动
            if (getScrollX() < leftChildWidth / 2) {
                dx = -getScrollX();
                if (getScrollX() < 1) {
                    scrollBy(dx, 0);
                    return;
                }
            } else {
                dx = mScrollMaxWidth - getScrollX();
                isExpanded = true;
            }
        }
        mScroller.startScroll(getScrollX(), 0, dx, 0, NORMAL_CLOSE_DURATION);
        invalidate();
    }

    /**
     * 抛
     *
     * @param velocityX
     */
    public void fling(int velocityX) {
        if (getChildCount() > 0) {
            mScroller.fling(getScrollX(), 0, velocityX, 0, 0,
                    mScrollMaxWidth, 0, 0);
            awakenScrollBars(mScroller.getDuration());
            invalidate();
        }
    }

    private void obtainVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * 关闭当前的
     */
    public void close(boolean isQuickClose) {
        if (isExpanded) {
            isExpanded = false;
            int dx = -getScrollX();
            int duration = isQuickClose ? QUICK_CLOSE_DURATION : NORMAL_CLOSE_DURATION;
            mScroller.startScroll(getScrollX(), 0, dx, 0, duration);
            invalidate();
        }
    }

    private SlideChangeListener mSlideChangeListener;

    public void setSlideChangeListener(SlideChangeListener slideChangeListener) {
        this.mSlideChangeListener = slideChangeListener;
    }

    /**
     * 状态改变监听器
     */
    public interface SlideChangeListener {
        /**
         * 是否打开侧滑布局
         *
         * @param child 点击的子视图
         * @return true表示打开侧滑布局，false表示关闭侧滑布局
         */
        boolean shouldExpandLayout(View child);

        /**
         * 是否应该把事件传递给子View
         *
         * @param child slideLayout中的子布局
         * @return true表示会将事件传递到子View,false表示不会将事件传递到子View
         */
        boolean shouldDispatchEventToChild(View child);
    }
}
