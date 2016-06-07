package com.example.ivor_hu.meizhi.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.services.StuffFetchService;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.Constants;

/**
 * Created by Ivor on 2016/3/3.
 */
public class StuffFragment extends BaseFragment {
    public static final String SERVICE_TYPE = "service_type";
    private static final String TAG = "StuffFragment";
    private static final String TYPE = "type";

    private UpdateResultReceiver updateResultReceiver;

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
        if (!mIsCollections)
            mLocalBroadcastManager.registerReceiver(updateResultReceiver, new IntentFilter(StuffFetchService.ACTION_UPDATE_RESULT));
        else
            updateData();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (mIsCollections)
            return;

        mLocalBroadcastManager.unregisterReceiver(updateResultReceiver);
    }

    @Override
    protected void initData() {
        super.initData();
        mType = getArguments().getString(TYPE);
        mIsCollections = Constants.TYPE.COLLECTIONS.getApiName().equals(mType);
        if (!mIsCollections)
            updateResultReceiver = new UpdateResultReceiver();
    }

    @Override
    protected void loadingMore() {
        if (mIsLoadingMore)
            return;

        Intent intent = new Intent(getActivity(), StuffFetchService.class);
        intent.setAction(StuffFetchService.ACTION_FETCH_MORE).putExtra(SERVICE_TYPE, mType);
        getActivity().startService(intent);

        mIsLoadingMore = true;
        setRefreshLayout(true);
    }

    @Override
    protected void fetchLatest() {
        if (mIsRefreshing)
            return;

        if (mIsCollections) {
            setRefreshLayout(false);
            updateData();
            return;
        }

        Intent intent = new Intent(getActivity(), StuffFetchService.class);
        intent.setAction(StuffFetchService.ACTION_FETCH_REFRESH).putExtra(SERVICE_TYPE, mType);
        getActivity().startService(intent);

        mIsRefreshing = true;
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
        final StuffAdapter adapter = new StuffAdapter(getActivity(), mRealm, mType);
        adapter.setOnItemClickListener(new StuffAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (mIsLoadingMore || mIsRefreshing)
                    return;

                CommonUtil.openUrl(getActivity(), adapter.getStuffAt(pos).getUrl());
            }

            @Override
            public void onItemLongClick(final View view, final int pos) {
                if (mIsLoadingMore || mIsRefreshing)
                    return;

                getActivity().startActionMode(new ShareListener(getActivity(), adapter.getStuffAt(pos), view));
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

    public class ShareListener implements AbsListView.MultiChoiceModeListener {
        private final Context context;
        private final Stuff stuff;
        private final View view;

        public ShareListener(Context context, Stuff stuff, View view) {
            this.context = context;
            this.stuff = stuff;
            this.view = view;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            view.setActivated(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.context_menu_share:
                    String textShared = stuff.getDesc() + "    " + stuff.getUrl() + " -- " + context.getString(R.string.share_msg);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_msg));
                    intent.putExtra(Intent.EXTRA_TEXT, textShared);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            view.setActivated(false);
        }
    }

    private class UpdateResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int fetched = intent.getIntExtra(StuffFetchService.EXTRA_FETCHED, 0);
            String trigger = intent.getStringExtra(StuffFetchService.EXTRA_TRIGGER);
            String type = intent.getStringExtra(StuffFetchService.EXTRA_TYPE);

            if (!type.equals(mType)) {
                return;
            }

            Log.d(TAG, "fetched " + fetched + ", triggered by " + trigger);
            if (fetched == 0 && trigger.equals(StuffFetchService.ACTION_FETCH_MORE)) {
                CommonUtil.makeSnackBar(mRefreshLayout, getString(R.string.fragment_no_more), Snackbar.LENGTH_SHORT);
                mIsNoMore = true;
            }

            setRefreshLayout(false);
            if (mIsRefreshing) {
                mIsRefreshing = false;
                CommonUtil.makeSnackBar(mRefreshLayout, getString(R.string.fragment_refreshed), Snackbar.LENGTH_SHORT);
                mRecyclerView.smoothScrollToPosition(0);
            }
            if (mIsLoadingMore)
                mIsLoadingMore = false;

            if (null == mAdapter || fetched == 0)
                return;
            ((StuffAdapter) mAdapter).updateInsertedData(fetched, trigger.equals(StuffFetchService.ACTION_FETCH_MORE));
        }
    }
}
