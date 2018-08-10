package com.arms.perfect.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.arms.perfect.common.BaseConfig;
import com.arms.perfect.util.FileUtil;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GifTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.io.File;

/**
 * @Author: Joe liuzhaojava@foxmail.com
 * @Date: 2018/3/10 0:46
 * @Description: 使用Glide框架加载图片
 */
public class GlideManager implements ILoader {

    private static class GlideLoaderHolder {
        public static GlideManager instance = new GlideManager();
    }

    public static GlideManager getInstance() {
        return GlideLoaderHolder.instance;
    }

    @Override
    public void loadNet(ImageView target, String url, Options options) {
        load(getRequestManager(target.getContext()).load(url), target, options);
    }

    @Override
    public void loadResource(ImageView target, int resId, Options options) {
        load(getRequestManager(target.getContext()).load(resId), target, options);
    }

    @Override
    public void loadAssets(ImageView target, String assetName, Options options) {
        load(getRequestManager(target.getContext()).load("file:///android_asset/" + assetName), target, options);
    }

    @Override
    public void loadFile(ImageView target, File file, Options options) {
        load(getRequestManager(target.getContext()).load(file), target, options);
    }

    @Override
    public void clearMemoryCache(Context context) {
        Glide.get(context).clearMemory();
    }

    @Override
    public void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

    @Override
    public void loadGifImage(ImageView target, String url, Options options) {
        load(getRequestManager(target.getContext()).load(url).asGif(), target, options);
    }

    @Override
    public void loadGifImage(ImageView target, int res, Options options) {
        load(getRequestManager(target.getContext()).load(res).asGif(), target, options);
    }

    @Override
    public void loadRoundCornersImage(final ImageView target, String url, Options options) {
        getRequestManager(target.getContext())
                .load(url)
                .asBitmap()
                .into(new BitmapImageViewTarget(target) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(target.getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        target.setImageDrawable(circularBitmapDrawable);
                        target.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                });

    }

    @Override
    public void loadCircleImage(final ImageView target, String url, Options options, int cornerSize) {
        getRequestManager(target.getContext()).load(url)
                .transform(new GlideRoundTransform(target.getContext(), cornerSize))
                .into(target);
    }

    @Override
    public void clearCache(Context context) {
        clearDiskCache(context);
        clearMemoryCache(context);
        try {
            String path = FileUtil.getExternalCacheDir(context);
            FileUtil.deleteFile(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCacheSize(Context context) {
        String size;
        try {
            String fileName = FileUtil.getExternalCacheDir(context) + "/" + BaseConfig.CACHE_DISK_DIR;
            size = FileUtil.getFormatSize(FileUtil.getFolderSize(new File(fileName)));
            return size;
        } catch (Exception e) {
            e.printStackTrace();
            return "0.00B";
        }
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    @Override
    public long getFolderSize(File file){
        return FileUtil.getFolderSize(file);
    }

    private RequestManager getRequestManager(Context context) {
        return Glide.with(context);
    }

    private void load(GifTypeRequest request, ImageView target, Options options) {
        if (options == null) options = Options.defaultOptions();

        if (options.loadingResId != Options.RES_NONE) {
            request.placeholder(options.loadingResId);
        }
        if (options.loadErrorResId != Options.RES_NONE) {
            request.error(options.loadErrorResId);
        }
        request.crossFade().into(target);
    }

    private void load(DrawableTypeRequest request, ImageView target, Options options) {
        if (options == null) options = Options.defaultOptions();

        if (options.loadingResId != Options.RES_NONE) {
            request.placeholder(options.loadingResId);
        }
        if (options.loadErrorResId != Options.RES_NONE) {
            request.error(options.loadErrorResId);
        }
        request.crossFade().into(target);
    }

}
