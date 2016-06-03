package com.example.ivor_hu.meizhi.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.utils.CommonUtil;

import io.realm.Realm;

/**
 * Created by ivor on 16-6-3.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mRefreshLayout;
    protected LocalBroadcastManager mLocalBroadcastManager;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.Adapter mAdapter;
    protected boolean mIsLoadingMore;
    protected boolean mIsRefreshing;
    protected Realm mRealm;
    protected String mType;
    protected boolean mIsCollections;
    protected boolean mIsNoMore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void initData() {
        mRealm = Realm.getDefaultInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), container, false);

        mRefreshLayout = $(view, getRefreshLayoutId());
        mRecyclerView = $(view, getRecyclerViewId());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = getLayoutManager();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = initAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!mIsLoadingMore && dy > 0) {
                    int lastVisiblePos = getLastVisiblePos();
                    if (!mIsCollections && !mIsNoMore && lastVisiblePos + 1 == mAdapter.getItemCount()) {
                        loadingMore();
                        CommonUtil.makeSnackBar(mRefreshLayout, getResources().getString(R.string.fragment_load_more), Snackbar.LENGTH_SHORT);
                    }
                }
            }
        });

        if (!mIsCollections)
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchLatest();
            }
        };
        mRefreshLayout.setOnRefreshListener(listener);
        if (savedInstanceState == null)
            listener.onRefresh();

        // another way to call onRefresh
//        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mRecyclerView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                mRefreshLayout.setRefreshing(true);
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.removeAllChangeListeners();
        mRealm.close();
    }

    public void setRefreshLayout(final boolean state) {
        if (mRefreshLayout == null)
            return;

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(state);
            }
        });
    }

    public void smoothScrollToTop() {
        if (mLayoutManager != null)
            mLayoutManager.smoothScrollToPosition(mRecyclerView, null, 0);
    }

    public boolean isFetching() {
        return mIsLoadingMore || mIsRefreshing;
    }

    protected <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    protected abstract void loadingMore();

    protected abstract void fetchLatest();

    protected abstract int getLastVisiblePos();

    protected abstract int getLayoutResId();

    protected abstract int getRefreshLayoutId();

    protected abstract RecyclerView.Adapter initAdapter();

    protected abstract RecyclerView.LayoutManager getLayoutManager();

    protected abstract int getRecyclerViewId();
}
