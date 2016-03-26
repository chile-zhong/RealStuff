package com.example.ivor_hu.meizhi.db;

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
    private String id;
    private String title, url, author, type;
    private Date publishedAt, lastChanged;
    private boolean isLiked;

    public Stuff() {
    }

    public Stuff(String id, String type, String title, String url, String author, Date publishedAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.url = url;
        this.author = author;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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
