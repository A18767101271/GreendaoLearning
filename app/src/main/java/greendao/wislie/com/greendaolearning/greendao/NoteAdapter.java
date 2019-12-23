package greendao.wislie.com.greendaolearning.greendao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import greendao.wislie.com.greendaolearning.R;
import greendao.wislie.com.greendaolearning.slide.SlideLayout;
import greendao.wislie.com.greendaolearning.util.BaseDiffCallback;

import java.util.List;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/12/2110:33 PM
 * desc   : note列表
 * version: 1.0
 */
public class NoteAdapter extends BaseQuickAdapter<Note, BaseViewHolder> {

    public NoteAdapter(int layoutResId, @Nullable List<Note> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Note note) {

        helper.setText(R.id.textViewNoteText, note.getText());
        helper.setText(R.id.textViewNoteComment, note.getComment());

        helper.addOnClickListener(R.id.tv_note_delete); //监听点击"删除"
        helper.addOnClickListener(R.id.tv_note_detail); //监听点击"详情"
        SlideLayout slideLayout = helper.getView(R.id.slide_layout);

        slideLayout.setSlideChangeListener(new SlideLayout.SlideChangeListener() {
            @Override
            public boolean shouldExpandLayout(View child) {
                //判断哪个view处于点击状态
                if (child != null) {
                    switch (child.getId()) {
                        case R.id.tv_note_delete:
                            return false;
                        case R.id.tv_note_detail:
                            return true;
                    }
                }
                return false;
            }

            @Override
            public boolean shouldDispatchEventToChild(View child) {
                if (child != null) {
                    switch (child.getId()) {
                        case R.id.tv_note_delete:
                        case R.id.tv_note_detail:
                            return true;
                    }
                }
                return false;
            }
        });
    }

    public void setData(List<Note> newItemList) {
        // 获取DiffResult结果
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new BaseDiffCallback<Note>(mData, newItemList) {

            @Override
            public boolean areItemsTheSame(Note lastItem, Note newItem) {
                return lastItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(Note lastItem, Note newItem) {
                return lastItem == newItem;
            }
        });

        //按照DiffResult定义好的逻辑，更新数据
        mData.clear();
        mData.addAll(newItemList);
        //使用DiffResult分发给adapter热更新
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * 根据下标删除数据
     *
     * @param position
     */
    public void delete(int position) {
        if (position >= getItemCount()) return;
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, getItemCount());
    }
}
