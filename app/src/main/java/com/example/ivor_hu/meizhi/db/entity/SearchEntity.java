package com.example.ivor_hu.meizhi.db.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ivor on 16-6-17.
 */

public class SearchEntity implements Parcelable {
    public static final Parcelable.Creator<SearchEntity> CREATOR = new Creator<SearchEntity>() {
        @Override
        public SearchEntity createFromParcel(Parcel source) {
            return new SearchEntity(source);
        }

        @Override
        public SearchEntity[] newArray(int size) {
            return new SearchEntity[size];
        }
    };
    /**
     * desc : 还在用ListView？
     * publishedAt : 2016-05-12T12:04:43.857000
     * readability : <div></div>
     * type : Android
     * url : http://www.jianshu.com/p/a92955be0a3e
     * who : 陈宇明
     */

    private String desc;
    private String publishedAt;
    private String readability;
    private String type;
    private String url;
    private String who;
    private boolean isLiked;

    public SearchEntity(Parcel source) {
        desc = source.readString();
        publishedAt = source.readString();
        readability = source.readString();
        type = source.readString();
        url = source.readString();
        who = source.readString();
//        isLiked = source.readByte() == 1;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getReadability() {
        return readability;
    }

    public void setReadability(String readability) {
        this.readability = readability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(desc);
        dest.writeString(publishedAt);
        dest.writeString(readability);
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(who);
//        dest.writeByte((byte) (isLiked ? 1 : 0));
    }
}
