package com.example.ivor_hu.meizhi.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.db.data.StuffRepository;
import com.example.ivor_hu.meizhi.net.GankApi.Result;

import java.util.List;

/**
 * Created by ivor on 2017/11/24.
 */

public class StuffViewModel extends ViewModel {

    private final StuffRepository mRepository;

    private final MutableLiveData<FetchWrapper> mFetchWrapper;

    private final LiveData<Result<List<Stuff>>> mStuffs;

    public StuffViewModel() {
        this.mRepository = StuffRepository.getInstance();

        mFetchWrapper = new MutableLiveData<>();
        mStuffs = Transformations.switchMap(mFetchWrapper, new Function<FetchWrapper, LiveData<Result<List<Stuff>>>>() {
            @Override
            public LiveData<Result<List<Stuff>>> apply(FetchWrapper wrapper) {
                return mRepository.fetchStuffs(wrapper.type, wrapper.page);
            }
        });
    }

    public void fetchStuffs(String type, int page) {
        mFetchWrapper.setValue(new FetchWrapper(type, page));
    }

    public LiveData<Result<List<Stuff>>> getStuffs() {
        return mStuffs;
    }

    private static class FetchWrapper {
        final String type;
        final int page;

        public FetchWrapper(String type, int page) {
            this.type = type;
            this.page = page;
        }
    }

}
