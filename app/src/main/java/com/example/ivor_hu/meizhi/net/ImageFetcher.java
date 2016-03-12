package com.example.ivor_hu.meizhi.net;

import android.graphics.Point;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ivor on 2016/2/9.
 */
public interface ImageFetcher {
    void prefetchImage(String url, Point measured)
            throws IOException, InterruptedException, ExecutionException;
}
