package com.qb.xrealsys.ifafu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky on 24/02/2018.
 */

public class Table<T> extends Search {

    private List<T> data;

    public Table() { data = new ArrayList<>();}

    public void append(T t) {
        data.add(t);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
