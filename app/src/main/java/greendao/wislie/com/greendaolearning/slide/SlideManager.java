package greendao.wislie.com.greendaolearning.slide;

import java.util.ArrayList;
import java.util.List;

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

    public SlideManager() {
        mSlideList = new ArrayList<>();
    }

    /**
     * 判断当前slideLayout是否关闭
     *
     * @param slideLayout
     * @return true表示侧滑打开, false表示侧滑未打开
     */
    public boolean isClosed(SlideLayout slideLayout) {
        if (mSlideList.size() == 0) return true;
        if (!contains(slideLayout)) return true;
        return false;
    }

    /**
     *
     * 判断是否都关闭
     * @return
     */
    public boolean isClosedAll() {
        if (mSlideList.size() == 0) return true;
        boolean isClosed = true;
        for (int i = 0; i < mSlideList.size(); i++) {
            if (!isClosed(mSlideList.get(i))) {
                isClosed = false;
                break;
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

    /**
     * 正常关闭所有的
     */
    public void closeAll() {
        for (int i = mSlideList.size() - 1; i >= 0; i--) {
            SlideLayout sl = mSlideList.get(i);
            sl.close(false);
        }
    }

    /**
     * 快速关闭所有的
     */
    public void quickCloseAll(){
        for (int i = mSlideList.size() - 1; i >= 0; i--) {
            SlideLayout sl = mSlideList.get(i);
            sl.close(true);
        }
    }

    /**
     * 删除所有的
     */
    public void removeAll(){
        if(mSlideList.size() > 0) mSlideList.clear();
    }
}
