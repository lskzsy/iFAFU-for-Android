package com.qb.xrealsys.ifafu.Base.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky on 14/02/2018.
 */

public class Search extends Model {

    private List<String> searchYearOptions;

    private int          selectedYearOption;

    private List<String> searchTermOptions;

    private int          selectedTermOption;

    public Search() {
        searchYearOptions = new ArrayList<>();
        searchTermOptions = new ArrayList<>();
        selectedYearOption = 0;
        selectedTermOption = 0;
    }

    public List<String> getSearchTermOptions() {
        return searchTermOptions;
    }

    public void setSearchTermOptions(List<String> searchTermOptions) {
        this.searchTermOptions = searchTermOptions;
    }

    public int getSelectedTermOption() {
        return selectedTermOption;
    }

    public void setSelectedTermOption(int selectedTermOption) {
        this.selectedTermOption = selectedTermOption;
    }

    public int getSelectedYearOption() {
        return selectedYearOption;
    }

    public List<String> getSearchYearOptions() {
        return searchYearOptions;
    }

    public void setSearchYearOptions(List<String> searchYearOptions) {
        this.searchYearOptions = searchYearOptions;
    }

    public void setSelectedYearOption(int selectedYearOption) {
        this.selectedYearOption = selectedYearOption;
    }
}
