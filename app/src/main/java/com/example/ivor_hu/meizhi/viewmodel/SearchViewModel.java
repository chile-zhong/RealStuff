package com.example.ivor_hu.meizhi.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.example.ivor_hu.meizhi.db.data.SearchReposity;
import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.net.GankApi.Result;

import java.util.List;

/**
 * Created by ivor on 2017/11/25.
 */

public class SearchViewModel extends ViewModel {
    private final SearchReposity mReposity;

    private final MutableLiveData<SearchWrapper> mSearchWrapper;
    private final LiveData<Result<List<SearchEntity>>> mResult;

    public SearchViewModel() {
        mReposity = SearchReposity.getInstance();

        mSearchWrapper = new MutableLiveData<>();
        mResult = Transformations.switchMap(mSearchWrapper, new Function<SearchWrapper, LiveData<Result<List<SearchEntity>>>>() {
            @Override
            public LiveData<Result<List<SearchEntity>>> apply(SearchWrapper wrapper) {
                return mReposity.search(wrapper.query, wrapper.category, wrapper.count, wrapper.page);
            }
        });
    }

    public void search(String query, String category, int page) {
        mSearchWrapper.setValue(new SearchWrapper(query, category, GankApi.DEFAULT_BATCH_NUM, page));
    }

    public LiveData<Result<List<SearchEntity>>> getSearchResult() {
        return mResult;
    }

    private static class SearchWrapper {
        final String query;
        final String category;
        final int count;
        final int page;

        public SearchWrapper(String query, String category, int count, int page) {
            this.query = query;
            this.category = category;
            this.count = count;
            this.page = page;
        }
    }

}
