package com.example.ivor_hu.meizhi.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivor on 16-6-21.
 */
public abstract class BaseStuffAdapter extends RecyclerView.Adapter<BaseStuffAdapter.Viewholder> {
    private static final String TAG = "BaseStuffAdapter";
    protected final Context mContext;
    protected final String mType;
    protected List<Stuff> mStuffs;
    private OnItemClickListener mOnItemClickListener;

    public BaseStuffAdapter(Context context, String type) {
        this.mContext = context;
        this.mType = type;
        mStuffs = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Viewholder(LayoutInflater.from(mContext).inflate(R.layout.stuff_item, parent, false));
    }

    @Override
    public void onBindViewHolder(Viewholder holder, final int position) {
        final Stuff stuff = mStuffs.get(position);
        holder.source.setText(stuff.getWho());
        holder.title.setText(stuff.getDesc());
        holder.date.setText(DateUtil.format(stuff.getPublishedAt()));
        holder.stuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.stuff.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    return mOnItemClickListener.onItemLongClick(v, position);
                }

                return false;
            }
        });

        bindColBtn(holder.likeBtn, position);
    }

    @Override
    public int getItemCount() {
        return mStuffs.size();
    }

    @Override
    public long getItemId(int position) {
        return mStuffs.get(position).getId().hashCode();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    protected abstract void bindColBtn(ImageButton likeBtn, int position);

    public Stuff getStuffAt(int pos) {
        return mStuffs.get(pos);
    }

    public interface OnItemClickListener {
        boolean onItemLongClick(View v, int position);

        void onItemClick(View v, int position);
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, source, date;
        LinearLayout stuff;
        ImageButton likeBtn;

        public Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.stuff_title);
            source = itemView.findViewById(R.id.stuff_author);
            date = itemView.findViewById(R.id.stuff_date);
            stuff = itemView.findViewById(R.id.stuff);
            likeBtn = itemView.findViewById(R.id.like_btn);
        }
    }
}
