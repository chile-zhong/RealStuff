package com.example.ivor_hu.meizhi.db.data;

import android.arch.lifecycle.LiveData;

import com.example.ivor_hu.meizhi.db.SearchBean;
import com.example.ivor_hu.meizhi.net.AbstractLiveDataAsyncNetRequest;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;

import java.util.List;

import retrofit2.Call;

/**
 * Created by ivor on 2017/11/25.
 */

public class SearchReposity {
    private static SearchReposity sInstance;

    private SearchReposity() {
    }

    public static SearchReposity getInstance() {
        if (sInstance != null) {
            return sInstance;
        }

        synchronized (SearchReposity.class) {
            if (sInstance == null) {
                sInstance = new SearchReposity();
            }
        }
        return sInstance;
    }

    public LiveData<Result<List<SearchBean>>> search(final String query, final String category, final int count, final int page) {
        return new AbstractLiveDataAsyncNetRequest<Result<List<SearchBean>>>() {
            @Override
            protected Call<Result<List<SearchBean>>> createCall(GankApi gankApi) {
                return gankApi.search(query, category, count, page);
            }
        }.asLiveData();
    }
}
