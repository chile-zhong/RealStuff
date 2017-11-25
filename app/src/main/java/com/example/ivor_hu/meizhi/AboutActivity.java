package com.example.ivor_hu.meizhi;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivor on 2016/2/25.
 */
public class AboutActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private ArrayMap<String, String> mLibsList;
    private List<String> mFeasList;
    private List<String> mCompsList;
    private List<String> mHeaderList;
    private AboutAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mToolbar = $(R.id.about_toolbar);
        mRecyclerView = $(R.id.about_recyclerview);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        if (NavUtils.getParentActivityName(this) != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView verTv = $(R.id.version_name);
        try {
            verTv.setText(String.format(getString(R.string.version_name), CommonUtil.getVersionName(this)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        initData();
        mAdapter = new AboutAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        mHeaderList = new ArrayList<>();
        mHeaderList.add("Android Architecture Components");
        mHeaderList.add(getString(R.string.about_libs_used));
        mHeaderList.add(getString(R.string.about_feas_used));
        mCompsList = new ArrayList<>();
        mCompsList.add("Lifecycle");
        mCompsList.add("LiveData");
        mCompsList.add("Room");
        mLibsList = new ArrayMap<>();
        mLibsList.put("bumptech / Glide", "https://github.com/bumptech/glide");
        mLibsList.put("Mike Ortiz / TouchImageView", "https://github.com/MikeOrtiz/TouchImageView");
        mLibsList.put("Square / Retrofit", "https://github.com/square/retrofit");
        mFeasList = new ArrayList<>();
        mFeasList.add("CardView");
        mFeasList.add("CollapsingToolbarLayout");
        mFeasList.add("DrawerLayout");
        mFeasList.add("RecyclerView");
        mFeasList.add("Shared Element Transition");
        mFeasList.add("SnackBar");
        mFeasList.add("TranslucentBar");
    }

    private <T extends View> T $(int resId) {
        return (T) findViewById(resId);
    }

    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    class AboutAdapter extends RecyclerView.Adapter<ViewHolder> {
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;
        private final int firstHeaderPosition = 0;
        private final int secondHeaderPosition = 1 + mCompsList.size();
        private final int thirdHeaderPosition = 2 + mCompsList.size() + mLibsList.size();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return viewType == TYPE_ITEM
                    ? new ItemViewHolder(getLayoutInflater().inflate(R.layout.about_item, parent, false))
                    : new HeaderViewHolder(getLayoutInflater().inflate(R.layout.about_header, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_ITEM) {
                if (position < secondHeaderPosition) {
                    ((ItemViewHolder) holder).textView.setText(mCompsList.get(position - 1));
                    ((ItemViewHolder) holder).textView.setClickable(false);
                } else if (position < thirdHeaderPosition) {
                    ((ItemViewHolder) holder).textView.setText(mLibsList.keyAt(position - secondHeaderPosition - 1));
                } else {
                    ((ItemViewHolder) holder).textView.setText(mFeasList.get(position - thirdHeaderPosition - 1));
                    ((ItemViewHolder) holder).textView.setClickable(false);
                }
            } else {
                String text;
                if (position == firstHeaderPosition) {
                    text = mHeaderList.get(0);
                } else if (position == thirdHeaderPosition) {
                    text = mHeaderList.get(mHeaderList.size() - 1);
                } else {
                    text = mHeaderList.get(1);
                }
                ((HeaderViewHolder) holder).textView.setText(text);
            }

        }

        @Override
        public int getItemCount() {
            return mLibsList.size() + mFeasList.size() + mHeaderList.size() + mCompsList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position == firstHeaderPosition
                    || position == secondHeaderPosition
                    || position == thirdHeaderPosition
                    ? TYPE_HEADER
                    : TYPE_ITEM;
        }

        public int getFirstHeaderPosition() {
            return firstHeaderPosition;
        }

        public int getSecondHeaderPosition() {
            return secondHeaderPosition;
        }

        public int getThirdHeaderPosition() {
            return thirdHeaderPosition;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class HeaderViewHolder extends ViewHolder {
        TextView textView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView;
        }
    }

    private class ItemViewHolder extends ViewHolder {
        TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = $(itemView, R.id.item_text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getAdapterPosition();
                    if (pos < mAdapter.getThirdHeaderPosition() && pos > mAdapter.getSecondHeaderPosition()) {
                        CommonUtil.openUrl(AboutActivity.this, mLibsList.valueAt(pos - mAdapter.getSecondHeaderPosition() - 1));
                    }
                }
            });
        }
    }
}
