package dj.com.okhttpcache.rxjava;

/**
 * 只管上层(上游和下游)   当前节点
 */

public class ObservableMap<T, R> extends Observable<R> {

    private Observable<T> source; //前面的
    private Function<T, R> function; //当前转换

    public ObservableMap(Observable<T> source,Function<T, R> function) {
        this.source = source;
        this.function = function;
    }

    @Override
    protected void subscribeActual(Observer<R> observer) {
        //第一步
        //对observer包裹了一层   静态代理包裹 source永远是上游的 observable对象
        source.subscribe(new MapObserver(observer, function));
    }


    private class MapObserver implements Observer<T>  {
        final Observer<R> observer;
        final Function<T, R> function;

        public MapObserver(Observer<R> source, Function<T, R> function) {
            this.observer = source;
            this.function = function;
        }

        @Override
        public void onSubscribe() {
            observer.onSubscribe();
        }

        @Override
        public void onNext(T item) {//要去转换   item--->String

            R applyR= null;
            try {
                applyR = function.apply(item);
                observer.onNext(applyR);

            } catch (Exception e) {
                 observer.onError(e);
            }
        }

        @Override
        public void onError(Throwable e) {
            observer.onError(e);
        }

        @Override
        public void onComplete() {
            observer.onComplete();
        }
    }
}
