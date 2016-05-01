package com.commands.categories;

/**
 * Created by Pablo on 5/1/2016.
 */
public class CategoryItem {

    private Long id;
    private String parent_hash_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getParentHashId() {
        return parent_hash_id;
    }

    public void setParentHashId(String parent_hash_id) {
        this.parent_hash_id = parent_hash_id;
    }
}
