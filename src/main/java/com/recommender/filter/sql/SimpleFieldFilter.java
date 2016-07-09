package com.recommender.filter.sql;
import com.recommender.filter.QueryFilter;

/**
 * Created by Pablo Barrenechea on 10/10/2015.
 */
public class SimpleFieldFilter implements QueryFilter {

    private String fieldName;
    private String value;

    public SimpleFieldFilter(String fieldName, String filterValue){
        this.fieldName = fieldName;
        this.value = filterValue;
    }

    public String getQueryFilter() {
        return " " + this.fieldName + " = " + this.value + " ";
    }
}
