package greendao.wislie.com.greendaolearning.greendao;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import greendao.wislie.com.greendaolearning.R;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/12/2311:26 AM
 * desc   : greendao基本操作学习
 * version: 1.0
 */
public class NoteLearningActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_learning);
        ButterKnife.bind(this);
        initToolBar(true,getString(R.string.basic_knowledge_title));
    }

    /**
     * 初始化 Toolbar
     */
    private void initToolBar(boolean homeAsUpEnabled, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnabled);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setNavigationIcon(R.mipmap.icon_back);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
