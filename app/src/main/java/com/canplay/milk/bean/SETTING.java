package com.canplay.milk.bean;

import com.mykar.framework.activeandroid.Model;
import com.mykar.framework.activeandroid.annotation.Column;
import com.mykar.framework.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by linquandong on 17/1/9.
 */
@Table(name = "SETTING")
public class SETTING extends Model implements Serializable,Cloneable {

    public SETTING(){
        super();
    }

    @Column(name = "postion", unique = true,onUniqueConflict = Column.ConflictAction.REPLACE)
    public int postion;

    @Column(name = "fieldName")
    public String fieldName;

    @Column(name = "fiedDesc")
    public String fiedDesc;

    @Column(name = "scale")
    public float scale;

    @Column(name = "selected")
    public int selected;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
