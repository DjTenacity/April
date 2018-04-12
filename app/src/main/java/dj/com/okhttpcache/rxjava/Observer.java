package dj.com.okhttpcache.rxjava;

import io.reactivex.annotations.NonNull;

/**
 * 观察者
 */
public interface Observer<T> {
    void onSubscribe();
    void onNext(@NonNull T item);
    void onError(@NonNull Throwable e);
    void onComplete();
}
