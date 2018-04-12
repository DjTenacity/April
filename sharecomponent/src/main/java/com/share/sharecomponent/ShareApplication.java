package com.darren.sharecomponent;

import android.app.Application;
import android.content.Context;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * Created by Administrator on 2017/6/15.
 */

public class ShareApplication extends Application {
    private static Context application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        initAppKey();
    }

    /**
     * 初始化第三方分享和登录的AppKey
     */
    private static void initAppKey() {
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
        PlatformConfig.setSinaWeibo("3921700954", "04b48b094faeb16683c32669824ebdad", "http://sns.whalecloud.com");
        UMShareAPI.get(application);
    }

    public static void attach(Context context) {
        application = context;
        initAppKey();
    }
}
