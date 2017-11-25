package com.example.ivor_hu.meizhi.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.example.ivor_hu.meizhi.db.Image;
import com.example.ivor_hu.meizhi.db.data.GirlRepository;
import com.example.ivor_hu.meizhi.net.GankApi;

import java.util.List;

/**
 * Created by ivor on 2017/11/24.
 */

public class GirlViewModel extends AndroidViewModel {
    private final MutableLiveData<Integer> mPage;
    private final LiveData<GankApi.Result<List<Image>>> mGirls;
    private GirlRepository mRepository;

    public GirlViewModel(@NonNull Application application) {
        super(application);
        mRepository = GirlRepository.getInstance(application);

        mPage = new MutableLiveData<>();
        mGirls = Transformations.switchMap(mPage, new Function<Integer, LiveData<GankApi.Result<List<Image>>>>() {
            @Override
            public LiveData<GankApi.Result<List<Image>>> apply(Integer page) {
                return mRepository.fetchGirls(page);
            }
        });
    }


    public void fetchGirls(int page) {
        mPage.setValue(page);
    }

    public LiveData<GankApi.Result<List<Image>>> getGirls() {
        return mGirls;
    }

}
