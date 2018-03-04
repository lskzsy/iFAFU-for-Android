package com.qb.xrealsys.ifafu.Syllabus.model;

import com.qb.xrealsys.ifafu.Base.model.Table;

/**
 * Created by sky on 11/02/2018.
 */

public class Syllabus extends Table<Course> {

    private int     campus;

    public int getCampus() {
        return campus;
    }

    public void setCampus(int campus) {
        this.campus = campus;
    }
}
