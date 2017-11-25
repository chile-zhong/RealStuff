package com.example.ivor_hu.meizhi.net;

import android.arch.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ivor on 2017/11/24.
 */

public abstract class AbstractLiveDataAsyncNetRequest<ResponseType> {
    private static final String TAG = "AbstractLiveDataAsyncNe";
    protected final MutableLiveData<ResponseType> mLiveData;

    public AbstractLiveDataAsyncNetRequest() {
        mLiveData = new MutableLiveData<>();
        GankApi gankApi = GankApiService.getInstance();
        Call<ResponseType> call = createCall(gankApi);
        call.enqueue(new Callback<ResponseType>() {
            @Override
            public void onResponse(Call<ResponseType> call, Response<ResponseType> response) {
                onHandleResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseType> call, Throwable t) {
                onHandleFailure(call, t);
            }
        });
    }

    protected void onHandleFailure(Call<ResponseType> call, Throwable t) {
        mLiveData.setValue(null);
    }

    protected void onHandleResponse(Call<ResponseType> call, Response<ResponseType> response) {
        mLiveData.setValue(response.body());
    }

    public MutableLiveData<ResponseType> asLiveData() {
        return mLiveData;
    }

    protected abstract Call<ResponseType> createCall(GankApi gankApi);
}
