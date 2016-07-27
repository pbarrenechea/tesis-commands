package com.recommender;

import org.apache.mahout.cf.taste.common.TasteException;

/**
 * Created by Usuario on 26/07/2016.
 */
public interface CustomRecommender {

    void evaluate() throws TasteException;
}
