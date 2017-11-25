package com.example.ivor_hu.meizhi.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.example.ivor_hu.meizhi.utils.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Ivor on 2016/2/28.
 */
@Entity(tableName = "collection")
public class Stuff {
    private static final String TAG = "Stuff";
    @NonNull
    @PrimaryKey
    @SerializedName("_id")
    private String id;
    private String desc, url, who, type;
    private Date publishedAt, lastChanged;
    private boolean isLiked, isDeleted;

    public Stuff() {
    }

    @Ignore
    public Stuff(String id, String type, String desc, String url, String who, Date publishedAt) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.url = url;
        this.who = who;
        this.publishedAt = publishedAt;
        this.lastChanged = publishedAt;
        this.isLiked = false;
        this.isDeleted = false;
    }

    public static Stuff fromSearch(SearchEntity bean) throws ParseException {
        return new Stuff(
                bean.getUrl(),
                bean.getType(),
                bean.getDesc(),
                bean.getUrl(),
                bean.getWho(),
                DateUtil.parse(bean.getPublishedAt())
        );
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
