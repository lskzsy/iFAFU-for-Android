package com.qb.xrealsys.ifafu.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky on 14/02/2018.
 */

public class ScoreTable extends Search {

    private List<Score> data;

    public ScoreTable() {
        data = new ArrayList<>();
    }

    public void append(Score score) {
        data.add(score);
    }

    public List<Score> getData() {
        return data;
    }

    public void setData(List<Score> data) {
        this.data = data;
    }
}
