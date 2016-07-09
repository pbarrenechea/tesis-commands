package com.recommender.filter.sql;

import com.recommender.filter.QueryFilter;

/**
 * Created by Pablo Barrenechea on 10/10/2015.
 */
public class OrFilter implements QueryFilter {

    protected QueryFilter criteria;
    protected QueryFilter otherCriteria;

    public OrFilter(QueryFilter c1, QueryFilter c2){
        this.criteria = c1;
        this.otherCriteria = c2;
    }

    public String getQueryFilter() {
        return "(" + this.criteria.getQueryFilter() + " OR " + this.otherCriteria.getQueryFilter() + " )";
    }
}
