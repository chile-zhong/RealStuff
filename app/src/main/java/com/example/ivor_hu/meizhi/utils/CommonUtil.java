package com.example.ivor_hu.meizhi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ivor on 2016/2/6.
 */
public class CommonUtil {
    private CommonUtil() {
    }

    /**
     * Show toast
     *
     * @param context
     * @param str
     * @param lengthShort
     */
    public static void toast(Context context, String str, int lengthShort) {
        Toast.makeText(context, str, lengthShort).show();
    }

    /**
     * Show snackbar
     *
     * @param parentView
     * @param str
     * @param length
     */
    public static void makeSnackBar(View parentView, String str, int length) {
        final Snackbar snackbar = Snackbar.make(parentView, str, length);
        snackbar.show();
    }

    /**
     * Open URL using system browser
     *
     * @param context
     * @param url
     */
    public static void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Generate md5 key for picture
     *
     * @param imagePath 图片路径
     * @return 图片对应的key
     */
    public static String keyForImage(String imagePath) {

        String key = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("md5");
            messageDigest.update(imagePath.getBytes());
            key = byteToHex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * Transform binary to hex
     *
     * @param digest
     * @return
     */
    private static String byteToHex(byte[] digest) {

        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }
        return builder.toString();
    }
}
