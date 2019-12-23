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

    @NotNull
    private String text; //输入内容

    private Date date;//什么时候添加

    private String comment; //添加时间

@Generated(hash = 2054018170)
public Note(Long id, @NotNull String text, Date date, String comment) {
    this.id = id;
    this.text = text;
    this.date = date;
    this.comment = comment;
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
}
