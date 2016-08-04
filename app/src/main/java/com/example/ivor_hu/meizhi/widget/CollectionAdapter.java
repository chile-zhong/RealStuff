package com.example.ivor_hu.meizhi.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.example.ivor_hu.meizhi.R;
import com.example.ivor_hu.meizhi.base.StuffBaseAdapter;
import com.example.ivor_hu.meizhi.db.Stuff;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by ivor on 16-6-21.
 */
public class CollectionAdapter extends StuffBaseAdapter {
    private static final String TAG = "CollectionAdapter";

    public CollectionAdapter(Context context, Realm realm, String type) {
        super(context, realm, type);
        mStuffs.addChangeListener(new RealmChangeListener<RealmResults<Stuff>>() {
            @Override
            public void onChange(RealmResults<Stuff> element) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initStuffs(Realm realm, String mType) {
        mStuffs = Stuff.collections(realm);
    }

    @Override
    protected void bindColBtn(ImageButton likeBtn, final int position) {
        likeBtn.setImageResource(R.drawable.like);
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position);
            }
        });
    }

    private void deleteItem(final int position) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Stuff stuff = mStuffs.get(position);
                if (stuff.isDeleted())
                    stuff.deleteFromRealm();
                else
                    stuff.setLiked(false);
            }
        });
    }
}
