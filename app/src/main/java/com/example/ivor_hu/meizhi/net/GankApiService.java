package com.example.ivor_hu.meizhi.net;

import com.example.ivor_hu.meizhi.utils.DateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ivor on 2016/4/4.
 */
public class GankApiService {
    private static final Gson GSON = new GsonBuilder()
            .setDateFormat(DateUtil.DATE_FORMAT_WHOLE)
            .create();
    private static final Retrofit GIRLS_RETROFIT = new Retrofit.Builder()
            .baseUrl(GankApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GSON))
            .build();
    private static volatile GankApi sGankApi;

    private GankApiService() {
    }

    public static GankApi getInstance() {
        if (sGankApi == null) {
            synchronized (GankApiService.class) {
                if (sGankApi == null) {
                    sGankApi = GIRLS_RETROFIT.create(GankApi.class);
                }
            }
        }

        return sGankApi;
    }

}
