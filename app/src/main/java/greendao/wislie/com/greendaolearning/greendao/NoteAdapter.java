package greendao.wislie.com.greendaolearning.greendao;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    private RecyclerView mRv;

    public NoteAdapter(RecyclerView rv, int layoutResId, @Nullable List<Note> data) {
        super(layoutResId, data);
        mRv = rv;
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, Note note) {

        helper.setText(R.id.textViewNoteText, note.getText());
        helper.setText(R.id.textViewNoteComment, note.getComment());

        helper.addOnClickListener(R.id.tv_note_delete); //监听点击"删除"
        helper.addOnClickListener(R.id.tv_note_detail); //监听点击"详情"
        helper.addOnClickListener(R.id.tv_note_top); //监听点击"置顶"
//        helper.addOnClickListener(R.id.tv_cancel_note_top); //监听点击"取消置顶"

        if (note.getTop()) {
//            helper.setVisible(R.id.tv_cancel_note_top, true);
//            helper.setVisible(R.id.tv_note_top, false);
            helper.setText(R.id.tv_note_top, "取消置顶");
        } else {
//            helper.setVisible(R.id.tv_cancel_note_top, false);
//            helper.setVisible(R.id.tv_note_top, true);
            helper.setText(R.id.tv_note_top, "置顶");
        }

        final SlideLayout slideLayout = helper.getView(R.id.slide_layout);

        slideLayout.setSlideChangeListener(new SlideLayout.SlideChangeListener() {
            @Override
            public boolean shouldExpandLayout(View child) {
                //判断哪个view处于点击状态
                if (child != null) {
                    switch (child.getId()) {
                        case R.id.tv_note_delete:
                        case R.id.tv_note_top:
//                        case R.id.tv_cancel_note_top:
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
                        case R.id.tv_note_top:
//                        case R.id.tv_cancel_note_top:
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

        View slideLayout = getViewByPosition(mRv, position,R.id.slide_layout);
        if(slideLayout instanceof SlideLayout){
            ((SlideLayout) slideLayout).close(false);
        }
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position, getItemCount());
    }

    /**
     * 置顶 或 取消置顶
     *
     * @param position
     */
    public void smoothToTop(int position) {
        Note note = getItem(position);

        if(!note.getTop()){ //未置顶
            smoothToTop(note, position);
        }else{
            cancelToTop();
        }
    }

    /**
     * 置顶
     * @param note
     * @param position
     */
    private void smoothToTop(Note note,int position){
        note.setTop(true);
        note.setLastIndex(position);
        //先删除
        delete(position);

        Note topNote = getItem(0);
        topNote.setTop(false);
        notifyItemChanged(0);
        //最后置顶
        note.setCurIndex(0);
        addData(0, note);
    }

    /**
     * 取消置顶
     */
    public void cancelToTop() {
        Note topNote = getItem(0);
        topNote.setTop(false);
        //先删除
        delete(0);
        //将此note更换到原来的位置

        int insertIndex = topNote.getLastIndex();
        if (topNote.getLastIndex() > getItemCount()) {
            insertIndex = getItemCount() - 1;
            topNote.setLastIndex(insertIndex);
        }
        topNote.setCurIndex(insertIndex);
        addData(insertIndex, topNote);

    }
}
