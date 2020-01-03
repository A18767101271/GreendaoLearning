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
 * desc   : 侧滑SlideLayout
 * version: 1.0 需要在down的时候将之前的remove掉
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
    private int mCouldScrollMaxWidth;
    /**
     * true表示向右滑动
     **/
    private boolean mSlideRight = false;
    /**
     * 子视图一半的宽度
     */
    private int mHalfBlockWidth;
    /**
     * 速度追踪器
     **/
    private VelocityTracker mVelocityTracker;
    /**
     * 滚动工具
     **/
    private Scroller mScroller;
    /**
     * 可滑动子视图集合
     **/
    private List<View> mSlideViews = new ArrayList<>();

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
        mCouldScrollMaxWidth = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            if (i == getChildCount() - 1) {
                ViewGroup slideLayout = getSlideLayout();
                if (slideLayout == null) break;
                mSlideViews.clear();
                for (int j = 0; j < slideLayout.getChildCount(); j++) {
                    View slideChild = slideLayout.getChildAt(j);
                    if (slideChild.getVisibility() != View.VISIBLE) {
                        continue;
                    }
                    mSlideViews.add(slideChild);
                    int slideWidth = slideChild.getMeasuredWidth();
                    mHalfBlockWidth = slideWidth / 2;
                    mCouldScrollMaxWidth += slideWidth;
                    measuredWidth += slideWidth;
                }
            } else {
                measuredWidth += child.getMeasuredWidth();

            }
            measuredHeight = child.getMeasuredHeight();
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private ViewGroup getSlideLayout() {
        if (getChildCount() != 0) {
            View child = getChildAt(getChildCount() - 1);
            if (child instanceof ViewGroup) return (ViewGroup) child;
        }
        return null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int startLeft = 0;
            //子视图排列在同一行
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != VISIBLE) {
                    continue;
                }
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

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                mLastX = ev.getX();
                mLastY = ev.getY();

                //如果存在其他没关闭的,则关闭
                if(SlideManager.INSTANCE.isCloseAllExceptThis(this)){
                    return true;
                }else{
                    View child = getClickView(ev);
                    //侧滑打开的情况
                    if (mSlideChangeListener == null || !mSlideChangeListener.shouldExpandLayout(child)) {
                        //判断是否应该把事件传递给子View
                        if (mSlideChangeListener != null && mSlideChangeListener.shouldDispatchEventToChild(child)) {
                            return super.dispatchTouchEvent(ev);
                        }
                        //表示向右滑动
                        mSlideRight = true;
                        return true;
                    }
                }
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
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
        obtainVelocityTracker(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                if (Math.abs(x - mLastX) > mTouchSlop && Math.abs(x - mLastX) > Math.abs(y - mLastY)) {
                    //防止事件被父布局拦截
                    requestDisallowInterceptTouchEvent(true);
                }
                //x的值变大,说明向右滑动
                int deltaX = (int) -(x - mLastX);
                mSlideRight = deltaX <= 0;
                mLastX = x;
                scrollBy(deltaX, 0);
                return true;
            case MotionEvent.ACTION_UP:
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) mVelocityTracker.getXVelocity();
                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    fling(-initialVelocity);
                } else {
                    //滑动到最左边或者最右边
                    scrollToBorder();
                }
                releaseVelocityTracker();
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                scrollToBorder();
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
        for (int i = 0; i < mSlideViews.size(); i++) {
            View child = mSlideViews.get(i);
            RectF rect = DensityUtil.calcViewScreenLocation(child);
            boolean isInViewRect = rect.contains(ev.getRawX(), ev.getRawY());
            if (isInViewRect) {
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
                scrollToBorder();
            }
            //继续调用computeScroll方法
            invalidate();
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (x >= mCouldScrollMaxWidth) {
            x = mCouldScrollMaxWidth;
        } else if (x < 0) {
            x = 0;
        }
        if (getScrollX() != x) {
            super.scrollTo(x, y);
        }
    }

    /**
     * 滑动到边界
     */
    private void scrollToBorder() {
        int scrollX = getScrollX();
        int dx = 0;

        if (scrollX < 1) {
            dx = -scrollX;
            scrollBy(dx, 0);
            SlideManager.INSTANCE.remove(this);
            return;
        } else if (scrollX > mCouldScrollMaxWidth - 1 && scrollX <= mCouldScrollMaxWidth) {
            if (!mSlideRight) {  //向左边滑动
                dx = mCouldScrollMaxWidth - scrollX;
                scrollBy(dx, 0);
                SlideManager.INSTANCE.add(this);
                return;
            } else { //比如点击空白处
                dx = -scrollX;
                SlideManager.INSTANCE.remove(this); //这么写会欠考虑
            }
        } else if (scrollX >= 1 && scrollX <= mCouldScrollMaxWidth - 1) {

            if (!mSlideRight) { //向左边滑动
                if (scrollX < mHalfBlockWidth / 2) {
                    dx = -scrollX;
                } else {
                    dx = mCouldScrollMaxWidth - scrollX;
                }
                SlideManager.INSTANCE.add(this);
            } else { //向右边滑动
                if (scrollX <= mCouldScrollMaxWidth - mHalfBlockWidth / 2) {
                    dx = -scrollX;
                } else {
                    dx = mCouldScrollMaxWidth - scrollX;
                }
                SlideManager.INSTANCE.remove(this);
            }
        }else{
            Log.i(TAG, hashCode()+" scrollToBorder else 1..");
        }
        mScroller.startScroll(scrollX, 0, dx, 0, NORMAL_CLOSE_DURATION);
        invalidate();
    }

    /**
     * 抛
     *
     * @param velocityX
     */
    public void fling(int velocityX) {
        mScroller.fling(getScrollX(), 0, velocityX, 0, 0,
                mCouldScrollMaxWidth, 0, 0);
        awakenScrollBars(mScroller.getDuration());

        int finalX = mScroller.getFinalX();
        if(finalX < 1){
            SlideManager.INSTANCE.remove(this);
        }else if (finalX > mCouldScrollMaxWidth - 1 && finalX <= mCouldScrollMaxWidth) {
            if (!mSlideRight) {
                SlideManager.INSTANCE.add(this);
            }else{
                SlideManager.INSTANCE.remove(this);
            }
        }else if (finalX >= 1 && finalX <= mCouldScrollMaxWidth - 1) {
            if (!mSlideRight) {
                SlideManager.INSTANCE.add(this);
            }else{
                SlideManager.INSTANCE.remove(this);
            }
        }
        invalidate();
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
            int dx = -getScrollX();
            int duration = isQuickClose ? QUICK_CLOSE_DURATION : NORMAL_CLOSE_DURATION;
            mScroller.startScroll(getScrollX(), 0, dx, 0, duration);
            invalidate();
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
         * @return true表示会将事件传递到子View, false表示不会将事件传递到子View
         */
        boolean shouldDispatchEventToChild(View child);
    }
}
