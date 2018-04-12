package dj.com.okhttpcache.rxjava;

import android.graphics.Bitmap;

/**
 * 被观察者
 */

public abstract class Observable<T> implements ObservableSource<T> {

    public static <T> Observable<T> just(T item) {
        return onAssembly(new ObservableJust<T>(item));
    }

    private static <T> Observable<T> onAssembly(Observable<T> source) {
        // 留出来了
        return source;
    }

    @Override
    public void subscribe(Observer<T> observer) {
        subscribeActual(observer);
    }

    public void subscribe(Consumer<T> onNext) {
        subscribe(onNext, null, null);

    }

    private void subscribe(Consumer<T> onNext, Consumer<T> error, Consumer<T> complete) {
        //
        // subscribe(onNext,error,complete);
        subscribe(new LambdaObserver<T>(onNext));
    }


    protected abstract void subscribeActual(Observer<T> observer);

    ///==============
    public <R> Observable<R> map(Function<T,R>  function) {

        //this  就是被代理的对象
        return  onAssembly(new ObservableMap <>(this,function));
    }


    public Observable<T> subscribeOn(Schedulers schedulers) {
        return onAssembly(new ObservableSchedulers(this,schedulers));
    }

    public Observable<T> observerOn(Schedulers schedulers) {
        return onAssembly(new ObserverOnObservable(this,schedulers));
    }
}
