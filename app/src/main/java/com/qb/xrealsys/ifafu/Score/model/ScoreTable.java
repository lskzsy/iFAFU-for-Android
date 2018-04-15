package com.qb.xrealsys.ifafu.Score.model;

import com.qb.xrealsys.ifafu.Base.model.Table;

import java.util.List;

/**
 * Created by sky on 14/02/2018.
 */

public class ScoreTable extends Table<Score> {

    private List<Score> defaultScore;

    private int         defaultSelectedYear;

    private int         defaultSelectedTerm;

    public void updateDefaultData() {
        defaultScore        = getData();
        defaultSelectedYear = getSelectedYearOption();
        defaultSelectedTerm = getSelectedTermOption();
    }

    public List<Score> getDefaultScore() {
        return defaultScore;
    }

    public int getDefaultSelectedYear() {
        return defaultSelectedYear;
    }

    public int getDefaultSelectedTerm() {
        return defaultSelectedTerm;
    }
}
