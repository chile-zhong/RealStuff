package com.example.ivor_hu.meizhi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.ivor_hu.meizhi.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Ivor on 2016/2/6.
 */
public class CommonUtil {
    private CommonUtil(){}

    public static void toast(Context context, String str, int lengthShort) {
        Toast.makeText(context, str, lengthShort).show();
    }

    public static void makeSnackBar(View view, String str, int length) {
        final Snackbar snackbar = Snackbar.make(view, str, length);
        snackbar.show();
    }

    public static void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 通过md5 生成图片对应的key
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
     * 将二进制数组转换成十六进制
     *
     * @param digest 二进制数组
     * @return 十六进制字符串
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
