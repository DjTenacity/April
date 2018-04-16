package dj.com.okhttpcache;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import dj.com.okhttpcache.download.DownloadCallback;
import dj.com.okhttpcache.download.DownloadFacade;
import dj.com.okhttpcache.interceptor.CacheRequestInterceptor;
import dj.com.okhttpcache.interceptor.CacheResponseInterceptor;
import dj.com.okhttpcache.retrofit.simple.RetrofitClient;
import dj.com.okhttpcache.retrofit.simple.UserLoginResult;
import dj.com.okhttpcache.rxregin.RxLogin;
import dj.com.okhttpcache.rxregin.RxLoginPlatform;
import dj.com.okhttpcache.rxregin.RxLoginResult;
import dj.com.sharelib.ShareApplication;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 有三个类需要用户去关注，后面我们有可能会自己去更新代码，用户就需要换调用方式
        // 调用的方式 门面
        DownloadFacade.getFacade().init(this);

        DownloadFacade.getFacade()
                .startDownload("http://acj3.pc6.com/pc6_soure/2017-11/com.ss.android.essay.joke_664.apk", new DownloadCallback() {

                    @Override
                    public void onFailure(IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onSucceed(File file) {
                        installFile(file);
                    }

                });


        // 多线断点下载，只要客户端做一下处理就可以了
        // 什么叫做断点续传，逻辑是什么？
        // 如果下载中断（网络断开，程序退出），下次可以接着上次的地方下载
        // 多线程的逻辑是什么？多个线程读后台文件每个线程只负责读取单独的内容

        // 文件更新 ，专门下载apk软件（应用宝，迅雷，百度云）

        // 文件更新，1. 可以直接跳转到浏览器更新，2.直接下载不断点，也不多线程（OkHttp）3.多线程 4. 多线程加断点

        // 专门下载apk软件：多线程 + 断点，最多只能同时下载几个文件，一些正在下载，一些暂停，一些准备，参考 OKHttp 源码 Dispatch 的逻辑

        // 4. 多线程加断点
        /*OkHttpManager okHttpManager = new OkHttpManager();
        Call call = okHttpManager.asyncCall("http://acj3.pc6.com/pc6_soure/2017-11/com.ss.android.essay.joke_664.apk");

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 不断的读写文件，单线程
                InputStream inputStream = response.body().byteStream();

                File file = new File(getCacheDir(),"x02345.apk");

                OutputStream outputStream = new FileOutputStream(file);

                int len = 0;
                byte[] buffer = new byte[1024*10];

                while ((len = inputStream.read(buffer))!=-1){
                    outputStream.write(buffer,0,len);
                }

                inputStream.close();
                outputStream.close();

                installFile(file);
            }
        });*/

        // 断点续传，需要服务器配合，思路跟断点下载类似
    }




    private void installFile(File file) {
        // 核心是下面几句代码
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
    }



    //++++++++++++++++++++++++++++++


    OkHttpClient mHttpClient;

    void myCatch() {
        // 自定义缓存（要求：有网 30s 内请求读缓存，无网直接读缓存）
        // OkHttp 自带的扩展有坑，我们之前自己写过这个缓存管理，与 OkHttp 结合就可以了

        // 思路？拦截器?分为两种
        File file = new File(Environment.getExternalStorageDirectory(), "cache");
        Cache cache = new Cache(file, 100 * 1024 * 1024);
          mHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                // 加载最前 过期时间缓存多少秒
                .addInterceptor(new CacheRequestInterceptor(this))
                // 加载最后,数据缓存 过期时间 30s
                .addNetworkInterceptor(new CacheResponseInterceptor())
                .build();

        // rxPermission
        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            // 权限申请，并且用户给了权限
                            uploadFile();
                        }
                    }
                });
    }

    public void click(View view) {
        String url = "https://api.saiwuquan.com/api/appv2/sceneModel";
        // 构建一个请求
        final Request request = new Request.Builder()
                .url(url).build();
        // new RealCall 发起请求
        Call call = mHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", response.body().string());
                // 都是有 第一把，第二把没有网络的了只有缓存的 (30s 以内)，过了 30s 之后又会有网络的了（会再请求更新）
                Log.e("TAG", response.cacheResponse() + " ; " + response.networkResponse());
            }
        });
    }


    //=====================================
    private void uploadFile() {
        // 这个是 Okhttp 上传文件的用法
        String url = "https://api.saiwuquan.com/api/upload";
        File file = new File(Environment.getExternalStorageDirectory(), "test.apk");
        OkHttpClient httpClient = new OkHttpClient();
        // 构建请求 Body , 这个我们之前自己动手写过
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        builder.addFormDataPart("platform", "android");
        builder.addFormDataPart("file", file.getName(),
                RequestBody.create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));

        ExMultipartBody exMultipartBody = new ExMultipartBody(builder.build()
                , new UploadProgressListener() {

            @Override
            public void onProgress(long total, long current) {
                showToast(total, current);
            }
        });

        // 怎么监听上传文件的进度？

        // 构建一个请求
        final Request request = new Request.Builder()
                .url(url)
                .post(exMultipartBody).build();
        // new RealCall 发起请求
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("TAG", response.body().string());
            }
        });
    }

    private void showToast(final long total, final long current) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this,current+"/"+total,Toast.LENGTH_LONG).show();
            }
        });
    }

    private String guessMimeType(String filePath) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();

        String mimType = fileNameMap.getContentTypeFor(filePath);

        if (TextUtils.isEmpty(mimType)) {
            return "application/octet-stream";
        }
        return mimType;
    }



    void share(){
        ShareApplication.attach(this);

       View mClearContent = findViewById(R.id.tv);
        RxView.clicks(mClearContent).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                RxLogin.create(MainActivity.this)
                        .doOauthVerify(RxLoginPlatform.Platform_QQ)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<RxLoginResult>() {
                            @Override
                            public void accept(RxLoginResult rxLoginResult) throws Exception {
                                if(rxLoginResult.isSucceed()){
                                    // 怎么进来
                                }
                                Toast.makeText(MainActivity.this,rxLoginResult.getMsg(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    void myRetrofit(){

        RetrofitClient.getServiceApi().userLogin("fff", "1111111")
                .enqueue(new dj.com.okhttpcache.retrofit.Callback<UserLoginResult>() {
                    @Override
                    public void onResponse(dj.com.okhttpcache.retrofit.Call<UserLoginResult> call, dj.com.okhttpcache.retrofit.Response<UserLoginResult> response) {
                        Log.e("TAG",response.body.toString());
                    }

                    @Override
                    public void onFailure(dj.com.okhttpcache.retrofit.Call<UserLoginResult> call, Throwable t) {

                    }
                });
    }

}
