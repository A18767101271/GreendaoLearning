package greendao.wislie.com.greendaolearning.slide;

import android.util.Log;

import java.util.*;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/11/13 13:05 PM
 * desc   : 侧滑SlideLayout管理器
 * version: 1.0
 */
public class SlideManager {

    public static final SlideManager INSTANCE = new SlideManager();
    private List<SlideLayout> mSlideList;
    //是否在滑动
    private Map<SlideLayout, Boolean> mSlidingMap = new HashMap<>();

    public SlideManager() {
        mSlideList = new ArrayList<>();
    }

    /**
     *
     * @param slideLayout
     * @return
     */
    public boolean isCloseAllExceptThis(SlideLayout slideLayout){
        boolean isClosed = false;
        Iterator<SlideLayout> iterator = mSlideList.iterator();
        while (iterator.hasNext()) {
            SlideLayout sl = iterator.next();
            if (sl != slideLayout) {
                Log.i("SlideLayout", "isCloseAllExceptThis: 关闭..");
                isClosed = true;
                sl.close(false);
                mSlideList.remove(sl);
            }
        }
        return isClosed;
    }

    /**
     * 移除slideLayout
     *
     * @param slideLayout
     */
    public void remove(SlideLayout slideLayout) {
        if (contains(slideLayout)) {
            mSlideList.remove(slideLayout);
        }
    }

    /**
     * 添加slideLayout
     *
     * @param slideLayout
     */
    public void add(SlideLayout slideLayout) {
        if (!contains(slideLayout)) {
            mSlideList.add(slideLayout);
        }
    }

    /**
     * 检查是否包含 slideLayout
     *
     * @param slideLayout
     * @return
     */
    private boolean contains(SlideLayout slideLayout) {
        boolean contains = false;
        for (int i = 0; i < mSlideList.size(); i++) {
            SlideLayout sl = mSlideList.get(i);
            if (sl == slideLayout) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public void close(SlideLayout slideLayout){
        Iterator<SlideLayout> iterator = mSlideList.iterator();
        while (iterator.hasNext()) {
            SlideLayout sl = iterator.next();
            if (sl == slideLayout) {
                sl.close(false);
            }
        }
    }

    public int size() {
        return mSlideList.size();
    }

    public void put(SlideLayout slideLayout, boolean isSliding) {
        mSlidingMap.put(slideLayout, isSliding);
    }

}
