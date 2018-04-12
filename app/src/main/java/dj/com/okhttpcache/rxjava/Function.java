package dj.com.okhttpcache.rxjava;

/**
 * Created by Administrator on 2018\4\8 0008.
 */

public interface Function<T,S> {
    S apply(T t) throws Exception;
}
