package greendao.wislie.com.greendaolearning.greendao;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.chad.library.adapter.base.BaseQuickAdapter;
import greendao.wislie.com.greendaolearning.GlobalApp;
import greendao.wislie.com.greendaolearning.R;
import greendao.wislie.com.greendaolearning.gen.DaoSession;
import greendao.wislie.com.greendaolearning.gen.NoteDao;
import greendao.wislie.com.greendaolearning.util.DividerItemDecoration;
import org.greenrobot.greendao.query.Query;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/12/215:18 PM
 * desc   : note列表
 * version: 1.0
 */
public class NoteActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.buttonAdd)
    Button addBtn;
    @BindView(R.id.editTextNote)
    EditText noteEt;
    @BindView(R.id.rv_note)
    RecyclerView noteRv;

    private NoteAdapter mAdapter;
    private List<Note> mDataList = new ArrayList<>();

    private NoteDao noteDao;
    private Query<Note> noteQuery;

    private String TAG = NoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        initView();
        DaoSession daoSession = GlobalApp.getApp().getDaoSession();
        noteDao = daoSession.getNoteDao();
        noteQuery = noteDao.queryBuilder().orderAsc(NoteDao.Properties.Text).build();
        queryNoteList();

    }

    private void initView() {
        toolbar.setTitle(getString(R.string.list_title));
        setSupportActionBar(toolbar);
        addBtn.setEnabled(false);
        noteEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    addBtn.setEnabled(true);
                } else {
                    addBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        noteRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        noteRv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //note 适配器
        mAdapter = new NoteAdapter(R.layout.item_note, mDataList);
        noteRv.setAdapter(mAdapter);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_note_delete:
                        deleteNote(position);
                        break;
                    case R.id.tv_note_detail:
                        startActivity(new Intent(NoteActivity.this, NoteLearningActivity.class));
                        break;
                    case R.id.tv_note_top: //置顶
                        mAdapter.smoothToTop(position);
                        break;
//                    case R.id.tv_cancel_note_top: //取消置顶
//                        mAdapter.cancelToTop();
//                        break;
                }
            }
        });
    }


    @OnClick({R.id.buttonAdd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonAdd:
                addNote();
                break;
        }
    }

    //查询note列表
    private void queryNoteList() {
        List<Note> noteList = noteQuery.list();
        mAdapter.setData(noteList);
    }

    //添加note
    private void addNote() {
        String text = noteEt.getText().toString();
        noteEt.setText("");
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
        String comment = "Added on" + df.format(new Date());

        Note note = new Note();
        note.setText(text);
        note.setDate(new Date());
        note.setComment(comment);
        long rowId = noteDao.insert(note);
        Log.i(TAG, "添加的rowId：" + rowId);
        mAdapter.addData(note);
    }

    //删除note
    private void deleteNote(int position) {
        Note note = mAdapter.getItem(position);
        noteDao.delete(note);
        mAdapter.delete(position);
    }

}
