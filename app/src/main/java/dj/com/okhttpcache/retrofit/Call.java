package dj.com.okhttpcache.retrofit;

public interface Call<T> {
    void enqueue(Callback<T> callback);
}
