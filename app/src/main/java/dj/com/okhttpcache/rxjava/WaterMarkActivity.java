package dj.com.okhttpcache.rxjava;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import dj.com.okhttpcache.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class WaterMarkActivity extends AppCompatActivity {

    // 给网络上的一张图片加水印，显示到 ImageView 控件上
    private ImageView mImage;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            mImage.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_mark);

        mImage = (ImageView) findViewById(R.id.image);

    }

    void faction1() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://img.taopic.com/uploads/allimg/130331/240460-13033106243430.jpg");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    // 加一个水印
                    bitmap = createWatermark(bitmap, "RxJava2.0");
                    // 显示到图片
                    Message message = Message.obtain();
                    message.obj = bitmap;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    void faction2() {
        // 好处？可读性比较高，一条链子下来的（可读性高），扩展性，维护性，等等
        // 学习成本要高，思想难以转换（事件流）
        Observable.just("http://img.taopic.com/uploads/allimg/130331/240460-13033106243430.jpg")
                .map(new Function<String, Bitmap>() { // 事件变换
                    @Override
                    public Bitmap apply(@NonNull String urlPath) throws Exception {
                        URL url = new URL(urlPath);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = urlConnection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return bitmap;
                    }
                })
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(@NonNull Bitmap bitmap) throws Exception {
                        bitmap = createWatermark(bitmap, "RxJava2.0");
                        return bitmap;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        mImage.setImageBitmap(bitmap);
                    }
                });
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
        return bmp;
    }
}