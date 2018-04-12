package dj.com.okhttpcache.rxregin;

import java.util.Map;


public class RxLoginResult {
    // 是否成功
    private boolean isSucceed;
    private String msg;
    private Map<String,String> userInfoMaps;
    private RxLoginPlatform platform;

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, String> getUserInfoMaps() {
        return userInfoMaps;
    }

    public void setUserInfoMaps(Map<String, String> userInfoMaps) {
        this.userInfoMaps = userInfoMaps;
    }

    public RxLoginPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(RxLoginPlatform platform) {
        this.platform = platform;
    }
}
