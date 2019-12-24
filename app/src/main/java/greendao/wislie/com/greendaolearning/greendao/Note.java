package greendao.wislie.com.greendaolearning.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author : wislie
 * e-mail : 254457234@qq.com
 * date   : 2019/12/21 10:26 AM
 * desc   : note类映射数据库中的note表
 * version: 1.0
 */
@Entity(indexes = {
        @Index(value = "text, date DESC", unique = true)
})
public class Note {

    @Id(autoincrement = true)
    private Long id;

    //输入内容
    @NotNull
    private String text;
    //什么时候添加
    private Date date;
    //添加时间
    private String comment;
    //是否置顶
    private Boolean top = Boolean.FALSE;
    //上一次的下标
    private Integer lastIndex = -1;
    //下标
    private Integer curIndex = -1;

@Generated(hash = 1932874470)
public Note(Long id, @NotNull String text, Date date, String comment,
        Boolean top, Integer lastIndex, Integer curIndex) {
    this.id = id;
    this.text = text;
    this.date = date;
    this.comment = comment;
    this.top = top;
    this.lastIndex = lastIndex;
    this.curIndex = curIndex;
}

@Generated(hash = 1272611929)
public Note() {
}

public Long getId() {
    return this.id;
}

public void setId(Long id) {
    this.id = id;
}

public String getText() {
    return this.text;
}

public void setText(String text) {
    this.text = text;
}

public Date getDate() {
    return this.date;
}

public void setDate(Date date) {
    this.date = date;
}

public String getComment() {
    return this.comment;
}

public void setComment(String comment) {
    this.comment = comment;
}

public Boolean getTop() {
    return this.top;
}

public void setTop(Boolean top) {
    this.top = top;
}

public Integer getLastIndex() {
    return this.lastIndex;
}

public void setLastIndex(Integer lastIndex) {
    this.lastIndex = lastIndex;
}

public Integer getCurIndex() {
    return this.curIndex;
}

public void setCurIndex(Integer curIndex) {
    this.curIndex = curIndex;
}
}
