package com.example.ivor_hu.meizhi.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ivor_hu.meizhi.ui.adapter.CollectionAdapter;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

/**
 * Created by ivor on 16-6-21.
 */
public class CollectionFragment extends BaseStuffFragment {
    private static final String TAG = "CollectionFragment";
    private static final String TYPE = "col_type";

    public static CollectionFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);

        CollectionFragment fragment = new CollectionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getArguments().getString(TYPE);
    }

    @Override
    protected void loadingMore() {
        return;
    }

    @Override
    protected void refresh() {
        setRefreshLayout(false);
    }

    @Override
    protected RecyclerView.Adapter initAdapter() {
        final CollectionAdapter adapter = new CollectionAdapter(getActivity(), mType);
        adapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
            @Override
            public boolean onItemLongClick(View v, int position) {
                if (isFetching()) {
                    return true;
                }

                getActivity().startActionMode(new StuffFragment.ShareListener(getActivity(), adapter.getStuffAt(position), v));
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
