package dj.com.okhttpcache.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Administrator on 2018\4\4 0004. CacheRequestInterceptor
 */

public class CacheResponseInterceptor implements Interceptor{
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response =chain.proceed(chain.request());

        //过期时间30s   max-age--->缓存时间,缓存策略
        response = response.newBuilder()
                .removeHeader("Cache-Control")
                .removeHeader("Pragma")
                .addHeader("Cache-Control","max-age="+30).build();

        return response;
    }
}
