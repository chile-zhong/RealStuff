package com.example.ivor_hu.meizhi.db;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Ivor on 2016/2/28.
 */
public class Stuff extends RealmObject {
    @PrimaryKey
    @SerializedName("_id")
    private String id;
    private String desc, url, who, type;
    private Date publishedAt, lastChanged;
    private boolean isLiked;

    public Stuff() {
    }

    public Stuff(String id, String type, String desc, String url, String who, Date publishedAt) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.url = url;
        this.who = who;
        this.publishedAt = publishedAt;
        this.lastChanged = publishedAt;
        this.isLiked = false;

    }

    public static RealmResults<Stuff> all(Realm realm, String type) {
        return realm.where(Stuff.class)
                .equalTo("type", type)
                .findAllSorted("publishedAt", Sort.DESCENDING);
    }

    public static RealmResults<Stuff> collections(Realm realm) {
        return realm.where(Stuff.class)
                .equalTo("isLiked", true)
                .findAllSorted("lastChanged", Sort.DESCENDING);
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

}
