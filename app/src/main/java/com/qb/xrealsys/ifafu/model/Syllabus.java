package com.qb.xrealsys.ifafu.model;

import java.util.ArrayList;
import java.util.List;

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
