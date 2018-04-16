package dj.com.okhttpcache.retrofit;

import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import dj.com.okhttpcache.http.POST;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class ServiceMethod {
    final Retrofit retrofit;
    final Method method;
    String httpMethod;
    String relativeUrl;
    final ParameterHandler<?>[] parameterHandlers;

    public ServiceMethod(Builder builder) {
        this.retrofit = builder.retrofit;
        this.method = builder.method;
        this.httpMethod = builder.httpMethod;
        this.relativeUrl = builder.relativeUrl;
        this.parameterHandlers = builder.parameterHandlers;
    }

    public okhttp3.Call createNewCall(Object[] args) {
        // 还需要一个对象，专门用来添加参数的
        RequestBuilder requestBuilder = new RequestBuilder(retrofit.baseUrl, relativeUrl, httpMethod, parameterHandlers, args);
        return retrofit.callFactory.newCall(requestBuilder.build());
    }

    public <T> T parseBody(ResponseBody responseBody) {
        // 获取解析类型 T 获取方法返回值的类型
        Type returnType = method.getGenericReturnType();// 返回值对象
        Class <T> dataClass = (Class <T>) ((ParameterizedType) returnType).getActualTypeArguments()[0];
        // 解析工厂去转换
        Gson gson = new Gson();
        T body = gson.fromJson(responseBody.charStream(),dataClass);
        return body;
    }

    public static class Builder {
        final Retrofit retrofit;
        final Method method;
        final Annotation[] methodAnnotations;
        String httpMethod;
        String relativeUrl;
        Annotation[][] parameterAnnotations;
        final ParameterHandler<?>[] parameterHandlers;

        public Builder(Retrofit retrofit, Method method) {
            this.retrofit = retrofit;
            this.method = method;
            methodAnnotations = method.getAnnotations();
            // 二维数组
            parameterAnnotations = method.getParameterAnnotations();
            parameterHandlers = new ParameterHandler[parameterAnnotations.length];
        }

        public ServiceMethod build() {
            // 解析，OkHttp 请求的时候 ，url = baseUrl + relativeUrl，method
            for (Annotation methodAnnotation : methodAnnotations) {
                // 解析 POST  GET
                parseAnnotationMethod(methodAnnotation);
            }

            // 解析参数注解
            int count = parameterHandlers.length;
            for (int i = 0; i < count; i++) {
                Annotation parameter = parameterAnnotations[i][0];
                // Query 等等
                // 会涉及到一个模板和策略两种设计模式
                if (parameter instanceof Query) {
                    // 一个一个封装成 ParameterHandler ，不同的参数注解选择不同的策略
                    parameterHandlers[i] = new ParameterHandler.Query<>(((Query) parameter).value());
                }
            }
            return new ServiceMethod(this);
        }

        private void parseAnnotationMethod(Annotation methodAnnotation) {
            // value ,请求方法
            if (methodAnnotation instanceof GET) {
                parseMethodAndPath("GET", ((GET) methodAnnotation).value());
            } else if (methodAnnotation instanceof POST) {
                parseMethodAndPath("POST", ((POST) methodAnnotation).value());
            }
            // 还有一大堆解析 if else
        }

        private void parseMethodAndPath(String method, String value) {
            this.httpMethod = method;
            this.relativeUrl = value;
        }
    }
}
