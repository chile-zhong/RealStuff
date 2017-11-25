package com.example.ivor_hu.meizhi.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.net.GankApi;
import com.example.ivor_hu.meizhi.ui.adapter.StuffAdapter;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.viewmodel.StuffViewModel;

import java.util.List;

/**
 * Created by Ivor on 2016/3/3.
 */
public class StuffFragment extends BaseStuffFragment {
    public static final String SERVICE_TYPE = "service_type";
    private static final String TAG = "StuffFragment";
    private static final String TYPE = "type";

    private StuffViewModel mStuffViewModel;

    public static StuffFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);

        StuffFragment fragment = new StuffFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + mType);
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getArguments().getString(TYPE);
        mStuffViewModel = ViewModelProviders.of(this).get(StuffViewModel.class);
        mStuffViewModel.getStuffs().observe(this, new Observer<GankApi.Result<List<Stuff>>>() {
            @Override
            public void onChanged(@Nullable GankApi.Result<List<Stuff>> result) {
                setRefreshLayout(false);
                setFetchingFlagsFalse();

                if (result == null) {
                    return;
                }

                StuffAdapter adapter = (StuffAdapter) mAdapter;
                if (mPage == 1) {
                    adapter.clearStuff();
                }
                adapter.addStuffs(result.results);
                mAdapter.notifyItemRangeInserted(adapter.getItemCount(), result.results.size());
                mPage++;
            }
        });
    }

    @Override
    protected void loadingMore() {
        if (mIsFetching) {
            return;
        }

        mStuffViewModel.fetchStuffs(mType, mPage);

        mIsFetching = true;
        setRefreshLayout(true);
    }

    @Override
    protected void refresh() {
        if (isFetching()) {
            return;
        }

        mPage = 1;
        mStuffViewModel.fetchStuffs(mType, mPage);

        setRefreshLayout(true);
    }

    @Override
    protected RecyclerView.Adapter initAdapter() {
        final StuffAdapter adapter = new StuffAdapter(getActivity(), mType);
        adapter.setOnItemClickListener(new StuffAdapter.OnItemClickListener() {
            @Override
            public boolean onItemLongClick(View v, int position) {
                if (isFetching()) {
                    return true;
                }

                getActivity().startActionMode(new ShareListener(getActivity(), adapter.getStuffAt(position), v));
                return true;
            }

            @Override
            public void onItemClick(View view, int pos) {
                if (isFetching()) {
                    return;
                }

                CommonUtil.openUrl(getActivity(), adapter.getStuffAt(pos).getUrl());
            }
        });
        return adapter;
    }

}
