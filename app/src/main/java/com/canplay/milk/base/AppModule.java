package com.canplay.milk.base;

import android.content.Context;

import com.canplay.milk.base.manager.ApiManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by peter on 2016/9/11.
 */
@Module
public class AppModule {
    private static final String ENDPOINT = "";

    private final BaseApplication application;

    public AppModule(BaseApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public ApiManager provideApiManager() {
        return ApiManager.getInstance(application, true);
    }

}
