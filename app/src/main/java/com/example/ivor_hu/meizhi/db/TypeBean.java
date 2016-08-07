package com.example.ivor_hu.meizhi.db;

/**
 * Created by ivor on 16-8-6.
 */
public class TypeBean {
    private int strId;
    private int apiId;

    public TypeBean(int strId, int apiId) {
        this.strId = strId;
        this.apiId = apiId;
    }

    public int getStrId() {
        return strId;
    }

    public void setStrId(int strId) {
        this.strId = strId;
    }

    public int getApiId() {
        return apiId;
    }

    public void setApiId(int apiId) {
        this.apiId = apiId;
    }
}
