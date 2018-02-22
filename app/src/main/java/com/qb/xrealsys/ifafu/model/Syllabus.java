package com.qb.xrealsys.ifafu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky on 11/02/2018.
 */

public class Syllabus extends Search {

    private List<Course> data;

    public Syllabus() {
        data = new ArrayList<>();
    }

    public void append(Course course) {
        data.add(course);
    }

    public List<Course> getData() {
        return data;
    }
}
