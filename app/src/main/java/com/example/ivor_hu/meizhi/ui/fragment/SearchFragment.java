package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.db.SearchBean;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.ui.adapter.SearchAdapter;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.viewmodel.SearchViewModel;

import java.util.List;

/**
 * Created by ivor on 16-6-17.
 */
public class SearchFragment extends BaseFragment {
    public static final String KEYWORD = "keyword";
    public static final String CATEGORY = "category";
    private static final String TAG = "SearchFragment";
    private String mKeyword;
    private String mCategory;
    private SearchViewModel mSearchViewModel;

    public static SearchFragment newInstance(String keyword, String category) {
        Bundle args = new Bundle();
        args.putString(KEYWORD, keyword);
        args.putString(CATEGORY, category);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mKeyword = getArguments().getString(KEYWORD);
        mCategory = getArguments().getString(CATEGORY);
        mPage = 1;
        mSearchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        mSearchViewModel.getSearchResult().observe(this, new Observer<GankApi.Result<List<SearchBean>>>() {
            @Override
            public void onChanged(@Nullable GankApi.Result<List<SearchBean>> result) {
                setFetchingFlagsFalse();
                setRefreshLayout(false);
                if (result == null) {
                    return;
                }

                SearchAdapter adapter = (SearchAdapter) mAdapter;
                if (mPage == 1) {
                    adapter.clearData();
                }
                adapter.addSearch(result.results);
                adapter.notifyItemRangeInserted(adapter.getItemCount(), result.results.size());
                mPage++;
            }
        });
    }

    @Override
    protected void loadingMore() {
        if (isFetching()) {
            return;
        }

        mSearchViewModel.search(mKeyword, mCategory, mPage);
        mIsFetching = true;
        setRefreshLayout(true);
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mPage = 1;
        mSearchViewModel.search(mKeyword, mCategory, mPage);
        mIsFetching = true;
        setRefreshLayout(true);
    }

    @Override
    protected int getLastVisiblePos() {
        return ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.stuff_fragment;
    }

    @Override
    protected int getRefreshLayoutId() {
        return R.id.stuff_refresh_layout;
    }

    @Override
    protected RecyclerView.Adapter initAdapter() {
        final SearchAdapter adapter = new SearchAdapter(getActivity());
        adapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (isFetching()) {
                    return;
                }

                CommonUtil.openUrl(getActivity(), adapter.getStuffAt(pos).getUrl());
            }

            @Override
            public void onItemLongClick(final View view, final int pos) {
            }
        });
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.stuff_recyclerview;
    }

    public void search(String keyword, String category) {
        this.mKeyword = keyword;
        this.mCategory = category;
        ((SearchAdapter) mAdapter).clearData();
        refresh();
    }
}
