package com.example.ivor_hu.meizhi.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.ivor_hu.meizhi.MainActivity;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.net.GankAPI;
import com.example.ivor_hu.meizhi.net.GankAPIService;
import com.example.ivor_hu.meizhi.utils.DateUtil;
import com.example.ivor_hu.meizhi.widget.StuffFragment;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Ivor on 2016/3/3.
 */
public class StuffFetchService extends IntentService {
    private static final String TAG = "StuffFetchService";

    public static final String ACTION_UPDATE_RESULT = "com.ivor.meizhi.update_result";
    public static final String EXTRA_FETCHED = "fetched";
    public static final String EXTRA_TRIGGER = "trigger";
    public static final String EXTRA_TYPE = "type";
    public static final String ACTION_FETCH_REFRESH = "com.ivor.meizhi.fetch_refresh";
    public static final String ACTION_FETCH_MORE = "com.ivor.meizhi.fetch_more";

    private String type;
    private LocalBroadcastManager localBroadcastManager;

    public StuffFetchService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        type = intent.getStringExtra(StuffFragment.SERVICE_TYPE);
        Log.d(TAG, "onHandleIntent: " + type);

        Realm realm = Realm.getDefaultInstance();

        RealmResults<Stuff> latest = Stuff.all(realm, type);

        int fetched = 0;
        try {
            if (latest.isEmpty()) {
                fetched = fetchLatest(realm);
                Log.d(TAG, "no latest, fresh fetch");
            } else if (ACTION_FETCH_REFRESH.equals(intent.getAction())) {
                Log.d(TAG, "latest fetch: " + latest.first().getPublishedAt());
                fetched = fetchRefresh(realm, latest.first().getPublishedAt());
            } else if (ACTION_FETCH_MORE.equals(intent.getAction())) {
                Log.d(TAG, "earliest fetch: " + latest.last().getPublishedAt());
                fetched = fetchMore(realm, latest.last().getPublishedAt());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendResult(intent, realm, fetched);
    }

    private void sendResult(Intent intent, Realm realm, int fetched) {
        realm.close();

        Log.d(TAG, "finished fetching, actual fetched " + fetched);

        Intent broadcast = new Intent(ACTION_UPDATE_RESULT);
        broadcast.putExtra(EXTRA_FETCHED, fetched);
        broadcast.putExtra(EXTRA_TRIGGER, intent.getAction());
        broadcast.putExtra(EXTRA_TYPE, type);

        localBroadcastManager.sendBroadcast(broadcast);
    }


    private int fetchLatest(final Realm realm) throws IOException {
        GankAPI.Result<List<Stuff>> result = GankAPIService.getInstance().latestStuff(type, 20).execute().body();

        if (result.error)
            return 0;

        int stuffSize = result.results.size();
        for (int i = 0; i < stuffSize; i++) {
            if (!saveToDb(realm, result.results.get(i)))
                return i;
        }
        return stuffSize;
    }

    private int fetchRefresh(Realm realm, Date publishedAt) throws IOException {
        String after = DateUtil.format(publishedAt);
        List<String> dates = DateUtil.generateSequenceDateTillToday(publishedAt);
        return fetch(realm, after, dates);
    }

    private int fetchMore(Realm realm, Date publishedAt) throws IOException {
        String before = DateUtil.format(publishedAt);
        List<String> dates = DateUtil.generateSequenceDateBefore(publishedAt, 10);
        return fetch(realm, before, dates);
    }

    private int fetch(Realm realm, String after, List<String> dates) throws IOException {
        int fetched = 0;
        if (type.equals(MainActivity.TYPE.ANDROID.getApiName())) {
            for (String date : dates) {
                if (date.equals(after))
                    continue;

                GankAPI.Result<GankAPI.Androids> stuffsResult = GankAPIService.getInstance().dayAndroids(date).execute().body();
                if (stuffsResult.error || null == stuffsResult.results || null == stuffsResult.results.stuffs)
                    continue;

                for (Stuff stuff : stuffsResult.results.stuffs) {
                    if (!saveToDb(realm, stuff))
                        return fetched;

                    fetched++;
                }
            }
        } else if (type.equals(MainActivity.TYPE.IOS.getApiName())) {
            for (String date : dates) {
                if (date.equals(after))
                    continue;

                GankAPI.Result<GankAPI.IOSs> stuffsResult = GankAPIService.getInstance().dayIOSs(date).execute().body();
                if (stuffsResult.error || null == stuffsResult.results || null == stuffsResult.results.stuffs)
                    continue;

                for (Stuff stuff : stuffsResult.results.stuffs) {
                    if (!saveToDb(realm, stuff))
                        return fetched;

                    fetched++;
                }
            }
        } else if (type.equals(MainActivity.TYPE.APP.getApiName())) {
            for (String date : dates) {
                if (date.equals(after))
                    continue;

                GankAPI.Result<GankAPI.Apps> stuffsResult = GankAPIService.getInstance().dayApps(date).execute().body();
                if (stuffsResult.error || null == stuffsResult.results || null == stuffsResult.results.stuffs)
                    continue;

                for (Stuff stuff : stuffsResult.results.stuffs) {
                    if (!saveToDb(realm, stuff))
                        return fetched;

                    fetched++;
                }
            }
        } else if (type.equals(MainActivity.TYPE.FUN.getApiName())) {
            for (String date : dates) {
                if (date.equals(after))
                    continue;

                GankAPI.Result<GankAPI.Funs> stuffsResult = GankAPIService.getInstance().dayFuns(date).execute().body();
                if (stuffsResult.error || null == stuffsResult.results || null == stuffsResult.results.stuffs)
                    continue;

                for (Stuff stuff : stuffsResult.results.stuffs) {
                    if (!saveToDb(realm, stuff))
                        return fetched;

                    fetched++;
                }
            }
        } else if (type.equals(MainActivity.TYPE.OTHERS.getApiName())) {
            for (String date : dates) {
                if (date.equals(after))
                    continue;

                GankAPI.Result<GankAPI.Others> stuffsResult = GankAPIService.getInstance().dayOthers(date).execute().body();
                if (stuffsResult.error || null == stuffsResult.results || null == stuffsResult.results.stuffs)
                    continue;

                for (Stuff stuff : stuffsResult.results.stuffs) {
                    if (!saveToDb(realm, stuff))
                        return fetched;

                    fetched++;
                }
            }
        } else if (type.equals(MainActivity.TYPE.WEB.getApiName())) {
            for (String date : dates) {
                if (date.equals(after))
                    continue;

                GankAPI.Result<GankAPI.Webs> stuffsResult = GankAPIService.getInstance().dayWebs(date).execute().body();
                if (stuffsResult.error || null == stuffsResult.results || null == stuffsResult.results.stuffs)
                    continue;

                for (Stuff stuff : stuffsResult.results.stuffs) {
                    if (!saveToDb(realm, stuff))
                        return fetched;

                    fetched++;
                }
            }
        }

    return fetched;
}

    private boolean saveToDb(Realm realm, Stuff stuff) {
        realm.beginTransaction();

        try {
            realm.copyToRealm(stuff);
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch image", e);
            realm.cancelTransaction();
            return false;
        }

        realm.commitTransaction();
        return true;
    }
}
