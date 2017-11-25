package com.example.ivor_hu.meizhi.db.data;

import android.arch.lifecycle.LiveData;

import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.net.AbstractLiveDataAsyncNetRequest;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;

import java.util.List;

import retrofit2.Call;

/**
 * Created by ivor on 2017/11/24.
 */

public class StuffRepository {
    private static StuffRepository sInstance;

    private StuffRepository() {
    }

    public static StuffRepository getInstance() {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (StuffRepository.class) {
            if (sInstance == null) {
                sInstance = new StuffRepository();
            }
        }
        return sInstance;
    }

    public LiveData<Result<List<Stuff>>> fetchStuffs(final String type, final int page) {
        return new AbstractLiveDataAsyncNetRequest<Result<List<Stuff>>>() {
            @Override
            protected Call<Result<List<Stuff>>> createCall(GankApi gankApi) {
                return gankApi.fetchStuffs(type, GankApi.DEFAULT_BATCH_NUM, page);
            }
        }.asLiveData();
    }
}
