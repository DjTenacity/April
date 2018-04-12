package dj.com.okhttpcache.rxregin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;



public class RxLoginActivity extends Activity implements UMAuthListener {
    public static final String PLATFORM_KEY = "PLATFORM_KEY";
    private UMShareAPI mUmShareAPI;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUmShareAPI = UMShareAPI.get(this);
        RxLoginPlatform platform = (RxLoginPlatform) getIntent().getSerializableExtra(PLATFORM_KEY);
        mUmShareAPI.deleteOauth(this,platformChange(platform),null);
        mUmShareAPI.getPlatformInfo(this, platformChange(platform), this);
    }

    /**
     * 平台转换
     * @param platform
     * @return
     */
    private SHARE_MEDIA platformChange(RxLoginPlatform platform) {
        switch (platform) {
            case Platform_QQ:
                return SHARE_MEDIA.QQ;
            case Platform_WX:
                return SHARE_MEDIA.WEIXIN;
        }
        return SHARE_MEDIA.QQ;
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {
        RxLogin.STATIC_LISTENER.onStart(share_media);
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        // 结果要传回去
        RxLogin.STATIC_LISTENER.onComplete(share_media,i,map);
        finish();
        overridePendingTransition(0,0);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        RxLogin.STATIC_LISTENER.onError(share_media,i,throwable);
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        RxLogin.STATIC_LISTENER.onCancel(share_media,i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
