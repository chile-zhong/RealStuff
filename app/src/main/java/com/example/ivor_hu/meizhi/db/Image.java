package com.example.ivor_hu.meizhi.db;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.ivor_hu.meizhi.net.ImageFetcher;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ivor on 2016/2/9.
 */
public class Image implements Parcelable {
    public static final Parcelable.Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    @SerializedName("_id")
    private String id;
    private String url;
    private int width;
    private int height;
    private Date publishedAt;

    public Image() {
    }

    public Image(String id, String url, Date publishedAt) {
        this.id = id;
        this.url = url;
        this.publishedAt = publishedAt;
    }

    public Image(Parcel parcel) {
        id = parcel.readString();
        url = parcel.readString();
        width = parcel.readInt();
        height = parcel.readInt();
        publishedAt = new Date(parcel.readLong());
    }

    public static Image persist(Image image, ImageFetcher imageFetcher)
            throws IOException, InterruptedException, ExecutionException {
        Point size = new Point();

        imageFetcher.prefetchImage(image.getUrl(), size);

        image.setWidth(size.x);
        image.setHeight(size.y);

        return image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeLong(publishedAt.getTime());
    }
}
