package com.example.ivor_hu.meizhi.db.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.net.AbstractLiveDataAsyncNetRequest;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;
import com.example.ivor_hu.meizhi.net.ImageFetcher;
import com.example.ivor_hu.meizhi.utils.AppExecutors;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by ivor on 2017/11/24.
 */

public class GirlRepository implements ImageFetcher {
    private static final String TAG = "GirlRepository";
    private static GirlRepository sInstance;
    private final AppExecutors mExecutors;
    private final Context mContext;

    private GirlRepository(Context context) {
        mExecutors = AppExecutors.getInstance();
        mContext = context.getApplicationContext();
    }

    public static GirlRepository getInstance(Context context) {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (GirlRepository.class) {
            if (sInstance == null) {
                sInstance = new GirlRepository(context);
            }
        }
        return sInstance;
    }

    public LiveData<Result<List<Image>>> fetchGirls(final int page) {
        return new AbstractLiveDataAsyncNetRequest<Result<List<Image>>>() {
            @Override
            protected void onHandleResponse(Call<Result<List<Image>>> call, Response<Result<List<Image>>> response) {
                if (response.body() != null && !response.body().error) {
                    prefetchImages(response.body(), mLiveData);
                } else {
                    mLiveData.setValue(null);
                }
            }

            @Override
            protected Call<Result<List<Image>>> createCall(GankApi gankApi) {
                return gankApi.fetchGirls(GankApi.DEFAULT_BATCH_NUM, page);
            }
        }.asLiveData();
    }

    private void prefetchImages(final Result<List<Image>> result, final MutableLiveData<Result<List<Image>>> liveData) {

        mExecutors.normal().execute(new Runnable() {
            @Override
            public void run() {
                for (Image image : result.results) {
                    try {
                        Image.persist(image, GirlRepository.this);
                    } catch (IOException | InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        Log.e(TAG, "prefetchImages: failed " + image.getUrl());
                    }
                }
                liveData.postValue(result);
            }
        });
    }

    @Override
    public void prefetchImage(String url, Point measured) throws IOException, InterruptedException, ExecutionException {
        Bitmap bitmap = Glide.with(mContext)
                .load(url).asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();

        measured.x = bitmap.getWidth();
        measured.y = bitmap.getHeight();
    }
}
