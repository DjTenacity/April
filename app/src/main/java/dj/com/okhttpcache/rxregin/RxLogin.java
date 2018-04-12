package dj.com.okhttpcache.rxregin;

import android.app.Activity;
import android.content.Intent;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;



public class RxLogin implements UMAuthListener {
    private Activity activity;
    private RxLoginResult mRxLoginResult;
    static UMAuthListener STATIC_LISTENER;
    private PublishSubject<RxLoginResult> mEmitter;

    private RxLogin(Activity activity){
        this.activity = activity;
        STATIC_LISTENER = this;
        mRxLoginResult = new RxLoginResult();
        mEmitter = PublishSubject.create();
    }

    public static RxLogin create(Activity activity){
        return new RxLogin(activity);
    }

    public Observable<RxLoginResult> doOauthVerify(RxLoginPlatform platform) {
        // 开启一个透明的 Activity ，参照 RxPermission 源码
        Intent intent = new Intent(activity,RxLoginActivity.class);
        intent.putExtra(RxLoginActivity.PLATFORM_KEY,platform);
        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
        // 设置平台
        mRxLoginResult.setPlatform(platform);

        List<Observable<RxLoginResult>> list = new ArrayList<>(1);
        list.add(mEmitter);
        return Observable.concat(Observable.fromIterable(list));
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        mRxLoginResult.setSucceed(true);
        mRxLoginResult.setUserInfoMaps(map);
        mRxLoginResult.setMsg("获取用户信息成功");
        // 拿到信息要回调回去 Activity -> Consumer -> accept
        mEmitter.onNext(mRxLoginResult);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        mRxLoginResult.setSucceed(false);
        mEmitter.onNext(mRxLoginResult);
        mRxLoginResult.setMsg("获取用户信息失败");
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        mRxLoginResult.setSucceed(false);
        mRxLoginResult.setMsg("用户取消第三方登录");
        mEmitter.onNext(mRxLoginResult);
    }
}
