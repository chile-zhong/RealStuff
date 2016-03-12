package com.example.ivor_hu.meizhi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ivor on 2016/1/27.
 */
public class PicUtil {
    public static final String TAG = "PicUtil";
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    public static final int SAVE_DONE_TOAST = 666;
    public static final String IMAGE_SAVE_PATH = "/Meizhi/";
    public static final String FILEPATH = "file_path";

    private PicUtil() {
    }

    /**
     * 获取SDCard的目录路径功能
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdcardDir = null;
        // 判断SDCard是否存在
        boolean sdcardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdcardExist) {
            sdcardDir = Environment.getExternalStorageDirectory();
        } else {
            sdcardDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        return sdcardDir.toString();
    }

    public static File saveBitmapToSDCard(Context context, Bitmap bitmap, String url, Handler handler) {
        // 图片存储路径
        String SavePath = getSDCardPath() + IMAGE_SAVE_PATH;
        // 保存Bitmap
        try {
            File path = new File(SavePath);
            if (!path.exists()) {
                path.mkdirs();
            }
            // 文件
            String filepath = SavePath + getLastStringFromUrl(url);
            File file = new File(filepath);
            if (!file.exists()) {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    //申请WRITE_EXTERNAL_STORAGE权限
//                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
//                }else {
                    file.createNewFile();
//                }
            }
            FileOutputStream fos = new FileOutputStream(file);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Message message = new Message();
                message.arg1 = SAVE_DONE_TOAST;
                Bundle bundle = new Bundle();
                bundle.putString(FILEPATH, filepath);
                message.setData(bundle);
                handler.sendMessage(message);
                Log.i(TAG, "saveBitmapToSDCard: " + filepath);
                return file;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getLastStringFromUrl(String url) {
        String[] splitStrs = url.split("\\/");
        return splitStrs[splitStrs.length - 1];
    }

    public static void saveBitmapFromUrl(Context context, String url, Handler handler) throws ExecutionException, InterruptedException {
        Bitmap bitmap = Glide.with(context)
                .load(url).asBitmap()
                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get();
        PicUtil.saveBitmapToSDCard(context, bitmap, url, handler);
    }

    public static String getImgPathFromUrl(String url) {
        return getSDCardPath() + IMAGE_SAVE_PATH + getLastStringFromUrl(url);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//    }
}
