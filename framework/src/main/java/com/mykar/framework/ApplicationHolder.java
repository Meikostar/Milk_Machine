package com.mykar.framework;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mykar.framework.ui.view.image.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.mykar.framework.ui.view.image.universalimageloader.core.ImageLoader;
import com.mykar.framework.ui.view.image.universalimageloader.core.ImageLoaderConfiguration;
import com.mykar.framework.ui.view.image.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by rolandxu on 15/4/23.
 */
public class ApplicationHolder {
    private static final String TAG = "ApplicationHolder";
    private static Application mApplication = null;

    /**
     * 获得Application
     *
     * @return the mapplication
     */
    public static Application getmApplication()
    {
        if (mApplication == null)
        {
            Log.e(TAG, "Global ApplicationContext is null, Please call ApplicationHolder.setmApplication(application) at the onCreate() method of Activity and Application");
        }else {

        }
        return mApplication;
    }

    /**
     * 设置Application
     *
     * @param mapplication
     *            the mapplication to set
     */
    public static void setmApplication(Application mapplication)
    {
        if (mapplication == null)
        {
            Log.e(TAG, "try to set null application, return");
            return;
        }
        if (mapplication != mApplication)
        {
            mApplication = mapplication;
            initImageLoader(mApplication);
        }

    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
//          ImageLoaderConfiguration.createDefault(this);
        // method.
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.memoryCacheSize(cacheSize);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 10 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
//        config.memoryCache(new WeakMemoryCache()).threadPoolSize(1);
        config.memoryCacheExtraOptions(480, 800);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());

    }
}
