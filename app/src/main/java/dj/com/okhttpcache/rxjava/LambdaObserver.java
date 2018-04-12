package dj.com.okhttpcache.rxjava;

/**
 * Created by Administrator on 2018\4\8 0008.
 */

public class LambdaObserver<T> implements Observer<T> {
    Consumer<T> onNext;

    public LambdaObserver(Consumer<T> onNext) {
        this.onNext = onNext;
    }
    //静态代理
    @Override
    public void onSubscribe() {

    }

    @Override
    public void onNext(T item) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }

}
