package com.example.ivor_hu.meizhi.ui.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.db.entity.Stuff;
import com.example.ivor_hu.meizhi.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ivor on 16-6-21.
 */
public class StuffAdapter extends RecyclerView.Adapter<StuffAdapter.Viewholder> {
    private static final String TAG = "StuffAdapter";
    protected final Context mContext;
    protected final String mType;
    protected List<Stuff> mStuffs;
    protected OnItemClickListener mOnItemClickListener;

    public StuffAdapter(Context context, String type) {
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

    public Stuff getStuffAt(int pos) {
        return mStuffs.get(pos);
    }

    public void addStuffs(List<Stuff> stuffs) {
        if (stuffs == null) {
            return;
        }

        mStuffs.addAll(stuffs);
        notifyItemRangeInserted(getItemCount(), stuffs.size());
    }

    public void clearStuff() {
        mStuffs.clear();
    }

    public void updateStuffs(List<Stuff> stuffs) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(mStuffs, stuffs), true);
        diffResult.dispatchUpdatesTo(this);
        mStuffs = stuffs;
    }

    public interface OnItemClickListener {
        boolean onItemLongClick(View v, int position);

        void onItemClick(View v, int position);
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        TextView title, source, date;
        RelativeLayout stuff;

        public Viewholder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.stuff_title);
            source = itemView.findViewById(R.id.stuff_author);
            date = itemView.findViewById(R.id.stuff_date);
            stuff = itemView.findViewById(R.id.stuff);
        }
    }

    public class DiffCallback extends DiffUtil.Callback {
        private List<Stuff> mOld, mNew;

        public DiffCallback(List<Stuff> mOld, List<Stuff> mNew) {
            this.mOld = mOld;
            this.mNew = mNew;
        }

        @Override
        public int getOldListSize() {
            return mOld.size();
        }

        @Override
        public int getNewListSize() {
            return mNew.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOld.get(oldItemPosition).getId().equals(mNew.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Stuff oldItem = mOld.get(oldItemPosition);
            Stuff newItem = mNew.get(newItemPosition);
            return !oldItem.getType().equals(newItem.getType())
                    && !oldItem.getDesc().equals(newItem.getDesc())
                    && !oldItem.getUrl().equals(newItem.getUrl());
        }
    }
}
