package com.javawxid.bean;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.List;

/**
 * @param
 * @return
 */
public class BaseCatalog1 implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column
    private String name;

    private List<BaseCatalog2> baseCatalog2List;

    public List<BaseCatalog2> getBaseCatalog2List() {
        return baseCatalog2List;
    }

    public void setBaseCatalog2List(List<BaseCatalog2> baseCatalog2List) {
        this.baseCatalog2List = baseCatalog2List;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

