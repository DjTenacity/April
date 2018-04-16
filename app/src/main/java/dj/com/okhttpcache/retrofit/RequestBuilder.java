package dj.com.okhttpcache.retrofit;


import okhttp3.HttpUrl;
import okhttp3.Request;


public class RequestBuilder {
    ParameterHandler<Object>[] parameterHandlers;
    Object[] args;
    HttpUrl.Builder httpUrl;
    public RequestBuilder(String baseUrl, String relativeUrl, String httpMethod, ParameterHandler<?>[] parameterHandlers, Object[] args) {
        this.parameterHandlers = (ParameterHandler<Object>[]) parameterHandlers;
        this.args = args;
        httpUrl = HttpUrl.parse(baseUrl+relativeUrl).newBuilder();
    }

    public Request build() {
        int count = args.length;
        for (int i=0;i < count;i++) {
            // userName = dj
            parameterHandlers[i].apply(this,args[i]);
        }
        // POST 等等
        Request request = new Request.Builder().url(httpUrl.build()).build();
        return request;
    }

    public void addQueryName(String key, String value) {
        // userName = dj&password = 940223
        httpUrl.addQueryParameter(key,value);
    }
    // 其他
}
