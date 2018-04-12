package dj.com.okhttpcache.rxjava;



public interface ObservableSource<T> {
    void subscribe(Observer<T> observer);
}
