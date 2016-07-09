package com.recommender.filter.sql;

import com.recommender.filter.QueryFilter;

/**
 * Created by Pablo Barrenechea on 10/10/2015.
 */
public class AndFilter implements QueryFilter {

    protected QueryFilter criteria;
    protected QueryFilter otherCriteria;

    public AndFilter(QueryFilter c1, QueryFilter c2 ){
        this.criteria =  c1;
        this.otherCriteria = c2;
    }

    public String getQueryFilter() {
        return "(" + this.criteria.getQueryFilter() + " AND " + this.otherCriteria.getQueryFilter() + " )";
    }
}
