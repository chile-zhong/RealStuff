package com.example.ivor_hu.meizhi.utils;

import com.example.ivor_hu.meizhi.R;

/**
 * Created by Ivor on 2016/2/15.
 */
public final class Constants {
    //    public static final String BASE_URL = "http://gank.avosapps.com/api/";
    public static final String NEW_BASE_URL = "http://gank.io/api/";
    public static final String LATEST_GIRLS_URL = NEW_BASE_URL + "data/%E7%A6%8F%E5%88%A9/10/1";
    public static final String LATEST_ANDROID_URL = NEW_BASE_URL + "data/Android/20/1";
    public static final String LATEST_IOS_URL = NEW_BASE_URL + "data/iOS/20/1";
    public static final String LATEST_APP_URL = NEW_BASE_URL + "data/App/20/1";
    public static final String LATEST_WEB_URL = NEW_BASE_URL + "data/%E5%89%8D%E7%AB%AF/20/1";
    public static final String LATEST_OTHERS_URL = NEW_BASE_URL + "data/%E6%8B%93%E5%B1%95%E8%B5%84%E6%BA%90/20/1";
    public static final String LATEST_FUN_URL = NEW_BASE_URL + "data/%E7%9E%8E%E6%8E%A8%E8%8D%90/20/1";
    //    public static final String BEFORE_DATE_URL = BASE_URL + "get/10/before/";
//    public static final String AFTER_DATE_URL = BASE_URL + "get/20/since/";
//    public static final String DAYLY_DATA_URL = BASE_URL + "day/";
    public static final String DAYLY_DATA_URL = NEW_BASE_URL + "day/";

    public static final String TYPE_COLLECTIONS = "Collections";
    public static final String TYPE_GIRLS = "Grils";
    public static final String TYPE_ANDROID = "Android";
    public static final String TYPE_IOS = "iOS";
    public static final String TYPE_WEB = "Web";
    public static final String TYPE_OTHERS = "Others";
    public static final String TYPE_FUN = "Fun";
    public static final String TYPE_APP = "App";

    public static final String[] TYPES = {TYPE_GIRLS, TYPE_ANDROID, TYPE_IOS, TYPE_WEB, TYPE_APP, TYPE_FUN, TYPE_OTHERS, TYPE_COLLECTIONS};


    private Constants() {
    }

    public static String getLatestUrlFromType(String type) throws IllegalArgumentException {
        String url;
        switch (type) {
            case TYPE_ANDROID:
                url = LATEST_ANDROID_URL;
                break;
            case TYPE_IOS:
                url = LATEST_IOS_URL;
                break;
            case TYPE_APP:
                url = LATEST_APP_URL;
                break;
            case TYPE_FUN:
                url = LATEST_FUN_URL;
                break;
            case TYPE_WEB:
                url = LATEST_WEB_URL;
                break;
            case TYPE_OTHERS:
                url = LATEST_OTHERS_URL;
                break;
            default:
                throw new IllegalArgumentException(type + " is a illegal argument!");
        }
        return url;
    }

    public static String getTypeNameFromType(String type) throws IllegalArgumentException {
        String typeName;
        switch (type) {
            case TYPE_ANDROID:
                typeName = "Android";
                break;
            case TYPE_IOS:
                typeName = "iOS";
                break;
            case TYPE_APP:
                typeName = "App";
                break;
            case TYPE_FUN:
                typeName = "瞎推荐";
                break;
            case TYPE_WEB:
                typeName = "前端";
                break;
            case TYPE_OTHERS:
                typeName = "拓展资源";
                break;
            default:
                throw new IllegalArgumentException(type + " is a illegal argument!");
        }
        return typeName;
    }

    public static String handleTypeStr(String typeInJSON) {
        String type;
        switch (typeInJSON) {
            case "拓展资源":
                type = TYPE_OTHERS;
                break;
            case "前端":
                type = TYPE_WEB;
                break;
            case "福利":
                type = TYPE_GIRLS;
                break;
            case "瞎推荐":
                type = TYPE_FUN;
                break;
            default:
                type = typeInJSON;
                break;
        }
        return type;
    }

    public static int getResIdFromType(String type) throws IllegalArgumentException {
        int resId;
        switch (type) {
            case TYPE_GIRLS:
                resId = R.string.nav_girls;
                break;
            case TYPE_ANDROID:
                resId = R.string.nav_android;
                break;
            case TYPE_IOS:
                resId = R.string.nav_ios;
                break;
            case TYPE_APP:
                resId = R.string.nav_app;
                break;
            case TYPE_FUN:
                resId = R.string.nav_fun;
                break;
            case TYPE_WEB:
                resId = R.string.nav_web;
                break;
            case TYPE_OTHERS:
                resId = R.string.nav_others;
                break;
            case TYPE_COLLECTIONS:
                resId = R.string.nav_collections;
                break;
            default:
                throw new IllegalArgumentException(type + " is a illegal argument!");
        }
        return resId;
    }

    public static String getTypeFromResId(int resId) throws IllegalArgumentException {
        String type;
        switch (resId) {
            case R.id.nav_girls:
                type = TYPE_GIRLS;
                break;
            case R.id.nav_android:
                type = TYPE_ANDROID;
                break;
            case R.id.nav_ios:
                type = TYPE_IOS;
                break;
            case R.id.nav_app:
                type = TYPE_APP;
                break;
            case R.id.nav_fun:
                type = TYPE_FUN;
                break;
            case R.id.nav_web:
                type = TYPE_WEB;
                break;
            case R.id.nav_others:
                type = TYPE_OTHERS;
                break;
            case R.id.nav_collections:
                type = TYPE_COLLECTIONS;
                break;
            default:
                throw new IllegalArgumentException(resId + " is a illegal argument!");
        }
        return type;
    }
}
