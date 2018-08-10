package com.arms.perfect.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;

/**
 * Created by liuzhao on 2018/8/7 17:03.
 * description:文件/文件夹工具类
 */
public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    /**
     * SD卡是否能用
     *
     * @return true 可用,false不可用
     */
    public static boolean isSDCardAvailable() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            Logger.e(TAG, "isSDCardAvailable : SD卡不可用!", e);
            return false;
        }
    }

    /**
     * 创建一个文件夹, 存在则返回, 不存在则新建
     *
     * @param parentDirectory 父目录路径
     * @param directory       目录名
     * @return 文件，null代表失败
     */
    public static File generateDirectory(String parentDirectory, String directory) {
        if (TextUtils.isEmpty(parentDirectory) || TextUtils.isEmpty(directory)) {
            Log.e(TAG, "generateFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        File file = new File(parentDirectory, directory);
        boolean flag;
        if (!file.exists()) {
            flag = file.mkdir();//只能用来创建文件夹，且只能创建一级目录，如果上级不存在，就会创建失败
        } else {
            flag = true;
        }
        return flag ? file : null;
    }


    /**
     * 创建一个文件, 存在则返回, 不存在则新建
     *
     * @param catalogPath 父目录路径
     * @param name        文件名
     * @return 文件，null代表失败
     */
    public static File generateFile(String catalogPath, String name) {
        if (TextUtils.isEmpty(catalogPath) || TextUtils.isEmpty(name)) {
            Log.e(TAG, "generateFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(catalogPath, name);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();//只能用来创建文件，且只能在已存在的目录下创建文件，否则会创建失败
            } catch (IOException e) {
                Log.e(TAG, "generateFile : 创建" + catalogPath + "目录下的文件" + name + "文件失败!", e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /**
     * 根据全路径创建一个文件
     *
     * @param filePath 文件全路径
     * @return 文件，null代表失败
     */
    public static File generateFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG, "generateFile : 创建失败, 文件目录或文件名为空, 请检查!");
            return null;
        }
        boolean flag;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                flag = file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "generateFile : 创建" + file.getName() + "文件失败!", e);
                flag = false;
            }
        } else {
            flag = true;
        }
        return flag ? file : null;
    }

    /***
     * 获取指定文件夹内所有文件大小的和
     * @param file
     * @return
     */
    public static long getFolderSize(File file){
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除文件/文件夹
     * 如果是文件夹，则会删除其下的文件以及它本身
     *
     * @param file file
     * @return true代表成功删除
     */
    public static boolean deleteFile(File file) {
        if (file == null) {
            return true;
        }
        if (!file.exists()) {
            return true;
        }
        boolean result = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files) {
                for (File subFile : files) {
                    if (subFile.isDirectory()) {
                        if (!deleteFile(subFile)) {
                            result = false;
                        }
                    } else {
                        if (!subFile.delete()) {
                            result = false;
                        }
                    }
                }
            }
        }
        if (!file.delete()) {
            result = false;
        }

        return result;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //返回"/data"目录
    public static String getDataDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    //返回"/storage/emulated/0"目录
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    //返回"/system"目录
    public static String getRootDirectory() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    //返回"/cache"目录
    public static String getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    /**
     * @param type 所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量
     * @return 返回"/storage/emulated/0/xxx"目录
     * 例如传入Environment.DIRECTORY_ALARMS则返回"/storage/emulated/0/Alarms"
     */
    public static String getExternalStoragePublicDirectory(String type) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
        //返回的目录有可能不存在
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    //返回"/data/user/0/com.xxx.xxx/cache"目录
    public static String getCacheDir(Context mContext) {
        return mContext.getCacheDir().getAbsolutePath();
    }

    //返回"/data/user/0/com.xxx.xxx/files"目录
    public static String getFilesDir(Context mContext) {
        return mContext.getFilesDir().getAbsolutePath();
    }

    //返回"/storage/emulated/0/Android/data/com.xxx.xxx/cache"目录
    public static String getExternalCacheDir(Context mContext) {
        return mContext.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * @param type 所放的文件的类型，传入的参数是Environment类中的DIRECTORY_XXX静态变量
     * @return 返回"/storage/emulated/0/Android/data/com.xxx.xxx/files/Alarms"目录
     * 例如传入Environment.DIRECTORY_ALARMS则返回"/storage/emulated/0/Android/data/com.xxx.xxx/files/Alarms"
     */
    public static String getExternalFilesDir(Context mContext,String type) {
        File file = mContext.getExternalFilesDir(Environment.DIRECTORY_ALARMS);
        //返回的目录有可能不存在
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
    /***
     *  格式化文件大小的单位
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "B";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    public static File getFileByUri(Uri uri,Context mContext) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = mContext.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.MediaColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{BaseColumns._ID, MediaStore.MediaColumns.DATA},
                        buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                if (cursor != null) {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
                            .moveToNext()) {
                        index = cursor.getColumnIndex(BaseColumns._ID);
                        index = cursor.getInt(index);
                        dataIdx = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                        path = cursor.getString(dataIdx);
                    }
                    cursor.close();
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = {MediaStore.MediaColumns.DATA};
            Cursor cursor = mContext.getContentResolver().query(uri, proj, null,
                    null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor
                            .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    path = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            if (path != null) {
                return new File(path);
            }
        }
        return null;
    }
    public static Bitmap GetLocalOrNetBitmap(String url)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try
        {
            in = new BufferedInputStream(new URL(url).openStream(), 2*1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 2*1024);
            copy(in, out);
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        byte[] b = new byte[2*1024];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }
}
