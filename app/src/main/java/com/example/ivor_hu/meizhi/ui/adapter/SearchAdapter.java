package com.example.ivor_hu.meizhi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.db.entity.SearchEntity;
import com.example.ivor_hu.meizhi.utils.CommonUtil;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivor on 16-6-17.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.Viewholder> {
    private static final String TAG = "SearchAdapter";
    private Context mContext;
    private List<SearchEntity> mSearchBeens;
    private OnItemClickListener mOnItemClickListener;

    public SearchAdapter(Context context) {
        this.mContext = context;
        this.mSearchBeens = new ArrayList<>();
        setHasStableIds(true);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final SearchEntity searchEntity = mSearchBeens.get(position);
        holder.author.setText(searchEntity.getWho());
        holder.title.setText(searchEntity.getDesc());
        try {
            holder.date.setText(DateUtil.formatSearchDate(searchEntity.getPublishedAt()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (null != mOnItemClickListener) {
            holder.stuff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });

            holder.stuff.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }

        if (CommonUtil.isWifiConnected(mContext)
                && !TextUtils.isEmpty(searchEntity.getReadability())) {
            holder.webView.setVisibility(View.VISIBLE);
            holder.webView.setTag(position);
            holder.webView.getSettings().setUseWideViewPort(true);
            holder.webView.getSettings().setLoadWithOverviewMode(true);
            holder.webView.getSettings().setDefaultFontSize(48);
            holder.webView.loadData(searchEntity.getReadability(), "text/html; charset=UTF-8", "utf8");
            holder.webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                            mOnItemClickListener.onItemClick(view, position);
                            return true;
                        default:
                            return true;
                    }
                }
            });
        } else {
            holder.webView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mSearchBeens.size();
    }

    @Override
    public long getItemId(int position) {
        return mSearchBeens.get(position).getUrl().hashCode();
    }

    public SearchEntity getSearchEntityAt(int pos) {
        return mSearchBeens.get(pos);
    }

    public void clearData() {
        mSearchBeens.clear();
        notifyDataSetChanged();
    }

    private <T extends View> T $(View view, int resId) {
        return (T) view.findViewById(resId);
    }

    public void addSearch(List<SearchEntity> results) {
        if (results == null) {
            return;
        }

        mSearchBeens.addAll(results);
    }

    public interface OnItemClickListener {

        void onItemClick(View view, int pos);

        void onItemLongClick(View view, int pos);

    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, author, date;
        RelativeLayout stuff;
        WebView webView;

        public Viewholder(final View itemView) {
            super(itemView);
            title = $(itemView, R.id.stuff_title);
            author = $(itemView, R.id.stuff_author);
            date = $(itemView, R.id.stuff_date);
            stuff = $(itemView, R.id.stuff);
            webView = $(itemView, R.id.readability_wv);
        }
    }
}
