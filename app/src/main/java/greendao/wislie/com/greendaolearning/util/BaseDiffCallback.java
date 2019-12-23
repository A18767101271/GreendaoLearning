package greendao.wislie.com.greendaolearning.util;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/12/21 5:18 PM
 * desc   : 用于判断recyclerView列表中的数据是否有变化
 * version: 1.0
 */
public abstract class BaseDiffCallback<T> extends DiffUtil.Callback {

    private List<T> mLastList;
    private List<T> mNewList;

    public BaseDiffCallback(List<T> lastList, List<T> newList) {
        mLastList = lastList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mLastList == null ? 0 : mLastList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList == null ? 0 : mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        T lastFlesh = mLastList.get(i);
        T newFlesh = mNewList.get(i1);
        return areItemsTheSame(lastFlesh, newFlesh );
    }


    @Override
    public boolean areContentsTheSame(int i, int i1) {
        T lastFlesh = mLastList.get(i);
        T newFlesh = mNewList.get(i1);
        return areContentsTheSame(lastFlesh, newFlesh);
    }

    /**
     * 判断两者是否是相同的item
     * @param lastItem
     * @param newItem
     * @return
     */
    public abstract boolean areItemsTheSame(T lastItem, T newItem);


    /**
     * 判断两者的content是否相同
     * @param lastItem
     * @param newItem
     * @return
     */
    public abstract boolean areContentsTheSame(T lastItem, T newItem);
}
