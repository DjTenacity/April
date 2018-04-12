package dj.com.okhttpcache.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dj.com.okhttpcache.R;

/**
 * 事件流
 */
public class RxjavaActivity extends AppCompatActivity {
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark);
        final ImageView image = findViewById(R.id.image);

        // 1.观察者 Observable 被观察对象
        // Observer 观察者
        // subscribe 注册订阅
        /*Observable.just("urlxxx")
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe() {

                    }

                    @Override
                    public void onNext(String item) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                //just方法创建了一个 ObservableJust 对象 并通过onAssembly 方法返回这个对象,
                //ObservableJust 的 subscribe方法是 用观察者和 just() 方法传入的 item参数 来创建一个内部类,然后调用观察者的onSubscribe方法,在通过内部类的方法和 item参数 来执行观察者的其他方法

                //------上面直接是 ObservableJust直接调用方法subscribe,下面是ObservableJust 对象调用map方法 ------

                //ObservableJust 对象调用map方法
                // map方法就是 创建了一个 ObservableMap 对象并且 通过构造函数 传入 Function 对象和调用map方法的Observable对象,  并通过onAssembly 方法返回这个对象

                // ObservableJust ,ObservableMap 都继承了 Observable
                // ObservableMap中有一个MapObserver内部类实现了Observer

                //ObservableMap 的 subscribe方法是调用构造方法中传入的上一层的 Observable 对象的subscribe方法,并创建MapObserver这个观察者对象

                Observable.just("http://img.taopic.com/uploads/allimg/130331/240460-13033106243430.jpg")
                        .map(new Function<String, Bitmap>() {//事件变换
                            @Override
                            public Bitmap apply(String s) throws Exception {
                                URL url = new URL(s);
                                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                                InputStream inputStream = urlConnection.getInputStream();
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                return bitmap;
                            }
                        })   /**map只管这一个节点*/
                        .map(new Function<Bitmap, Bitmap>() {
                            @Override
                            public Bitmap apply(Bitmap s) throws Exception {
                                bitmap = createWatermark(s, "RxJava2.0");
                                return bitmap;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observerOn(Schedulers.mainThread())
                        .subscribe(new Consumer<Bitmap>() {
                            @Override
                            public void onNext(final Bitmap r) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        image.setImageBitmap(r);
                                    }
                                });
                            }
                        })
                ;
            }
        }).start();
    }


    private Bitmap createWatermark(Bitmap bitmap, String mark) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint p = new Paint();
        // 水印颜色
        p.setColor(Color.parseColor("#C5FF0000"));
        // 水印字体大小
        p.setTextSize(150);
        //抗锯齿
        p.setAntiAlias(true);
        //绘制图像
        canvas.drawBitmap(bitmap, 0, 0, p);
        //绘制文字
        canvas.drawText(mark, 0, h / 2, p);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        /**
         * save方法用于临时保存画布坐标系统的状态

         restore方法可以用来恢复save之后设置的状态,
         可以简单理解为调用restore之后，restore方法前调用的rotate/translate/scale方法全部就还原了，画布的坐标系统恢复到save方法之前，
         但是这里要注意的是，restore方法的调用只影响restore之后绘制的内容，对restore之前已经绘制到屏幕上的图形不会产生任何影响。
         * */
        return bmp;
    }
}
