package com.example.ivor_hu.meizhi.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;
import com.example.ivor_hu.meizhi.MainActivity;
import com.example.ivor_hu.meizhi.db.Stuff;
import com.example.ivor_hu.meizhi.utils.Constants;
import com.example.ivor_hu.meizhi.utils.DateUtil;
import com.example.ivor_hu.meizhi.utils.VolleyUtil;
import com.example.ivor_hu.meizhi.widget.StuffFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

    private String type, latestUrl, typeName;
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
        latestUrl = MainActivity.TYPE.valueOf(type).getLatestUrl();
        typeName = MainActivity.TYPE.valueOf(type).getApiName();
        Log.d(TAG, "onHandleIntent: " + type + " " + latestUrl + " " + typeName);

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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


    private int fetchLatest(final Realm realm) throws InterruptedException, ExecutionException, ParseException, JSONException {
        RequestFuture<JSONObject> future = VolleyUtil.getInstance(this).getJSONSync(latestUrl, type);

        int fetched = 0;
        JSONObject response = future.get();
        if (response.getBoolean("error"))
            return 0;

        JSONArray array = response.getJSONArray("results");
        int len = array.length();
        JSONObject androidObj;
        String url, date, id, author, title, typeLocal;
        Stuff stuff;
        for (int i = 0; i < len; i++) {
            androidObj = array.getJSONObject(i);
            url = androidObj.getString("url");
            date = androidObj.getString("publishedAt");
            id = androidObj.getString("_id");
            author = androidObj.getString("who");
            title = androidObj.getString("desc");
            typeLocal = MainActivity.TYPE.getTypeFromAPIName(androidObj.getString("type"));

            stuff = new Stuff(id, typeLocal, title, url, author, DateUtil.parse(date));
            if (!saveToDb(realm, stuff)) {
                return i;
            }
            fetched++;
        }

        return fetched;
    }

    private int fetchRefresh(Realm realm, Date publishedAt) throws InterruptedException, ExecutionException, ParseException, JSONException {
        String after = DateUtil.format(publishedAt);
        List<String> dates = DateUtil.generateSequenceDateTillToday(publishedAt);
        return fetch(realm, after, dates);
    }

    private int fetchMore(Realm realm, Date publishedAt) throws InterruptedException, ExecutionException, JSONException, ParseException {
        String before = DateUtil.format(publishedAt);
        List<String> dates = DateUtil.generateSequenceDateBefore(publishedAt, 10);
        return fetch(realm, before, dates);
    }

    private int fetch(Realm realm, String after, List<String> dates) throws InterruptedException, ExecutionException, JSONException, ParseException {
        int fetched = 0;
        for (String date : dates) {
            if (date == null)
                return fetched;

            if (date.equals(after))
                continue;

            RequestFuture<JSONObject> stuffFuture = VolleyUtil.getInstance(this).getJSONSync(Constants.DAYLY_DATA_URL + date, type);

            JSONObject imgResponse = stuffFuture.get();
            if (imgResponse.getBoolean("error"))
                continue;

            JSONObject results = imgResponse.getJSONObject("results");
            if (!results.has(typeName))
                continue;

            JSONArray stuffs = results.getJSONArray(typeName);
            int len = stuffs.length();
            for (int i = 0; i < len; i++) {
                JSONObject stuff = stuffs.getJSONObject(i);
                if (stuff == null)
                    continue;

                if (!saveToDb(realm, new Stuff(
                        stuff.getString("_id"),
                        MainActivity.TYPE.getTypeFromAPIName(stuff.getString("type")),
                        stuff.getString("desc"),
                        stuff.getString("url"),
                        stuff.getString("who"),
                        DateUtil.parse(stuff.getString("publishedAt"))))) {
                    return fetched;
                }
                fetched++;
            }

        }
        return fetched;
    }

    private boolean saveToDb(Realm realm, Stuff stuff) {
        realm.beginTransaction();

        try {
            realm.copyToRealm(stuff);
            Log.d(TAG, "saveToDb: " + stuff.getPublishedAt());
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch image", e);
            realm.cancelTransaction();
            return false;
        }

        realm.commitTransaction();
        return true;
    }
}
